package nitrogen.countrywar;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import nitrogen.countrywar.core.Core;
import nitrogen.countrywar.core.CoreType;
import nitrogen.countrywar.core.Cores;
import nitrogen.countrywar.commands.RuleCommand;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.event.block.BlockExplodeEvent;

import java.util.Map.Entry;

public class Events implements Listener {
    Countrywar plugin;
    boolean is_light_core = false;
    public static String occupy_forest, occupy_ocean, occupy_rock, occupy_fire, occupy_light;

    BossBar SEOUL_BAR = Bukkit.createBossBar("", BarColor.GREEN, BarStyle.SOLID);
    BossBar BUSAN_BAR = Bukkit.createBossBar("", BarColor.BLUE, BarStyle.SOLID);
    BossBar JEJU_BAR = Bukkit.createBossBar("", BarColor.WHITE, BarStyle.SOLID);
    BossBar PYONGYANG_BAR = Bukkit.createBossBar("", BarColor.RED, BarStyle.SOLID);
    BossBar BAEKDU_BAR = Bukkit.createBossBar("", BarColor.YELLOW, BarStyle.SOLID);
    
    public Events(Countrywar plugin) {
    	this.plugin = plugin;
    }

    public void setTimeout(Runnable runnable, int delay) {
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            } catch (Exception e) {
                System.err.println(e);
            }
        }).start();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {  // 서버 조인 메세지
        Player p = e.getPlayer();
        Country country = Countrywar.getCountry(p);
        e.setJoinMessage("§f[§a+§f] " + p.getName());

        if(country == null)
            p.setPlayerListName("§f[ §d§l방랑자 §f] §7"+p.getName());
        else
            p.setPlayerListName("§f[ §3§l" + country + " §f] §7"+p.getName());
        Countrywar.reloadCores();
		
		p.sendMessage(RuleCommand.rules);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) { // 서버 나감 메세지
        Player p = e.getPlayer();
        e.setQuitMessage("§f[§c-§f] " + p.getName());
        if(Countrywar.pvp.contains(p.getUniqueId().toString())) {
            p.remove();
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        Entity eVictim = e.getEntity();
        Entity eDamager = e.getDamager();

        if(!(eDamager instanceof Player)) return;
        Player damager = (Player) eDamager;
        
        Country damager_country = Countrywar.getCountry(damager);
        
        // 코어가 대미지를 입을 때
        if (Core.isCore(eVictim)) {
        	Core core = Core.from(eVictim);
            CoreType type = core.getCoreType();
            if (damager_country == null) {
                damager.sendMessage("§c[ §f✖ §c] §c§l방랑자는 수호 신상을 점령하실 수 없습니다");
                e.setCancelled(true);
                return; }
            if (!Countrywar.canattackcore) {  // if canattackcore is false
                damager.sendMessage("§c[ §f✖ §c] §c§l지금은 수호 신상을 점령하실 수 없습니다");
                e.setCancelled(true);
                return; }
            if(core.isOccupied() && core.getOccupier().equals(damager_country)) {
            	damager.sendMessage("§c[ §f✖ §c] §c§l이미 이 수호 신상을 점령하였습니다");
            	e.setCancelled(true);
                return; }

            double damage = e.getDamage(); // damage
            core.damageCore(damage);
            double coreHealth = core.getCoreHealth();
            Countrywar.config.set("core-health." + type, coreHealth);
            Countrywar.save();
            if(core.getCoreType().equals(CoreType.seoul)) {
                SEOUL_BAR.setTitle("§b§l◊ §6§lSEOUL CORE §b§l◊ §f- §c❤ §f" + health_calculate(Cores.SEOUL.getCoreHealth()));
                SEOUL_BAR.setProgress(bar_calculate(Cores.SEOUL.getCoreHealth()));
                SEOUL_BAR.addPlayer(damager); }
            else if(core.getCoreType().equals(CoreType.busan)) {
                BUSAN_BAR.setTitle("§b§l◊ §6§lBUSAN CORE §b§l◊ §f- §c❤ §f" + health_calculate(Cores.BUSAN.getCoreHealth()));
                BUSAN_BAR.setProgress(bar_calculate(Cores.BUSAN.getCoreHealth()));
                BUSAN_BAR.addPlayer(damager); }
            else if(core.getCoreType().equals(CoreType.jeju)) {
                JEJU_BAR.setTitle("§b§l◊ §6§lJEJU CORE §b§l◊ §f- §c❤ §f" + health_calculate(Cores.JEJU.getCoreHealth()));
                JEJU_BAR.setProgress(bar_calculate(Cores.JEJU.getCoreHealth()));
                JEJU_BAR.addPlayer(damager); }
            else if(core.getCoreType().equals(CoreType.pyongyang)) {
                PYONGYANG_BAR.setTitle("§b§l◊ §6§lPYONGYANG CORE §b§l◊ §f- §c❤ §f" + health_calculate(Cores.PYONGYANG.getCoreHealth()));
                PYONGYANG_BAR.setProgress(bar_calculate(Cores.PYONGYANG.getCoreHealth()));
                PYONGYANG_BAR.addPlayer(damager); }
            else if(core.getCoreType().equals(CoreType.baekdu)) {
                BAEKDU_BAR.setTitle("§b§l◊ §6§lBAEKDU CORE §b§l◊ §f- §c❤ §f" + health_calculate(Cores.BAEKDU.getCoreHealth()));
                BAEKDU_BAR.setProgress(bar_calculate(Cores.BAEKDU.getCoreHealth()));
                BAEKDU_BAR.addPlayer(damager); }
            
            if(core.isOccupied())
            	core.getOccupier().getPlayers().forEach(offlinePlayer -> {
            		Player player = offlinePlayer.getPlayer();
            		if(player == null) return;
            		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED+damager.getPlayerListName()+"§c님이 " + CoreType.localize(core.getCoreType()) + " 수호신상을 공격 중입니다! - 남은 체력: " + health_calculate(coreHealth)));
            	});
            
            if(coreHealth <= 1) {  // 코어가 죽었을 때
        		core.damage(987654321.0);
        		core.remove();
                Player p = damager;  // 코어 점령자
                CoreType corenick = core.getCoreType(); // 코어 종류

                String korean_core = CoreType.localize(corenick);

                for(Player player : Bukkit.getOnlinePlayers()) { // 모든 플레이어 player
                	player.sendTitle("§b[ §f수호 신상 안내 §b]", String.format("§3§l%s §f국가의 §6§l%s§f이(가) §c§l%s§c§l의 수호신상§f을 점령했습니다!", Countrywar.getCountry(p).getName(), p.getName(), korean_core), 20, 40, 20);
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL,1,  1);
                }
                Slime slime = p.getWorld().spawn(core.getLocation(), Slime.class);
                Core newCore = Core.create(slime, null);
                newCore.setCoreType(corenick);
                newCore.setSize(13);
                newCore.setHealth(50.0);
                newCore.setAI(false);
                newCore.setSilent(true);
                newCore.setRemoveWhenFarAway(false);
                newCore.setCustomNameVisible(false);
                newCore.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 2147483647, 127, true, false));
                newCore.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 2147483647, 5, true, false));
                newCore.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 2147483647, 127, true, false));

                newCore.setCoreHealth(5000.0);

                newCore.setOccupier(Countrywar.getCountry(p));
                Cores.set(corenick.toString(), newCore);
                Countrywar.cores.remove(corenick.toString());
                Countrywar.cores.put(corenick.toString(), newCore);
                Countrywar.config.set("cores." + corenick.toString(), newCore.getUniqueId().toString());
                Countrywar.config.set("core-occupier." + corenick, Countrywar.getCountry(p).getID().toString());
                Countrywar.config.set("core-health." + corenick, 5000.0);
                Countrywar.save();
            }
        }
        
        // 공격 받은 사람도 플레이어일 때
        if(!(eVictim instanceof Player)) return;
        Player victim = (Player) eVictim;
        Country victim_country = Countrywar.getCountry(victim);

        if(damager_country != null && victim_country != null) {  // 공격한 사람과 공격받은 사람이 둘 다 국가에 소속되었을 때
            if (damager_country.equals(victim_country)) {
                damager.sendMessage("§c[ §f✖ §c] §c§l같은 국가에 속해있는 플레이어를 공격할 수 없습니다");
                e.setCancelled(true); // damage cancel
                return; }
        }

        String x, y;
        x = damager.getUniqueId().toString();
        y = victim.getUniqueId().toString();
        Countrywar.pvp.remove(x);
        Countrywar.pvp.remove(y);
        Countrywar.pvp.add(x);
        Countrywar.pvp.add(y);
        setTimeout(() -> {
        	Countrywar.pvp.remove(x);
            Countrywar.pvp.remove(y);
        }, 20000);  // 20s wait
    }

    @EventHandler
    public void onCoreDamage(EntityDamageEvent e) {  // 코어 히트박스 압사 방지
        if((!e.getEntityType().equals(EntityType.SLIME))) return; // 코어가 슬라임일 때 다음줄부터 실행
        if(!Core.isCore(e.getEntity()) && !e.getEntity().getScoreboardTags().contains("country_core")) return;
        
        EntityDamageEvent.DamageCause cause = e.getCause();

        if(cause.equals(EntityDamageEvent.DamageCause.SUFFOCATION) || cause.equals(EntityDamageEvent.DamageCause.LAVA))
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player victim = e.getEntity();
        Player killer = victim.getKiller();

        String victim_info = victim.getPlayerListName();
        if(killer == null) {  // 킬러가 아무도 없다면
            e.setDeathMessage("§c[ §f§l! §c] "+victim_info+"§f이(가) 혼자서 사망하셨습니다");
            return;}

        String killer_info = killer.getPlayerListName();
        e.setDeathMessage("§c[ §f§l! §c] "+victim_info+"§f이(가) "+killer_info+"§f에게 살해당했습니다");
    }

    @EventHandler
    public void onSlimeSplit(SlimeSplitEvent e) {  // 코어 슬라임이 분열할 때 이벤트 취소
    	if(!Core.isCore(e.getEntity())) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        Country p_country = Countrywar.getCountry(p);
        String chat = e.getMessage();

        if (p_country == null) {
            e.setFormat("  §7§l방랑자 §f>  "+p.getPlayerListName()+": §f"+chat);
            return; }
        e.setFormat("  §e§l전체 채팅 §f>  "+p.getPlayerListName()+": §f"+chat);
    }

    @EventHandler
    public void onPlayerNearCore(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        
    	try {
	        
	        for(Entry<String, Core> entry : Countrywar.cores.entrySet()) {
	        	Location locate_core = entry.getValue().getLocation();
	            double dis = p.getLocation().distance(locate_core);
	
	            if (dis <= 50) {
	                Chunk chunk = locate_core.getChunk();
	
	                for (Entity entity : chunk.getEntities()) {
	                	if(!Core.isCore(entity)) continue;
	                	Core core = Core.from(entity);

                        if(core.getCoreType().equals(CoreType.seoul)) {
                            SEOUL_BAR.setTitle("§b§l◊ §6§lSEOUL CORE §b§l◊ §f- §c❤ §f" + health_calculate(Cores.SEOUL.getCoreHealth()));
                            SEOUL_BAR.setProgress(bar_calculate(Cores.SEOUL.getCoreHealth()));
                            SEOUL_BAR.addPlayer(p); }
                        else if(core.getCoreType().equals(CoreType.busan)) {
                            BUSAN_BAR.setTitle("§b§l◊ §6§lBUSAN CORE §b§l◊ §f- §c❤ §f" + health_calculate(Cores.BUSAN.getCoreHealth()));
                            BUSAN_BAR.setProgress(bar_calculate(Cores.BUSAN.getCoreHealth()));
                            BUSAN_BAR.addPlayer(p); }
                        else if(core.getCoreType().equals(CoreType.jeju)) {
                            JEJU_BAR.setTitle("§b§l◊ §6§lJEJU CORE §b§l◊ §f- §c❤ §f" + health_calculate(Cores.JEJU.getCoreHealth()));
                            JEJU_BAR.setProgress(bar_calculate(Cores.JEJU.getCoreHealth()));
                            JEJU_BAR.addPlayer(p); }
                        else if(core.getCoreType().equals(CoreType.pyongyang)) {
                            PYONGYANG_BAR.setTitle("§b§l◊ §6§lPYONGYANG CORE §b§l◊ §f- §c❤ §f" + health_calculate(Cores.PYONGYANG.getCoreHealth()));
                            PYONGYANG_BAR.setProgress(bar_calculate(Cores.PYONGYANG.getCoreHealth()));
                            PYONGYANG_BAR.addPlayer(p); }
                        else if(core.getCoreType().equals(CoreType.baekdu)) {
                            BAEKDU_BAR.setTitle("§b§l◊ §6§lBAEKDU CORE §b§l◊ §f- §c❤ §f" + health_calculate(Cores.BAEKDU.getCoreHealth()));
                            BAEKDU_BAR.setProgress(bar_calculate(Cores.BAEKDU.getCoreHealth()));
                            BAEKDU_BAR.addPlayer(p); }
	                }
	                return;
	            } else {
	                SEOUL_BAR.removePlayer(p);
	                BUSAN_BAR.removePlayer(p);
	                JEJU_BAR.removePlayer(p);
	                PYONGYANG_BAR.removePlayer(p);
	                BAEKDU_BAR.removePlayer(p);
	            }
	        }
    	} catch(Exception ex) {
            SEOUL_BAR.removePlayer(p);
            BUSAN_BAR.removePlayer(p);
            JEJU_BAR.removePlayer(p);
            PYONGYANG_BAR.removePlayer(p);
            BAEKDU_BAR.removePlayer(p);
    	}
    }

    @EventHandler
    public void onCoreProtectPlace(BlockPlaceEvent e) {
    	if(Countrywar.caninteruptcore) return;
    	
        Player p = e.getPlayer();
        for(Entry<String, Core> entry : Countrywar.cores.entrySet()) {
        	Location location = entry.getValue().getLocation();
        	double distance = p.getLocation().distance(location);
        	if(distance <= 15) {
                p.sendMessage("§c[ §f✖ §c] §f§l신상 주변에서의 블록 설치나 파괴는 불가능합니다");
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onCoreProtectBreak(BlockBreakEvent e) {
    	if(Countrywar.caninteruptcore) return;
    	
        Player p = e.getPlayer();
        for(Entry<String, Core> entry : Countrywar.cores.entrySet()) {
        	Location location = entry.getValue().getLocation();
        	double distance = p.getLocation().distance(location);
        	if(distance <= 15) {
                p.sendMessage("§c[ §f✖ §c] §f§l신상 주변에서의 블록 설치나 파괴는 불가능합니다");
                e.setCancelled(true);
                return;
            }
        }
    }

    public double bar_calculate(double health) {
        double result;
        result = health / 5000;
        return result;
    }

    public double health_calculate(double health) {
        return Math.round(health * 10.0) / 10.0;
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
    	Player player = (Player) e.getPlayer();
    	if(player == null) return;
    	Country country = Countrywar.getCountry(player);
    	if(country == null) return;
    	if(e.getInventory().equals(country.inventory)) {
    		country.inventoryOpener = null;
    		Countrywar.config.set("country." + country.getID() + ".inventory", Countrywar.saveInventory(country.inventory));
    		Countrywar.save();
    	}
    }
    
    @EventHandler
    public void onItemSmith(SmithItemEvent e) {
        e.setCancelled(true);
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack item = e.getItem();

        if(item != null && ((item.getType() == Material.LAVA_BUCKET) || (item.getType() == Material.WATER_BUCKET)) && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            for(Entry<String, Core> entry : Countrywar.cores.entrySet()) {
                Location location = entry.getValue().getLocation();
                double distance = p.getLocation().distance(location);
                if(distance <= 15) {
                    p.sendMessage("§c[ §f✖ §c] §f§l신상 주변에서의 블록 설치나 파괴는 불가능합니다");
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }
    
    @EventHandler
    public void onBlockExplode(BlockExplodeEvent e) {
    	if(e.getBlock().getType().equals(Material.RESPAWN_ANCHOR))
    		e.setCancelled(true);
    }
    
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
    	if(e.getEntityType().equals(EntityType.ENDER_CRYSTAL))
    		e.setCancelled(true);
    	if(e.getEntityType().equals(EntityType.PRIMED_TNT))
    		e.setCancelled(true);
    }

    @EventHandler
    public void onExplosion(ExplosionPrimeEvent e) {
        if(e.getEntityType().equals(EntityType.CREEPER)) return;  // 이벤트 취소
        e.setCancelled(true);
    }
}
