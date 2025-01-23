package me.harpervenom.peakyBlocks.lastwars.Trader;

import me.harpervenom.peakyBlocks.lastwars.Turret.Turret;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class Good {

    public static List<Good> allGoods = new ArrayList<>();

    public static Good getGood(int id) {
        for (Good good : allGoods) {
            if (good.getId() == id) {
                return good;
            }
        }
        return null;
    }

    private static int idCount = 0;

    private final int id;
    private ItemStack displayItem;
    private final ItemStack item;
    private int price;

    private static final NamespacedKey ID_KEY = new NamespacedKey("peaky_blocks", "good_id");

    public Good(Material material, int amount, int price) {
        id = idCount;
        idCount++;

        ItemStack item = new ItemStack(material);
        item.setAmount(amount);

        this.item = item;

        ItemStack displayItem = new ItemStack(item);

        ItemMeta meta = displayItem.getItemMeta();
        if (meta == null) return;

        List<String> lore = new ArrayList<>();
        this.price = price;
        lore.add(ChatColor.WHITE + "Цена: " + ChatColor.GREEN + price + " EXP");

        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        dataContainer.set(ID_KEY, PersistentDataType.INTEGER, id);

        meta.setLore(lore);
        displayItem.setItemMeta(meta);
        this.displayItem = displayItem;

        allGoods.add(this);
    }

    public int getId() {
        return id;
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getPrice() {
        return price;
    }

    public static Integer getGoodId(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        if (!dataContainer.has(ID_KEY, PersistentDataType.INTEGER)) return null;

        return dataContainer.get(ID_KEY, PersistentDataType.INTEGER);
    }
}
