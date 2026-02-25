package nitrogen.countrywar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class Country {
	private static List<Country> countries = new ArrayList<>();
	private List<OfflinePlayer> players = new ArrayList<>();
	private String name;
	private UUID id;
	private OfflinePlayer leader = null;
	public Inventory inventory = null;
	public Player inventoryOpener = null;
	
	private Country(String name, UUID id, OfflinePlayer leader) {
		this.name = name;
		this.id = id;
		this.setLeader(leader);
	}
	
	public boolean join(OfflinePlayer player, boolean flag) {
		for(OfflinePlayer p : players) {
			if(p.getUniqueId().equals(player.getUniqueId()))
				return false;
		}
		players.add(player);
		Countrywar.config.set("country-of-player." + player.getUniqueId(), id.toString());
		if(flag) {
			List<String> splayers = players.stream().map(p -> p.getUniqueId().toString()).collect(Collectors.toList());
			Countrywar.config.set("country." + id + ".players", splayers);
			Countrywar.save();
		}
		if(player.isOnline()) {
			Countrywar.server.getPlayerList().sendPlayerPermissionLevel(((CraftPlayer) player.getPlayer()).getHandle());
			player.getPlayer().recalculatePermissions();
		}
		if(leader.isOnline()) {
			Countrywar.server.getPlayerList().sendPlayerPermissionLevel(((CraftPlayer) leader.getPlayer()).getHandle());
			leader.getPlayer().recalculatePermissions();
		}
		return true;
	}
	
	public boolean join(OfflinePlayer player) {
		return this.join(player, true);
	}
	
	public boolean kick(OfflinePlayer player) {
		players.remove(player);
		List<String> splayers = players.stream().map(p -> p.getUniqueId().toString()).collect(Collectors.toList());
		Countrywar.config.set("country." + id + ".players", splayers);
		Countrywar.config.set("country-of-player." + player.getUniqueId().toString(), null);
		Countrywar.save();
		if(player.isOnline()) {
			Countrywar.server.getPlayerList().sendPlayerPermissionLevel(((CraftPlayer) player.getPlayer()).getHandle());
			player.getPlayer().recalculatePermissions();
		}
		if(leader.isOnline()) {
			Countrywar.server.getPlayerList().sendPlayerPermissionLevel(((CraftPlayer) leader.getPlayer()).getHandle());
			leader.getPlayer().recalculatePermissions();
		}
		return true;
	}
	
	public String getName() {
		return this.name;
	}
	
	public UUID getID() {
		return this.id;
	}
	
	public OfflinePlayer getLeader() {
		return this.leader;
	}
	
	public void setLeader(OfflinePlayer leader) {
		OfflinePlayer oldLeader = this.leader;
		this.leader = leader;
		Countrywar.config.set("country." + id + ".leader", leader.getUniqueId().toString());
		Countrywar.save();
		if(oldLeader != null && oldLeader.isOnline()) {
			Countrywar.server.getPlayerList().sendPlayerPermissionLevel(((CraftPlayer) oldLeader.getPlayer()).getHandle());
			leader.getPlayer().recalculatePermissions();
		}
		if(leader.isOnline()) {
			Countrywar.server.getPlayerList().sendPlayerPermissionLevel(((CraftPlayer) leader.getPlayer()).getHandle());
			leader.getPlayer().recalculatePermissions();
		}
	}
	
	public String toString() {
		return this.getName();
	}
	
	public List<OfflinePlayer> getPlayers() {
		return players;
	}
	
	public boolean equals(Country other) {
		return this.getID().equals(other.getID());
	}
	
	public void destroy() {
		players.forEach(player -> {
			Countrywar.config.set("country-of-player." + player.getUniqueId(), null);
			Player p = player.getPlayer();
        	if(p == null) return;
			p.setPlayerListName("§f[ §d§l방랑자 §f] §7"+player.getName());
			Countrywar.server.getPlayerList().sendPlayerPermissionLevel(((CraftPlayer) p).getHandle());
			p.recalculatePermissions();
		});
		countries.remove(this);
		
		List<String> scountries = countries.stream().map(c -> c.getID().toString()).collect(Collectors.toList());
		Countrywar.config.set("countries", scountries);
		Countrywar.save();
	}
	
	public static List<Country> getCountries() {
		return countries;
	}
	
	protected static void loadCountries() {
		Countrywar.config.getStringList("countries").forEach(uuid -> {
			final String entry = "country." + uuid + ".";
			final Country c = new Country(Countrywar.config.getString(entry + "name"), UUID.fromString(uuid), Bukkit.getOfflinePlayer(UUID.fromString(Countrywar.config.getString(entry + "leader"))));
			Countrywar.config.getStringList(entry + "players").forEach(p -> c.join(Bukkit.getOfflinePlayer(UUID.fromString(p)), false));
			c.setLeader(Bukkit.getOfflinePlayer(UUID.fromString(Countrywar.config.getString(entry + "leader"))));
			String rawinv = Countrywar.config.getString(entry + "inventory");
			if(rawinv != null)
				c.inventory = Countrywar.getInventory(Countrywar.config.getString(entry + "inventory"), c.getName() + "의 국가창고");
			else
				c.inventory = Bukkit.createInventory(null, 54, c.getName() + "의 국가창고");
			countries.add(c);
		});
	}
	
	public static Country get(UUID id) {
		for(Country country : countries) {
			if(country.getID().equals(id))
				return country;
		}
		return null;
	}
	
	public static Country get(String name) {
		for(Country country : countries) {
			if(country.getName().equals(name))
				return country;
		}
		return null;
	}
	
	public static Country create(String name, OfflinePlayer leader) {
		Country country = new Country(name, UUID.randomUUID(), leader);
		country.join(leader);
		Countrywar.config.set("country." + country.getID() + ".name", name);
		country.inventory = Bukkit.createInventory(null, 54, name + "의 국가창고");
		Countrywar.config.set("country." + country.getID() + ".inventory", Countrywar.saveInventory(country.inventory));
		countries.add(country);
		List<String> list = Countrywar.config.getStringList("countries");
		if(list == null) list = Arrays.asList();
		list.add(country.getID().toString());
		Countrywar.config.set("countries", list);
		Countrywar.save();
		return country;
	}
}
