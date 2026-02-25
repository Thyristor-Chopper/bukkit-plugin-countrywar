package nitrogen.countrywar.commands;

import nitrogen.countrywar.Countrywar;
import nitrogen.countrywar.core.Core;
import nitrogen.countrywar.core.CoreType;
import nitrogen.countrywar.core.Cores;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;

public class CoreCommand {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
		LiteralArgumentBuilder literalArgumentBuilder = (LiteralArgumentBuilder) Commands.literal("core").requires(source -> source.hasPermission(3));
        
		for(String core : Countrywar.coreTypes) {
        	literalArgumentBuilder.then(((LiteralArgumentBuilder)
        		Commands.literal(core)
        			.executes(commandContext -> createCore(commandContext.getSource(), core))
        			.then(Commands.literal("create")
        				.executes(commandContext -> createCore(commandContext.getSource(), core)))
        			.then(Commands.literal("remove")
        				.executes(commandContext -> removeCore(commandContext.getSource(), core)))));
        }
		
        commandDispatcher.register(literalArgumentBuilder);
	}
	
	private static int createCore(CommandSourceStack source, String name) throws CommandSyntaxException {
		CommandSender sender = source.getBukkitSender();
		if(!(sender instanceof Player))
			throw new SimpleCommandExceptionType((Message) new TranslatableComponent("플레이어만 명령어를 사용할 수 있습니다")).create();
		if(!source.hasPermission(3) || !sender.isOp())
			throw new SimpleCommandExceptionType((Message) new TranslatableComponent("권한이 부족합니다")).create();
		
		final Player p = (Player) sender; // Command Sender
        
        if(Countrywar.cores.get(name) != null)
        	throw new SimpleCommandExceptionType((Message) new TranslatableComponent("신상이 이미 존재합니다")).create();
        
		int x = p.getLocation().getBlockX();
        int y = p.getLocation().getBlockY();
        int z = p.getLocation().getBlockZ();
        Location loc = new Location(p.getWorld(), x + 0.5, y, z + 0.5);
        Core core = Core.create(p.getWorld().spawn(loc, Slime.class), null);
        core.setSize(13);
        core.setHealth(50.0);
        core.setAI(false);
        core.setSilent(true);
        core.setRemoveWhenFarAway(false);
        core.setCustomNameVisible(false);
        core.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1073741824, 127, true, false));
        core.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1073741824, 5, true, false));
        core.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1073741824, 127, true, false));
        // 코어의 이름을 forest, ocean, rock, fire, light 중에 하나 선정
        core.setCoreType(CoreType.valueOf(name));
        core.addScoreboardTag("country_core");

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),  "fill " +(x+2)+ " " +(y)+ " " +(z+2)+ " " +(x-2)+ " " +(y+4)+ " " +(z-2)+ " white_concrete");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),  "fill " +(x+1)+ " " +(y+2)+ " " +(z+1)+ " " +(x-1)+ " " +(y+2)+ " " +(z-1)+ " iron_block");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),  "setblock " +(x)+ " " +(y+3)+ " " +(z)+ " beacon");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),  "setblock " +(x)+ " " +(y+4)+ " " +(z)+ " white_stained_glass");
        
        // Countrywar.coreloc.add(loc); // 코어 위치를 변수에 저장
        Countrywar.cores.remove(name);
        Countrywar.cores.put(name, core);
        Cores.set(name, core);
        Countrywar.config.set("cores." + name, core.getUniqueId().toString());
        Countrywar.config.set("core-pos." + name + ".x", core.getLocation().getChunk().getX());
        Countrywar.config.set("core-pos." + name + ".z", core.getLocation().getChunk().getZ());
        Countrywar.config.set("core-pos." + name + ".world", core.getLocation().getWorld().getUID().toString());
		core.getLocation().getWorld().setChunkForceLoaded(core.getLocation().getChunk().getX(), core.getLocation().getChunk().getZ(), true);
        Countrywar.save();
        
        source.sendSuccess(new TranslatableComponent("§c[ §f§l! §c] §e§l" + name + " §f코어가 생성되었습니다"), true);
        
		return 1;
	}
	
	private static int removeCore(CommandSourceStack source, String coreName) throws CommandSyntaxException {
		CommandSender sender = source.getBukkitSender();
		if(!(sender instanceof Player))
			throw new SimpleCommandExceptionType((Message) new TranslatableComponent("플레이어만 명령어를 사용할 수 있습니다")).create();
		if(!source.hasPermission(3) || !sender.isOp())
			throw new SimpleCommandExceptionType((Message) new TranslatableComponent("권한이 부족합니다")).create();
		
        Core core = Countrywar.cores.get(coreName);
        if(core == null)
        	throw new SimpleCommandExceptionType((Message) new TranslatableComponent("§c[ §f✖ §c] §c§l코어가 설치되지 않았습니다")).create();
        
        core.remove();
        Countrywar.cores.remove(coreName);
        Cores.set(coreName, null);
        Countrywar.config.set("cores." + coreName, null);
        Countrywar.save();
        source.sendSuccess(new TranslatableComponent("§a[ §f✔ §a] §f§l설치된 코어를 제거하였습니다"), true);

		return 1;
	}
}
