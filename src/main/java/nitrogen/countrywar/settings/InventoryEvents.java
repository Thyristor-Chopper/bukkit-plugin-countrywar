package nitrogen.countrywar.settings;

import nitrogen.countrywar.Country;
import nitrogen.countrywar.Countrywar;
import nitrogen.countrywar.core.Cores;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryEvents implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null)
            return;
        if (e.getClickedInventory().getHolder() instanceof CustomInventory) {
            e.setCancelled(true);

            Player p = (Player) e.getWhoClicked();

            if (e.getCurrentItem() == null)
                return;
            Material ItemType = e.getCurrentItem().getType();

            if (ItemType == Material.CLOCK) {
                TeleportCoreInv gui = new TeleportCoreInv();
                p.openInventory(gui.getInventory());
            } else if (ItemType == Material.BOOK) {
            	RecipeInv gui = new RecipeInv();
            	p.openInventory(gui.getInventory());
            } else if (ItemType == Material.BEACON) {
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
            }
        } else if (e.getClickedInventory().getHolder() instanceof TeleportCoreInv) {
            e.setCancelled(true);

            Player p = (Player) e.getWhoClicked();

            if (e.getCurrentItem() == null)
                return;
            Material ItemType = e.getCurrentItem().getType();

            if(ItemType == Material.CLOCK) {
            	Country country = Countrywar.getCountry(p);
            	if(country == null) {
            		p.sendMessage("§c[ §f§l! §c] §f§l국가에 소속되어 있지 않습니다");
            		return;
            	}
            	if(Countrywar.tpCooltime.contains(p.getUniqueId())) {
            		p.sendMessage("§c[ §f§l! §c] §f§l쿨타임이 끝나지 않았습니다");
            		return;
            	}
            	Location coreloc = null;
                String ItemName = e.getCurrentItem().getItemMeta().getDisplayName();
                if(ItemName.equals("§a§lSEOUL CORE")) {
                	if(!Cores.SEOUL.isOccupied() || !Cores.SEOUL.getOccupier().getID().equals(country.getID())) {
                		p.sendMessage("§c[ §f§l! §c] §f§l해당 코어를 점령하지 않았습니다");
                		return;}
                	coreloc = Cores.SEOUL.getLocation();
                	p.teleport(new Location(coreloc.getWorld(), coreloc.getX(), coreloc.getY() + 5, coreloc.getZ()));

                } else if(ItemName.equals("§7§lBUSAN CORE")) {
                	if(!Cores.BUSAN.isOccupied() || !Cores.BUSAN.getOccupier().getID().equals(country.getID())) {
                		p.sendMessage("§c[ §f§l! §c] §f§l해당 코어를 점령하지 않았습니다");
                		return;}
                	coreloc = Cores.BUSAN.getLocation();
                	p.teleport(new Location(coreloc.getWorld(), coreloc.getX(), coreloc.getY() + 5, coreloc.getZ()));

                } else if(ItemName.equals("§e§lJEJU CORE")) {
                	if(!Cores.JEJU.isOccupied() || !Cores.JEJU.getOccupier().getID().equals(country.getID())) {
                		p.sendMessage("§c[ §f§l! §c] §f§l해당 코어를 점령하지 않았습니다");
                		return;}
                	coreloc = Cores.JEJU.getLocation();
                	p.teleport(new Location(coreloc.getWorld(), coreloc.getX(), coreloc.getY() + 5, coreloc.getZ()));

                } else if(ItemName.equals("§b§lPYONGYANG CORE")) {
                	if(!Cores.PYONGYANG.isOccupied() || !Cores.PYONGYANG.getOccupier().getID().equals(country.getID())) {
                		p.sendMessage("§c[ §f§l! §c] §f§l해당 코어를 점령하지 않았습니다");
                		return;}
                	coreloc = Cores.PYONGYANG.getLocation();
                	p.teleport(new Location(coreloc.getWorld(), coreloc.getX(), coreloc.getY() + 5, coreloc.getZ()));

                } else if(ItemName.equals("§c§lBAEKDU CORE")) {
                	if(!Cores.BAEKDU.isOccupied() || !Cores.BAEKDU.getOccupier().getID().equals(country.getID())) {
                		p.sendMessage("§c[ §f§l! §c] §f§l해당 코어를 점령하지 않았습니다");
                		return;}
                	coreloc = Cores.BAEKDU.getLocation();
                	p.teleport(new Location(coreloc.getWorld(), coreloc.getX(), coreloc.getY() + 5, coreloc.getZ()));
                }

                p.sendMessage("§a[ §f✔ §a] §f§l코어로 순간이동했습니다");
                Countrywar.tpCooltime.add(p.getUniqueId());
                p.closeInventory();
                Countrywar.setTimeout(() -> {
                	Countrywar.tpCooltime.remove(p.getUniqueId());
                }, 1000 * 60 * 20);
            }
            else if (ItemType == Material.BARRIER) {
                CustomInventory gui = new CustomInventory();
                p.openInventory(gui.getInventory());
            }
        } else if (e.getClickedInventory().getHolder() instanceof RecipeInv) {
            e.setCancelled(true);
        }
    }
}