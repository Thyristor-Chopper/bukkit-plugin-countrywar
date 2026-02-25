package nitrogen.countrywar.settings;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CustomInventory implements InventoryHolder {

    private Inventory inv;

    public CustomInventory() {
        inv = Bukkit.createInventory(this, 9, "Settings");
        init();
    }

    private void init() {
        ItemStack item, empty;
        //
        empty = createItem("", Material.LIME_STAINED_GLASS_PANE, null);
        List<String> lore = new ArrayList<>();

        inv.setItem(inv.firstEmpty(), empty);

        lore.add("§fTeleport to the core \nwhich you occupied");
        item = createItem("§6§lTeleport", Material.CLOCK, lore);
        inv.setItem(inv.firstEmpty(), item);

        inv.setItem(inv.firstEmpty(), empty);
        inv.setItem(inv.firstEmpty(), empty);

        lore.clear();
        lore.add("§fCustom recipes");
        item = createItem("§6§lRecipes", Material.BOOK, lore);
        inv.setItem(inv.firstEmpty(), item);

        inv.setItem(inv.firstEmpty(), empty);
        inv.setItem(inv.firstEmpty(), empty);

        lore.clear();
        lore.add("§fShow who occupied");
        lore.add("§fthe core");
        item = createItem("§6§lCore Status", Material.BEACON, lore);
        inv.setItem(inv.firstEmpty(), item);

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
