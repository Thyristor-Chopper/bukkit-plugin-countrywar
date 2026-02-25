package nitrogen.countrywar.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;
import nitrogen.countrywar.Countrywar;

public class CanInteruptCoreCommand {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
		LiteralArgumentBuilder canInteruptCore = Commands.literal("caninteruptcore").requires(commandSourceStack -> commandSourceStack.hasPermission(3));
		canInteruptCore
			.then(Commands.argument("flag", BoolArgumentType.bool())
				.executes(commandContext -> toggleCoreInteruptable(commandContext.getSource(), BoolArgumentType.getBool(commandContext, "flag"))));
        commandDispatcher.register(canInteruptCore);
	}
	
	private static int toggleCoreInteruptable(CommandSourceStack source, boolean flag) throws CommandSyntaxException {
		if(!source.hasPermission(3) || !source.getBukkitSender().isOp())
			throw new SimpleCommandExceptionType((Message) new TranslatableComponent("권한이 부족합니다")).create();
		
		if(flag) {
            Countrywar.caninteruptcore = true;
            source.sendSuccess(new TranslatableComponent("§a[ §f✔ §a] §f§l코어 파괴: 허용"), true);
        } else {
        	Countrywar.caninteruptcore = false;
            source.sendSuccess(new TranslatableComponent("§a[ §f✔ §a] §f§l코어 파괴: 불가"), true);
        }
        
		return 1;
	}
}
