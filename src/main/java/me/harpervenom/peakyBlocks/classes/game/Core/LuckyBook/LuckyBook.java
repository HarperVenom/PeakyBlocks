package me.harpervenom.peakyBlocks.classes.game.Core.LuckyBook;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LuckyBook {

    public static ItemStack luckyBook;
    public static final String luckyBookName = ChatColor.YELLOW + "Счастливая Книга";

    static {
        luckyBook = new ItemStack(Material.BOOK);
        ItemMeta meta = luckyBook.getItemMeta();
        if (meta != null) meta.setDisplayName(luckyBookName);
        luckyBook.setItemMeta(meta);
    }

    public static void giveLootToPlayer(Player p) {
        List<ItemStack> loot = generateLoot();
        for (ItemStack item : loot) {
            HashMap<Integer, ItemStack> remaining = p.getInventory().addItem(item);

            for (ItemStack droppedItem : remaining.values()) {
                p.getWorld().dropItemNaturally(p.getLocation().clone().add(-0.5, 0, -0.5), droppedItem);
            }
        }
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.5f, 1);
    }

    public static List<ItemStack> generateLoot() {
        List<ItemStack> items = new ArrayList<>();

        items.add(new ItemStack(Material.BAMBOO_PLANKS, 3));
        items.add(new ItemStack(Material.BLUE_BED, 1));

        return items;
    }


}
