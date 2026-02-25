package nitrogen.countrywar.settings;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class TeleportCoreInv implements InventoryHolder {
    private Inventory inv;

    public TeleportCoreInv() {
        inv = Bukkit.createInventory(this, 45, "Teleport");
        init();
    }

    private void init() {
        ItemStack item, empty;
        empty = createItem("", Material.ORANGE_STAINED_GLASS_PANE, null);
        List<String> lore = new ArrayList<>();

        for(int i=0; i<10; i++)
            inv.setItem(inv.firstEmpty(), empty); // 1 ~ 10

        lore.add("§f - §c❤ §f5000");
        lore.add("§f - 좌표 : §6322 ~ 182");
        item = createItem("§a§lSEOUL CORE", Material.CLOCK, lore);
        inv.setItem(inv.firstEmpty(), item);

        lore.clear();
        lore.add("§f - §c❤ §f5000");
        lore.add("§f - 좌표 : §61078 ~ -348");
        item = createItem("§7§lBUSAN CORE", Material.CLOCK, lore);
        inv.setItem(inv.firstEmpty(), item);

        lore.clear();
        lore.add("§f - §c❤ §f5000");
        lore.add("§f - 좌표 : §61600 ~ 346");
        item = createItem("§e§lJEJU CORE", Material.CLOCK, lore);
        inv.setItem(inv.firstEmpty(), item);

        for (int i = 0; i < 6; i++)
            inv.setItem(inv.firstEmpty(), empty);

        lore.clear();
        lore.add("§f - §c❤ §f5000");
        lore.add("§f - 좌표 : §6-63 ~ 563");
        item = createItem("§b§lPYONGYANG CORE", Material.CLOCK, lore);
        inv.setItem(inv.firstEmpty(), item);

        lore.clear();
        lore.add("§f - §c❤ §f5000");
        lore.add("§f - 좌표 : §6-1267 ~ -525");
        item = createItem("§c§lBAEKDU CORE", Material.CLOCK, lore);
        inv.setItem(inv.firstEmpty(), item);

        for (int i = 0; i<13; i++) // 13개
            inv.setItem(inv.firstEmpty(), empty);

        lore.clear();
        lore.add("§fBack to the settings");
        item = createItem("§c§lBACK", Material.BARRIER, lore);
        inv.setItem(inv.firstEmpty(), item);

        for (int i = 0; i<10; i++) // 36 ~ 45
            inv.setItem(inv.firstEmpty(), empty);
    }

    private ItemStack createItem(String name, Material mat, List<String> lore) {
        ItemStack item = new ItemStack(mat, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }
}