package me.harpervenom.peakyBlocks.lastwars.LuckyBook;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static me.harpervenom.peakyBlocks.lastwars.LuckyBook.Loot.Loot.getLootItemStack;
import static me.harpervenom.peakyBlocks.lastwars.Trader.TraderListener.goodsPrices;

public class LuckyBook {

    public static ItemStack luckyBook;
    public static final String luckyBookName = ChatColor.YELLOW + "ЛакиБук";

    static {
        luckyBook = new ItemStack(Material.BOOK);
        ItemMeta meta = luckyBook.getItemMeta();
        if (meta != null) meta.setDisplayName(luckyBookName);
        luckyBook.setItemMeta(meta);

        goodsPrices.put(luckyBookName, 10);
    }

    public static void giveLootToPlayer(Player p) {
        List<ItemStack> loot = generateLoot();
        for (ItemStack item : loot) {
            HashMap<Integer, ItemStack> remaining = p.getInventory().addItem(item);

            for (ItemStack droppedItem : remaining.values()) {
                p.getWorld().dropItemNaturally(p.getLocation().clone().add(-0.5, 0, -0.5), droppedItem);
            }
        }
    }

    public static List<ItemStack> generateLoot() {
        List<ItemStack> items = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            ItemStack item = getLootItemStack();
//            if (item != null) {
//                Bukkit.broadcastMessage(item.getType() + " - " + item.getAmount());
//            } else {
//                Bukkit.broadcastMessage(null);
//            }

            items.add(item);
        }

        return items;
    }

}
