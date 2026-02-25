package nitrogen.countrywar.commands;

import java.util.ArrayList;
import java.util.List;

import nitrogen.countrywar.Country;
import nitrogen.countrywar.Countrywar;
import nitrogen.countrywar.core.Cores;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class CountryCommand {
	public static List<Inventory> countryInv = new ArrayList<>();
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
		LiteralArgumentBuilder countryNode = Commands.literal("country");
		countryNode.then(Commands.literal("create")
			.requires(source -> {
				CommandSender sender = source.getBukkitSender();
				if(!(sender instanceof Player)) return false;
				return Countrywar.getCountry((Player) sender) == null;
			})
			.then(Commands.argument("name", MessageArgument.message())
				.executes(commandContext -> createCountry(commandContext.getSource(), MessageArgument.getMessage(commandContext, "name")))));
		countryNode.then(Commands.literal("invite")
			.requires(source -> {
				CommandSender sender = source.getBukkitSender();
				if(!(sender instanceof Player)) return false;
				Country country = Countrywar.getCountry((Player) sender);
				if(country == null) return false;
				return country.getPlayers().size() < 4 && country.getLeader().getUniqueId().equals(((Player) sender).getUniqueId());
			})
			.then(Commands.argument("player", EntityArgument.player())
				.executes(commandContext -> invitePlayer(commandContext.getSource(), EntityArgument.getPlayer(commandContext, "player")))));
		countryNode.then(Commands.literal("accept")
			.requires(source -> {
				CommandSender sender = source.getBukkitSender();
				if(!(sender instanceof Player)) return false;
				Country country = Countrywar.getCountry((Player) sender);
				return country == null;
			})
			.executes(commandContext -> acceptInvite(commandContext.getSource())));
		countryNode.then(Commands.literal("deny")
			.requires(source -> {
				CommandSender sender = source.getBukkitSender();
				if(!(sender instanceof Player)) return false;
				Country country = Countrywar.getCountry((Player) sender);
				return country == null;
			})
			.executes(commandContext -> denyInvite(commandContext.getSource())));
		countryNode.then(Commands.literal("kick")
			.requires(source -> {
				CommandSender sender = source.getBukkitSender();
				if(!(sender instanceof Player)) return false;
				Country country = Countrywar.getCountry((Player) sender);
				if(country == null) return false;
				return country.getLeader().getUniqueId().equals(((Player) sender).getUniqueId());
			})
			.then(Commands.argument("player", EntityArgument.player())
				.executes(commandContext -> kickPlayer(commandContext.getSource(), EntityArgument.getPlayer(commandContext, "player")))));
		countryNode.then(Commands.literal("leave")
			.requires(source -> {
				CommandSender sender = source.getBukkitSender();
				if(!(sender instanceof Player)) return false;
				Country country = Countrywar.getCountry((Player) sender);
				return country != null;
			})
			.executes(commandContext -> leaveCountry(commandContext.getSource())));
		countryNode.then(Commands.literal("list")
			.executes(commandContext -> countryList(commandContext.getSource())));
		countryNode.then(Commands.literal("chat")
			.requires(source -> {
				CommandSender sender = source.getBukkitSender();
				if(!(sender instanceof Player)) return false;
				Country country = Countrywar.getCountry((Player) sender);
				return country != null;
			})
			.then(Commands.argument("message", MessageArgument.message())
				.executes(commandContext -> countryChat(commandContext.getSource(), MessageArgument.getMessage(commandContext, "message")))));
		countryNode.then(Commands.literal("storage")
			.requires(source -> {
				CommandSender sender = source.getBukkitSender();
				if(!(sender instanceof Player)) return false;
				Country country = Countrywar.getCountry((Player) sender);
				return country != null;
			})
			.executes(commandContext -> storage(commandContext.getSource())));
        commandDispatcher.register(countryNode);
	}
	
	private static int createCountry(CommandSourceStack source, Component nameComponent) throws CommandSyntaxException {
		CommandSender sender = source.getBukkitSender();
		if(!(sender instanceof Player))
			throw new SimpleCommandExceptionType((Message) new TranslatableComponent("플레이어만 명령어를 사용할 수 있습니다")).create();
		final Player p = (Player) sender;
        final String username = p.getName();
        final String name = nameComponent.getString();
        
        if(Countrywar.getCountry(p) != null)
        	throw new SimpleCommandExceptionType((Message) new TranslatableComponent("§c[ §f✖ §c] §c§l이미 소속된 국가가 있습니다")).create();
        if(Country.get(name) != null)
        	throw new SimpleCommandExceptionType((Message) new TranslatableComponent("§c[ §f✖ §c] §c§l이미 동일한 이름의 국가가 있습니다")).create();
        Country.create(name, p);
        source.sendSuccess(new TranslatableComponent("§a[ §f✔ §a] §3§l%s§f§l 국가가 생성되었습니다! \n§f§l대표 : §6§l%s", name, username), true);
        p.setPlayerListName(String.format("§f[ §3§l%s §f] §7%s", name, p.getName()));
        
        return 1;
	}
	
	@SuppressWarnings("deprecation")
	private static int invitePlayer(CommandSourceStack source, net.minecraft.world.entity.player.Player nmsPlayer) throws CommandSyntaxException {
		CommandSender sender = source.getBukkitSender();
		if(!(sender instanceof Player))
			throw new SimpleCommandExceptionType((Message) new TranslatableComponent("플레이어만 명령어를 사용할 수 있습니다")).create();
		final Player p = (Player) sender; // Command Sender
        final String username = p.getName(); // getName of the Player
        
        if(Countrywar.getCountry(p) == null)  // 아무 국가에도 소속되어 있지 않을 때
        	throw new SimpleCommandExceptionType((Message) new TranslatableComponent("§c[ §f✖ §c] §f§l국가에 소속되지 않은 플레이어는 이 명령어를 사용하실 수 없습니다")).create();
        if(!Countrywar.getCountry(p).getLeader().getUniqueId().equals(p.getUniqueId()))  // 리더가 아닐 때
        	throw new SimpleCommandExceptionType((Message) new TranslatableComponent("§c[ §f✖ §c] §f§l대표가 아닌 플레이어는 이 명령어를 사용하실 수 없습니다")).create();
        
        Player inviteplayer = Bukkit.getPlayer(nmsPlayer.getUUID());
        if(!(Bukkit.getOnlinePlayers().contains(inviteplayer)))  // 초대하려는 플레이어가 온라인이 아니라면 / 이름이 잘못되었다면
        	throw new SimpleCommandExceptionType((Message) new TranslatableComponent("§c[ §f✖ §c] §c§l국가에 초대하려는 플레이어가 온라인이 아니거나, 플레이어를 찾을 수 없습니다")).create();
        if(p.getUniqueId().equals(inviteplayer.getUniqueId()))  // if inviteplayer equals sender
        	throw new SimpleCommandExceptionType((Message) new TranslatableComponent("§c[ §f✖ §c] §f§l자신에게 초대를 보내실 수 없습니다")).create();
        if(Countrywar.getCountry(inviteplayer) != null)  // if inviteplayer is already in certain country
        	throw new SimpleCommandExceptionType((Message) new TranslatableComponent("§c[ §f✖ §c] §f§l플레이어가 이미 국가에 속해 있습니다")).create();
        
        String invite_name = inviteplayer.getName(); // 초대자의 닉네임

        if(Countrywar.req.get(invite_name) != null)
        	throw new SimpleCommandExceptionType((Message) new TranslatableComponent("§c[ §f✖ §c] §6§l%s§f이(가) 다른 국가의 초대요청 대기중입니다. §c나중에 다시 시도해주세요", invite_name)).create();
        
        Countrywar.req.put(invite_name, username);
        Countrywar.setTimeout(() -> {
            if(Countrywar.req.remove(invite_name) != null) {
                p.sendMessage("§c[ §f§l! §c] §6§l" + invite_name + "§f이(가) 국가 초대 요청에 응답하지 않습니다");
                inviteplayer.sendMessage("§c[ §f§l! §c] §6§l" + username + "§f의 국가 초대 요청이 만료되었습니다");
            }
        }, 20000);

        source.sendSuccess(new TranslatableComponent("§a[ §f✔ §a] §f§l초대를 보냈습니다"), false);
        
        TextComponent accept = new TextComponent("§a§l[수락]");
        TextComponent deny = new TextComponent("§c§l[거절]");
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/country accept"));
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§f/country accept").create()));
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/country deny"));
        deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§f/country deny").create()));

        TextComponent invite = new TextComponent("§9-------------------------\n §e§l국가장 "+p.getPlayerListName()+"§f에게 국가 참가 요청이 왔습니다\n §a§l수락하시겠습니까? ");
        inviteplayer.spigot().sendMessage(invite, accept, new TextComponent(" "), deny);
        
        return 1;
	}
	
	private static int acceptInvite(CommandSourceStack source) throws CommandSyntaxException {
		CommandSender sender = source.getBukkitSender();
		if(!(sender instanceof Player))
			throw new SimpleCommandExceptionType((Message) new TranslatableComponent("플레이어만 명령어를 사용할 수 있습니다")).create();
		final Player p = (Player) sender; // Command Sender
        final String username = p.getName(); // getName of the Player
        
        final String reqname = Countrywar.req.get(username);
        if(reqname == null)
        	throw new SimpleCommandExceptionType((Message) new TranslatableComponent("§c[ §f✖ §c] §f§l들어온 국가 초대 요청이 없습니다")).create();
        final Player requester = Bukkit.getPlayer(reqname);
        if(requester == null)
        	throw new SimpleCommandExceptionType((Message) new TranslatableComponent("§c[ §f✖ §c] §f§l초대자를 추적할 수 없습니다")).create();
        
        Country country = Countrywar.getCountry(requester);
        country.join(p);
        country.getPlayers().forEach(offlinePlayer -> {
        	Player player = offlinePlayer.getPlayer();
        	if(player == null) return;
        	player.sendMessage("§a[ §f✔ §a] §6§l"+p.getName()+"§f님이 국가에 입장했습니다");
        });
        
        source.sendSuccess(new TranslatableComponent("§a[ §f✔ §a] §f§l국가에 입장했습니다"), false);
        p.setPlayerListName("§f[ §3§l"+country.getName()+" §f] §7"+p.getName());
        Countrywar.req.remove(username);
        
        return 1;
	}
	
	private static int denyInvite(CommandSourceStack source) throws CommandSyntaxException {
		CommandSender sender = source.getBukkitSender();
		if(!(sender instanceof Player))
			throw new SimpleCommandExceptionType((Message) new TranslatableComponent("플레이어만 명령어를 사용할 수 있습니다")).create();
		final Player p = (Player) sender; // Command Sender
        final String username = p.getName(); // getName of the Player
        
        final String reqname = Countrywar.req.get(username);
        if(reqname == null)
        	throw new SimpleCommandExceptionType((Message) new TranslatableComponent("§c[ §f✖ §c] §f§l들어온 국가 초대 요청이 없습니다")).create();
        final Player requester = Bukkit.getPlayer(reqname);
        if(requester == null)
        	throw new SimpleCommandExceptionType((Message) new TranslatableComponent("§c[ §f✖ §c] §f§l초대자가 없습니다")).create();
        requester.sendMessage("§c[ §f§l! §c] §f§l국가 초대 요청이 거절되었습니다");
        source.sendSuccess(new TranslatableComponent("§c[ §f§l! §c] §f§l국가 초대 요청을 거절하였습니다"), false);
        Countrywar.req.remove(username);
        
        return 1;
	}
	
	private static int kickPlayer(CommandSourceStack source, net.minecraft.world.entity.player.Player nmsPlayer) throws CommandSyntaxException {
		CommandSender sender = source.getBukkitSender();
		if(!(sender instanceof Player))
			throw new SimpleCommandExceptionType((Message) new TranslatableComponent("플레이어만 명령어를 사용할 수 있습니다")).create();
		final Player p = (Player) sender; // Command Sender
        
        if(Countrywar.getCountry(p) == null)  // 아무 국가에도 소속되어 있지 않을 때
        	throw new SimpleCommandExceptionType((Message) new TranslatableComponent("§c[ §f✖ §c] §f§l국가에 소속되지 않은 플레이어는 이 명령어를 사용하실 수 없습니다")).create();
        if(!Countrywar.getCountry(p).getLeader().getUniqueId().equals(p.getUniqueId()))  // 리더가 아닐 때
        	throw new SimpleCommandExceptionType((Message) new TranslatableComponent("§c[ §f✖ §c] §f§l대표가 아닌 플레이어는 이 명령어를 사용하실 수 없습니다")).create();
        
        Player kickplayer = Bukkit.getPlayer(nmsPlayer.getUUID());
        if(!(Bukkit.getOnlinePlayers().contains(kickplayer)))  // 추방하려는 플레이어가 온라인이 아니라면 , 이름이 잘못 되었다면
        	throw new SimpleCommandExceptionType((Message) new TranslatableComponent("§c[ §f✖ §c] §c§l국가에서 추방하려는 플레이어가 온라인이 아니거나, 플레이어의 닉네임을 발견할 수 없습니다")).create();
        
        if(Countrywar.getCountry(kickplayer) == null)
        	throw new SimpleCommandExceptionType((Message) new TranslatableComponent("§c[ §f✖ §c] §c§l국가에 소속되지 않은 플레이어를 추방하실 수 없습니다")).create();
        if(kickplayer.getUniqueId().equals(p.getUniqueId()))  // if inviteplayer equals sender
        	throw new SimpleCommandExceptionType((Message) new TranslatableComponent("§c[ §f✖ §c] §f§l자신을 추방하실 수 없습니다")).create();
        if(!Countrywar.getCountry(kickplayer).equals(Countrywar.getCountry(p)))
        	throw new SimpleCommandExceptionType((Message) new TranslatableComponent("§c[ §f✖ §c] §c§l다른 국가의 플레이어를 추방하실 수 없습니다")).create();
        
        Countrywar.getCountry(p).kick(kickplayer);
        source.sendSuccess(new TranslatableComponent("§a[ §f✔ §a] §e§l §6§l" +kickplayer.getName()+ "§f님을 국가에서 추방했습니다"), false);
        kickplayer.sendMessage("§c[ §f§l! §c] §f§l국가에서 추방당했습니다");
        kickplayer.setPlayerListName("§f[ §d§l방랑자 §f] §7"+kickplayer.getName()); // change setDisplayname of kickplayer
        kickplayer.setCustomName(null);
        
        return 1;
	}
	
	private static int leaveCountry(CommandSourceStack source) throws CommandSyntaxException {
		try {
			CommandSender sender = source.getBukkitSender();
			if(!(sender instanceof Player))
				throw new SimpleCommandExceptionType((Message) new TranslatableComponent("플레이어만 명령어를 사용할 수 있습니다")).create();
			final Player p = (Player) sender; // Command Sender
	        
	        final Country country = Countrywar.getCountry(p);
	        if(country == null)  // 소속된 국가가 없으면  (종료)
	        	throw new SimpleCommandExceptionType((Message) new TranslatableComponent("§c[ §f✖ §c] §c§l소속된 국가가 없습니다")).create();
	
	        country.kick(p);
	        
	        if(country.getLeader().getUniqueId().equals(p.getUniqueId())) {
	        	if(Cores.SEOUL != null && Cores.SEOUL.getOccupier() != null && Cores.SEOUL.getOccupier().equals(country))
	        		Cores.SEOUL.contest();
	        	if(Cores.BUSAN != null && Cores.BUSAN.getOccupier() != null && Cores.BUSAN.getOccupier().equals(country))
	        		Cores.BUSAN.contest();
	        	if(Cores.JEJU != null && Cores.JEJU.getOccupier() != null && Cores.JEJU.getOccupier().equals(country))
	        		Cores.JEJU.contest();
	        	if(Cores.PYONGYANG != null && Cores.PYONGYANG.getOccupier() != null && Cores.PYONGYANG.getOccupier().equals(country))
	        		Cores.PYONGYANG.contest();
	        	if(Cores.BAEKDU != null && Cores.BAEKDU.getOccupier() != null && Cores.BAEKDU.getOccupier().equals(country))
	        		Cores.BAEKDU.contest();
	            
	            country.getPlayers().forEach(offlinePlayer -> {
	            	Player player = offlinePlayer.getPlayer();
	            	if(player == null) return;
	            	player.sendMessage("§c[ §f§l! §c] 국가가 해체되었습니다 점령한 수호신상이 초기화됩니다");
	            	player.setPlayerListName("§f[ §d§l방랑자 §f] §7"+player.getName()); // change setDisplayname
	            });
	            country.destroy();
	            p.setPlayerListName("§f[ §d§l방랑자 §f] §7"+p.getName()); // change setDisplayname
	            source.sendSuccess(new TranslatableComponent("§a[ §f✔ §a] §e§l국가를 해체하였습니다"), false);
	            return 1;
	        }
	        
	        source.sendSuccess(new TranslatableComponent("§a[ §f✔ §a] §e§l국가에서 나갔습니다"), false);
	        p.setPlayerListName("§f[ §d§l방랑자 §f] §7"+p.getName());  // change setDisplayname
	        
	        return 1;
		} catch(Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	private static int countryList(CommandSourceStack source) throws CommandSyntaxException {
		CommandSender p = source.getBukkitSender();
		
		// 방랑자 목록
		p.sendMessage("§9-------------------------\n §d§l방랑자");
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(Countrywar.getCountry(player) == null)
				p.sendMessage(" §f- §7"  + player.getName());
		}
		
		// 국가 목록
		Country.getCountries().forEach(country -> {
			int oc = 0;
			p.sendMessage("\n §3§l" + country.getName());
			if(country.getLeader().getName() == null)
				p.sendMessage(" §f- §6(국가 대표 미접속)");
			else
				p.sendMessage(" §f- §6" + country.getLeader().getName());
			for(OfflinePlayer player : country.getPlayers()) {
				if(player.getUniqueId().equals(country.getLeader().getUniqueId())) continue;
				if(!player.isOnline()) {
					if(player.getName() == null) {
						oc++;
						continue;
					} else {
						p.sendMessage(" §f- §7" + player.getName() + " (오프라인)");
					}
				} else {
					p.sendMessage(" §f- §7" + player.getName());
				}
			}
			if(oc > 0)
				p.sendMessage(String.format(" §f- §7(그 외 오프라인 플레이어 %d명)", oc));
			p.sendMessage(" §f §r §f §r ");
		});
		
        p.sendMessage("§9-------------------------");
        
        return 1;
	}
	
	private static int countryChat(CommandSourceStack source, Component msg) throws CommandSyntaxException {
		CommandSender sender = source.getBukkitSender();
		if(!(sender instanceof Player))
			throw new SimpleCommandExceptionType((Message) new TranslatableComponent("플레이어만 명령어를 사용할 수 있습니다")).create();
		final Player p = (Player) sender;  // Command Sender
        
		Country country = Countrywar.getCountry(p);
		if(country == null)
			throw new SimpleCommandExceptionType((Message) new TranslatableComponent("국가에 소속되어 있지 않습니다")).create();
		
		country.getPlayers().forEach(offlinePlayer -> {
			Player player = offlinePlayer.getPlayer();
			if(player == null) return;
			TextComponent format = new TextComponent("  §a§l국가 채팅 §f>  " + p.getPlayerListName() + ": §r");
			player.spigot().sendMessage(format, new TextComponent(msg.getString()));
		});

		return country.getPlayers().size();
	}
	
	private static int storage(CommandSourceStack source) throws CommandSyntaxException {
		CommandSender sender = source.getBukkitSender();
		if(!(sender instanceof Player))
			throw new SimpleCommandExceptionType((Message) new TranslatableComponent("§c[ §f§l! §c] §f§l플레이어만 명령어를 사용할 수 있습니다")).create();
		Player player = (Player) sender;
		Country country = Countrywar.getCountry(player);
		if(country == null)
			throw new SimpleCommandExceptionType((Message) new TranslatableComponent("§c[ §f§l! §c] §f§l국가에 소속되어 있지 않습니다")).create();
		if(country.inventoryOpener != null)
			throw new SimpleCommandExceptionType((Message) new TranslatableComponent("§c[ §f§l! §c] §f§l다른 친구가 국가 창고를 보고 있습니다")).create();
		player.openInventory(country.inventory);
		country.inventoryOpener = player;
		return 1;
	}
}
