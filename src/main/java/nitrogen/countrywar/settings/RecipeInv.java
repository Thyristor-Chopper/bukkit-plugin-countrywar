package nitrogen.countrywar.settings;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class RecipeInv implements InventoryHolder {
    private Inventory inv;

    public RecipeInv() {
        inv = Bukkit.createInventory(this, 45, "Recipe");
        init();
    }

    private void init() {
        ItemStack item, empty, redstone, leather;
        empty = createItem("", Material.WHITE_STAINED_GLASS_PANE, null);
        redstone = createItem("", Material.REDSTONE, null);
        leather = createItem("", Material.LEATHER, null);

        for (int i = 0; i < 2; i++)
            inv.setItem(inv.firstEmpty(), empty);

        item = createItem("", Material.EXPERIENCE_BOTTLE, null);
        inv.setItem(inv.firstEmpty(), item);

        for (int i = 0; i < 3; i++)
            inv.setItem(inv.firstEmpty(), empty);

        item = createItem("", Material.SADDLE, null);
        inv.setItem(inv.firstEmpty(), item);

        for (int i = 0; i < 12; i++)
            inv.setItem(inv.firstEmpty(), empty);

        // experience bottle
        inv.setItem(inv.firstEmpty(), redstone);
        item = createItem("", Material.EMERALD, null); // 에메랄드
        inv.setItem(inv.firstEmpty(), item);
        inv.setItem(inv.firstEmpty(), redstone);

        inv.setItem(inv.firstEmpty(), empty);

        inv.setItem(inv.firstEmpty(), leather);
        inv.setItem(inv.firstEmpty(), empty);
        inv.setItem(inv.firstEmpty(), leather);

        for (int i = 0; i < 2; i++)
            inv.setItem(inv.firstEmpty(), empty);

        inv.setItem(inv.firstEmpty(), redstone);
        item = createItem("", Material.GLASS_BOTTLE, null); // 유리병
        inv.setItem(inv.firstEmpty(), item);
        inv.setItem(inv.firstEmpty(), redstone);

        inv.setItem(inv.firstEmpty(), empty);

        inv.setItem(inv.firstEmpty(), leather);
        item = createItem("", Material.RABBIT_HIDE, null); // 토끼 가죽
        inv.setItem(inv.firstEmpty(), item);
        inv.setItem(inv.firstEmpty(), leather);

        for (int i = 0; i < 2; i++)
            inv.setItem(inv.firstEmpty(), empty);

        for (int i = 0; i < 3; i++)
            inv.setItem(inv.firstEmpty(), redstone);

        inv.setItem(inv.firstEmpty(), empty);

        inv.setItem(inv.firstEmpty(), leather);
        inv.setItem(inv.firstEmpty(), empty);
        inv.setItem(inv.firstEmpty(), leather);
    }

    private ItemStack createItem(String name, Material mat, List<String> lore) {
        ItemStack item = new ItemStack(mat, 1);
        // meta.setDisplayName(name);
        // meta.setLore(lore);
        // item.setItemMeta(meta);
        return item;
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }
}
