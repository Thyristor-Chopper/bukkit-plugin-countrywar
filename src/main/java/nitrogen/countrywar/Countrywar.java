package nitrogen.countrywar;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;

import nitrogen.countrywar.settings.InventoryEvents;
import nitrogen.countrywar.commands.*;
import nitrogen.countrywar.core.*;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;

import com.mojang.brigadier.CommandDispatcher;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Countrywar extends JavaPlugin {
	public static String version;
    public static List<String> pvp;
    public static Map<String, Integer> cooltime;
    public static List<String> country = new ArrayList<>(); // 국가 종류
    public static Map<String, String> req;
    public static List<String> leader = new ArrayList<>(); // 리더 종류
    public static List<Location> coreloc = new ArrayList<>(); // 코어 위치들 저장
    public static Map<String, Core> cores = new HashMap<>();
    public static boolean canattackcore = false;  // can attack core
    public static boolean forceAttackCore = false;
    public static boolean caninteruptcore = false;  // can interupt core
    public static FileConfiguration config = null;
    public static final String[] coreTypes = { "seoul", "busan", "jeju", "pyongyang", "baekdu" };
    public static Countrywar plugin = null;
    public static MinecraftServer server = null;
    public static Map<UUID, Inventory> cInventory = new HashMap<>();
    public static List<UUID> tpCooltime = new ArrayList<>();
    
    public static void reloadCores() {
    	cores.clear();
		for(final String core : coreTypes)
			try {
				String coreUUID = config.getString("cores." + core, null);
				if(coreUUID == null) continue;
				World coreWorld = Bukkit.getWorld(UUID.fromString(config.getString("core-pos." + core + ".world")));
				int x = config.getInt("core-pos." + core + ".x");
				int z = config.getInt("core-pos." + core + ".z");
				coreWorld.setChunkForceLoaded(x, z, true);
				coreWorld.loadChunk(x, z, true);
				Core slime = Core.create((Slime) Bukkit.getEntity(UUID.fromString(coreUUID)), config.getString("core-occupier." + core, null));
				slime.setCoreType(CoreType.valueOf(core));
				slime.setCoreHealth(config.getDouble("core-health." + core, 5000.0));
				Cores.set(core, slime);
				cores.put(core, slime);
			} catch(Exception e) {
				Bukkit.getLogger().info(core + " 불러오기 오류");
			}
    }

    @Override
    public void onEnable() {
    	plugin = this;
    	config = this.getConfig();
    	
		version = getMinecraftVersion();
		server = ((CraftServer) Bukkit.getServer()).getServer();
		
		if(!version.equals("1.17.1")) {
			getLogger().info("이 플러그인은 버킷 1.17.1에서만 작동합니다 (현재 버전은 " + version + ")");
			return;
		}
		
        // Plugin startup logic
        getLogger().info("국가전쟁 플러그인 초기화 중...");
        getServer().getPluginManager().registerEvents(new Events(this), this);
        
		CommandDispatcher<CommandSourceStack> dispatcher = server.vanillaCommandDispatcher.getDispatcher();
    	TpbedCommand.register(dispatcher);
    	CoreCommand.register(dispatcher);
    	CoreListCommand.register(dispatcher);
    	CanAttackCoreCommand.register(dispatcher);
    	CanInteruptCoreCommand.register(dispatcher);
    	RuleCommand.register(dispatcher);
    	CountryCommand.register(dispatcher);

        config.options().copyDefaults(true);
		saveConfig();
		
		Country.loadCountries();
		reloadCores();
		
        req = new HashMap<String, String>();
        pvp = new ArrayList<>();
        cooltime = new HashMap<>();

        // add custom recipe
        ShapedRecipe experience_bottle = new ShapedRecipe(new NamespacedKey(this, "experience_bottle"), new ItemStack(Material.EXPERIENCE_BOTTLE, 3));
        experience_bottle.shape("ABA", "ACA", "AAA");
        experience_bottle.setIngredient('A', Material.REDSTONE);
        experience_bottle.setIngredient('B', Material.EMERALD);
        experience_bottle.setIngredient('C', Material.GLASS_BOTTLE);
        getServer().removeRecipe(new NamespacedKey(this, "experience_bottle"));
        getServer().addRecipe(experience_bottle);  // 조합법 생성 완료
        
        ShapedRecipe saddle = new ShapedRecipe(new NamespacedKey(this, "saddle"), new ItemStack(Material.SADDLE));
        saddle.shape("ABA", "ACA", "ABA");
        saddle.setIngredient('A', Material.LEATHER);
        saddle.setIngredient('B', Material.AIR);
        saddle.setIngredient('C', Material.RABBIT_HIDE);
        getServer().removeRecipe(new NamespacedKey(this, "saddle"));
        getServer().addRecipe(saddle);
        
        new BukkitRunnable() {
            @Override
            public void run() {
            	if(forceAttackCore) return;
            	int hour = LocalDateTime.now().getHour();
            	if(hour >= 21 && hour < 23 && Bukkit.getOnlinePlayers().size() >= 5) {
            		setCoreAttackable(true);
            	} else {
            		setCoreAttackable(false);
            	}
            }
        }.runTaskTimer(this, 0L, 20L * 60);
        
        getCommand("setting").setExecutor(new BukkitCommands());
        getServer().getPluginManager().registerEvents(new InventoryEvents(), this);

        getLogger().info("국가전쟁 플러그인을 불러왔습니다");
    }
    
    public static void setCoreAttackable(boolean flag, boolean force) {
    	if(canattackcore == flag) return;
        canattackcore = flag;
        if(flag && force) forceAttackCore = true;
        else forceAttackCore = false;
        if(flag)
	        for(Player player : Bukkit.getOnlinePlayers()) {  // 모든 플레이어 player
	            player.sendTitle("§c[ §f수호 신상 점령 타임 §c]", "§f§l지금 시간부터 수호 신상을 점령하거나 탈취가 가능합니다", 20, 40, 20);
	            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
	        }
        else
	        for(Player player : Bukkit.getOnlinePlayers()) {  // 모든 플레이어 player
	            player.sendTitle("§c[ §f수호 신상 점령 타임 종료 §c]", "§f§l수호 신상 점령 타임이 끝났습니다", 20, 40, 20);
	            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
	        }
    }
    
    public static void setCoreAttackable(boolean flag) {
    	setCoreAttackable(flag, false);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Countrywar Plugin Inactive");
        saveConfig();
    }
    
    public static void save() {
    	plugin.saveConfig();
    }

    public static void setTimeout(Runnable runnable, int delay) { // timer
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            } catch (Exception e) {
                System.err.println(e);
            }
        }).start();
    }
    
    public static Country getCountry(Player player) {
    	String uuid = config.getString("country-of-player." + player.getUniqueId(), null);
    	if(uuid == null) return null;
    	return Country.get(UUID.fromString(uuid));
    }
	
	// https://www.spigotmc.org/threads/multiple-nms-versions.79618/
	public static String getMinecraftVersion()
    {
		Matcher matcher = Pattern.compile("(\\(MC: )([\\d\\.]+)(\\))").matcher(Bukkit.getVersion());
		if (matcher.find()) {
		    return matcher.group(2);
		}
		return null;
    }
	
	// https://www.spigotmc.org/threads/save-and-load-a-bukkit-inventory-in-config.462282/
    public static String saveInventory(Inventory inventory) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeInt(inventory.getSize());
            for(int i=0; i<inventory.getSize(); i++)
                dataOutput.writeObject(inventory.getItem(i));
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Could not convert inventory to base64.", e);
        }
    }
    public static Inventory getInventory(String data, String name) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            Inventory inventory = Bukkit.getServer().createInventory(null, dataInput.readInt(), name);
            for(int i=0; i<inventory.getSize(); i++)
                inventory.setItem(i, (ItemStack) dataInput.readObject());
            dataInput.close();
            return inventory;
        } catch(ClassNotFoundException e) {
            throw new RuntimeException("Unable to decode the class type.", e);
        } catch(IOException e) {
            throw new RuntimeException("Unable to convert Inventory to Base64.", e);
        }
    }
}
