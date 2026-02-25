package nitrogen.countrywar.commands;

import nitrogen.countrywar.core.Cores;
import org.bukkit.command.CommandSender;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class CoreListCommand {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
		commandDispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) Commands.literal("corelist"))
	        .executes(commandContext -> coreList((CommandSourceStack) commandContext.getSource()))));
	}
	
	private static int coreList(CommandSourceStack source) throws CommandSyntaxException {
		CommandSender p = source.getBukkitSender();

        p.sendMessage("§9-------------------------\n §d§lSEOUL CORE");
        if(Cores.SEOUL == null || Cores.SEOUL.getOccupier() == null) p.sendMessage(" §f- §6상태 §f: §7점령 §c✖");
        else p.sendMessage(String.format(" §f- §6상태 §f: §3§l%s §f국가가 점령", Cores.SEOUL.getOccupier().getName()));

        p.sendMessage("\n §d§lBUSAN CORE");
        if(Cores.BUSAN == null || Cores.BUSAN.getOccupier() == null) p.sendMessage(" §f- §6상태 §f: §7점령 §c✖");
        else p.sendMessage(String.format(" §f- §6상태 §f: §3§l%s §f국가가 점령", Cores.BUSAN.getOccupier().getName()));

        p.sendMessage("\n §d§lJEJU CORE");
        if(Cores.JEJU == null || Cores.JEJU.getOccupier() == null) p.sendMessage(" §f- §6상태 §f: §7점령 §c✖");
        else p.sendMessage(String.format(" §f- §6상태 §f: §3§l%s §f국가가 점령", Cores.JEJU.getOccupier().getName()));

        p.sendMessage("\n §d§lPYONGYANG CORE");
        if(Cores.PYONGYANG == null || Cores.PYONGYANG.getOccupier() == null) p.sendMessage(" §f- §6상태 §f: §7점령 §c✖");
        else p.sendMessage(String.format(" §f- §6상태 §f: §3§l%s §f국가가 점령", Cores.PYONGYANG.getOccupier().getName()));

        p.sendMessage("\n §d§lBAEKDU CORE");
        if(Cores.BAEKDU == null || Cores.BAEKDU.getOccupier() == null) p.sendMessage(" §f- §6상태 §f: §7점령 §c✖");
        else p.sendMessage(String.format(" §f- §6상태 §f: §3§l%s §f국가가 점령", Cores.BAEKDU.getOccupier().getName()));

        p.sendMessage("§9-------------------------");
        
		return 1;
	}
}
