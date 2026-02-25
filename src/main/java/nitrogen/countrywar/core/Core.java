package nitrogen.countrywar.core;

import java.util.Map.Entry;
import java.util.UUID;

import nitrogen.countrywar.Country;
import nitrogen.countrywar.Countrywar;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftSlime;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Slime;

public class Core extends CraftSlime {
	private Country occupier = null;
	private CoreType type = null;
	private double coreHealth = 5000.0;
	
	private Core(CraftServer server, net.minecraft.world.entity.monster.Slime entity) {
		super(server, entity);
	}
	
	public Country getOccupier() {
		return this.occupier;
	}
	
	public void setOccupier(Country country) {
		this.occupier = country;
	}
	
	public CoreType getCoreType() {
		return this.type;
	}
	
	public boolean isOccupied() {
		return occupier != null;
	}
	
	public void contest() {
		occupier = null;
	}
	
	public void setCoreType(CoreType type) {
		this.type = type;
	}
	
	public double getCoreHealth() {
		return this.coreHealth;
	}
	
	public double damageCore(double d) {
		this.coreHealth -= d;
		if(this.coreHealth < 0.0)
			this.coreHealth = 0.0;
		return this.coreHealth;
	}
	
	public void setCoreHealth(double d) {
		this.coreHealth = d;
	}
	
	public boolean equals(Core other) {
		return this.getUniqueId().equals(other.getUniqueId());
	}
	
	public static boolean isCore(Entity slime) {
		for(Entry<String, Core> entry : Countrywar.cores.entrySet()) {
			if(entry.getValue().getUniqueId().equals(slime.getUniqueId()))
				return true;
		}
		return false;
	}
	
	public static Core from(Entity slime) {
		for(Entry<String, Core> entry : Countrywar.cores.entrySet()) {
			Core core = entry.getValue();
			if(core.getUniqueId().equals(slime.getUniqueId())) 
				return core;
		}
		return null;
	}
	
	public static Core create(Slime slime, String occipier) {
		Core core = new Core((CraftServer) Bukkit.getServer(), ((CraftSlime) slime).getHandle());
		core.removeScoreboardTag("country_core");
		core.addScoreboardTag("country_core");
		if(occipier != null)
			core.setOccupier(Country.get(UUID.fromString(occipier)));
		
		return core;
	}
}
