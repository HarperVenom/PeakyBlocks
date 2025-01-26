package me.harpervenom.peakyBlocks.lastwars.LuckyBook;

import me.harpervenom.peakyBlocks.lastwars.LuckyBook.Loot.Category;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static me.harpervenom.peakyBlocks.lastwars.LuckyBook.Loot.Loot.*;
import static me.harpervenom.peakyBlocks.utils.Utils.createItem;

public class LuckyBook {

    public static ItemStack luckyBook;

    public static ItemStack blocksLuckyBook;
    public static ItemStack ingredientsLuckyBook;
    public static ItemStack utilitiesLuckyBook;
    public static ItemStack foodLuckyBook;
    public static ItemStack eggsLuckyBook;

    public static final String luckyBookName = ChatColor.WHITE + "Lucky Book";
    public static final String blocksLuckyBookName = ChatColor.YELLOW + "Lucky Book";
    public static final String ingredientsLuckyBookName = ChatColor.AQUA + "Lucky Book";
    public static final String utilitiesLuckyBookName = ChatColor.LIGHT_PURPLE + "Lucky Book";
    public static final String foodLuckyBookName = ChatColor.RED + "Lucky Book";
    public static final String eggsLuckyBookName = ChatColor.GREEN + "Lucky Book";


    static {
        luckyBook = createItem(Material.BOOK, luckyBookName, List.of(ChatColor.GRAY + "Любые предметы."));

        blocksLuckyBook = createItem(Material.BOOK, blocksLuckyBookName, List.of(ChatColor.GRAY + "Блоки."));
        ingredientsLuckyBook = createItem(Material.BOOK, ingredientsLuckyBookName, List.of(ChatColor.GRAY + "Ингредиенты."));
        utilitiesLuckyBook = createItem(Material.BOOK, utilitiesLuckyBookName, List.of(ChatColor.GRAY + "Снаряжение."));
        foodLuckyBook = createItem(Material.BOOK, foodLuckyBookName, List.of(ChatColor.GRAY + "Еда."));
        eggsLuckyBook = createItem(Material.BOOK, eggsLuckyBookName, List.of(ChatColor.GRAY + "Существа."));

//        int basePrice = 20;

//        goodsPrices.put(luckyBookName, basePrice);
//
//        goodsPrices.put(blocksLuckyBookName, (int) (basePrice * 1.3));
//        goodsPrices.put(ingredientsLuckyBookName, (int) (basePrice * 1.3));
//        goodsPrices.put(utilitiesLuckyBookName, (int) (basePrice * 1.5));
//        goodsPrices.put(foodLuckyBookName, (int) (basePrice * 1.5));
//        goodsPrices.put(eggsLuckyBookName, basePrice * 5);

    }

    public static void giveLootToPlayer(Player p, String name) {
        List<ItemStack> loot = generateLoot(name);
        for (ItemStack item : loot) {
            p.setCooldown(item.getType(), 20);
            HashMap<Integer, ItemStack> remaining = p.getInventory().addItem(item);

            for (ItemStack droppedItem : remaining.values()) {
                p.getWorld().dropItemNaturally(p.getLocation().clone().add(-0.5, 0, -0.5), droppedItem);
            }
        }
    }

    public static List<ItemStack> generateLoot(String name) {
        List<ItemStack> items = new ArrayList<>();

        for (int i = 0; i < 1; i++) {
            ItemStack item = getLootItemStack(getCategory(name));

            items.add(item);
        }

        return items;
    }

    public static ItemStack getPurchase(String name) {
        if (name.equals(luckyBookName)) {
            return luckyBook;
        } else if (name.equals(blocksLuckyBookName)) {
            return blocksLuckyBook;
        } else if (name.equals(ingredientsLuckyBookName)) {
            return ingredientsLuckyBook;
        } else if (name.equals(utilitiesLuckyBookName)) {
            return utilitiesLuckyBook;
        } else if (name.equals(foodLuckyBookName)) {
            return foodLuckyBook;
        } else if (name.equals(eggsLuckyBookName)) {
            return eggsLuckyBook;
        } else {
            return null;
        }
    }

    public static Category getCategory(String name) {
        if (name.equals(blocksLuckyBookName)) {
            return blocksCategory;
        } else if (name.equals(ingredientsLuckyBookName)) {
            return ingredientsCategory;
        } else if (name.equals(utilitiesLuckyBookName)) {
            return utilitiesCategory;
        } else if (name.equals(foodLuckyBookName)) {
            return foodCategory;
        } else if (name.equals(eggsLuckyBookName)) {
            return eggsCategory;
        } else {
            return null;
        }
    }

}
