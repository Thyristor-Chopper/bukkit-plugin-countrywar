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
import nitrogen.countrywar.core.Cores;

public class CanAttackCoreCommand {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
		LiteralArgumentBuilder canAttackCore = Commands.literal("canattackcore").requires(source -> source.hasPermission(3));
		canAttackCore
			.then(Commands.argument("flag", BoolArgumentType.bool())
				.executes(commandContext -> toggleCoreAttackable(commandContext.getSource(), BoolArgumentType.getBool(commandContext, "flag"))));
        commandDispatcher.register(canAttackCore);
	}
	
	private static int toggleCoreAttackable(CommandSourceStack source, boolean flag) throws CommandSyntaxException {
		if(!source.hasPermission(3) || !source.getBukkitSender().isOp())
			throw new SimpleCommandExceptionType((Message) new TranslatableComponent("권한이 부족합니다")).create();
		
		if(flag) {
			Countrywar.setCoreAttackable(true, true);
            source.sendSuccess(new TranslatableComponent("수호 신상을 공격할 수 있게 설정했습니다"), true);
        } else {
        	Countrywar.setCoreAttackable(false, true);
        	if(Cores.SEOUL != null)
        		Cores.SEOUL.setCoreHealth(5000.0);

        	if(Cores.BUSAN != null)
        		Cores.BUSAN.setCoreHealth(5000.0);

        	if(Cores.JEJU != null)
        		Cores.JEJU.setCoreHealth(5000.0);

        	if(Cores.PYONGYANG != null)
        		Cores.PYONGYANG.setCoreHealth(5000.0);

        	if(Cores.BAEKDU != null)
        		Cores.BAEKDU.setCoreHealth(5000.0);
            source.sendSuccess(new TranslatableComponent("수호 신상을 공격할 수 없게 설정했습니다"), true);
        }
		
		return 1;
	}
}
