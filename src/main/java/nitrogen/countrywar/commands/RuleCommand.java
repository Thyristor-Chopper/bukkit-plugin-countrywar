package nitrogen.countrywar.commands;

import org.bukkit.command.CommandSender;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class RuleCommand {
	public static String rules = "";
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
		commandDispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) Commands.literal("rule"))
	        .executes(commandContext -> rules((CommandSourceStack) commandContext.getSource()))));
	}
	
	private static int rules(CommandSourceStack source) {
		CommandSender sender = source.getBukkitSender();
		sender.sendMessage(rules);
		return 1;
	}
	
	static {
		rules += "§9-------------------------\n";
		rules += " §6§l설명\n §f- 오버월드에 5개 존재하는 수호신상을 모두\n점령하거나, 시간 내에 가장 많이 점령한 국가가 승리하게 되는 게임입니다.\n";
		rules += " §6§l명령어\n §f- 국가 관련 명령어: /country\n §e국가 생성, 해체, 창고 등\n §f- 설정 관련 명령어: /setting\n §e코어 점령 확인, 규칙, 신상 텔레포트 등\n §f- 이외의 명령어: /tpbed 침대로 tp (쿨타임 30분)\n";
		rules += " §6§l설정\n §f- 엑스레이 및 핵 사용 §c✖\n §f- 폭발물 사용 §c✖\n §f- 엔더 입장 §c✖\n §f- 네더라이트 갑옷 및 무기 §c✖\n §f- 신상 점령 시간: 매일\n§f- 신상 tp 쿨타임 §620§f분\n";
		rules += " §6§l팀\n §f- 팀 인원 제한은 4명이고, 팀 간에 자유롭게 이동할 수 있습니다.\n";
		rules += " §6§l제작자\n §f- SARI_L(준명), c_r_f(길고)\n §f- §c이러한 규칙을 지키지 않을 시에는 선처 없이 밴되실 수 있습니다.\n";
		rules += " §f- §6이외에 궁금한 질문은 섭장 SARI_L에게 문의 바랍니다.\n";
		rules += " §b* /rule 통해서 규칙을 다시 확인하실 수 있습니다. *";
	}
}
