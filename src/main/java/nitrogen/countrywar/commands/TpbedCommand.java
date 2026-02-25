package nitrogen.countrywar.commands;

import nitrogen.countrywar.Countrywar;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;

public class TpbedCommand {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
		commandDispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) Commands.literal("tpbed"))
	        .executes(commandContext -> tpBed((CommandSourceStack) commandContext.getSource()))));
	}
	
	private static int tpBed(CommandSourceStack source) throws CommandSyntaxException {
		CommandSender sender = source.getBukkitSender();
		
		if(!(sender instanceof Player))
			throw new SimpleCommandExceptionType((Message) new TranslatableComponent("플레이어만 명령어를 사용할 수 있습니다")).create();

        final Player p = (Player) sender; // Command Sender
        final String senderUUID = p.getUniqueId().toString(); // get UniqueID of the player
		
		if(Countrywar.cooltime.get(senderUUID) != null)
			throw new SimpleCommandExceptionType((Message) new TranslatableComponent("§c[ §f✖ §c] §c§l쿨타임이 적용되어 있습니다. §f§l(쿨타임 %s초)", Countrywar.config.getInt("cooltime") + "")).create();
        if(Countrywar.pvp.contains(senderUUID)) 
        	throw new SimpleCommandExceptionType((Message) new TranslatableComponent("§c[ §f✖ §c] §c§lPvP 도중에 순간이동하실 수 없습니다")).create();
        if(p.getBedSpawnLocation() == null)  // 만약 침대 리스폰이 정해지지 않았다면
        	throw new SimpleCommandExceptionType((Message) new TranslatableComponent("§c[ §f✖ §c] §c§l지정된 스폰포인트가 존재하지 않습니다")).create();

        Countrywar.cooltime.put(senderUUID, 1);
        Countrywar.setTimeout(() -> {
        	Countrywar.cooltime.remove(senderUUID);
        }, 1000 * 60 * 30); // 딜레이 1분
        source.sendSuccess(new TranslatableComponent("§a[ §f✔ §a] §e§l리스폰 포인트로 이동했습니다"), true);
        p.teleport(p.getBedSpawnLocation());
        
		return 1;
	}
}
