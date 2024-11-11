package me.harpervenom.peakyBlocks.lastwars.Trader;

import me.harpervenom.peakyBlocks.utils.CustomMenuHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static me.harpervenom.peakyBlocks.lastwars.LuckyBook.LuckyBook.*;
import static me.harpervenom.peakyBlocks.lastwars.Trader.TraderListener.goodsPrices;
import static me.harpervenom.peakyBlocks.utils.Utils.createItem;

public class Trader{

    private Villager villager;
    public static final String traderName = "Продавец";
    public static Inventory traderMenu;

    public Trader(Location spawn) {
        villager = (Villager) spawn.getWorld().spawnEntity(spawn, EntityType.VILLAGER);
        villager.setCustomNameVisible(false);
        villager.setCustomName(traderName);
        villager.setAI(false);
        villager.setInvulnerable(true);
        villager.setGravity(false);


        traderMenu = Bukkit.createInventory(new CustomMenuHolder("traderMenu"), 54, traderName);

        List<String> lore = luckyBook.getItemMeta().getLore();
        lore.add(ChatColor.WHITE + "Цена: " + ChatColor.GREEN + goodsPrices.get(luckyBookName) + " EXP");
        ItemStack luckyBookPurchase = createItem(luckyBook.getType(), luckyBook.getItemMeta().getDisplayName(), lore);

        lore = blocksLuckyBook.getItemMeta().getLore();
        lore.add(ChatColor.WHITE + "Цена: " + ChatColor.GREEN + goodsPrices.get(blocksLuckyBookName) + " EXP");
        ItemStack blocksLuckyBookPurchase = createItem(blocksLuckyBook.getType(), blocksLuckyBook.getItemMeta().getDisplayName(), lore);

        lore = ingredientsLuckyBook.getItemMeta().getLore();
        lore.add(ChatColor.WHITE + "Цена: " + ChatColor.GREEN + goodsPrices.get(ingredientsLuckyBookName) + " EXP");
        ItemStack ingredientsLuckyBookPurchase = createItem(ingredientsLuckyBook.getType(), ingredientsLuckyBook.getItemMeta().getDisplayName(), lore);

        lore = utilitiesLuckyBook.getItemMeta().getLore();
        lore.add(ChatColor.WHITE + "Цена: " + ChatColor.GREEN + goodsPrices.get(utilitiesLuckyBookName) + " EXP");
        ItemStack utilitiesLuckyBookPurchase = createItem(utilitiesLuckyBook.getType(), utilitiesLuckyBook.getItemMeta().getDisplayName(), lore);

        lore = foodLuckyBook.getItemMeta().getLore();
        lore.add(ChatColor.WHITE + "Цена: " + ChatColor.GREEN + goodsPrices.get(foodLuckyBookName) + " EXP");
        ItemStack foodLuckyBookPurchase = createItem(foodLuckyBook.getType(), foodLuckyBook.getItemMeta().getDisplayName(), lore);

        lore = eggsLuckyBook.getItemMeta().getLore();
        lore.add(ChatColor.WHITE + "Цена: " + ChatColor.GREEN + goodsPrices.get(eggsLuckyBookName) + " EXP");
        ItemStack eggsLuckyBookPurchase = createItem(eggsLuckyBook.getType(), eggsLuckyBook.getItemMeta().getDisplayName(), lore);

        traderMenu.setItem(22, luckyBookPurchase);

        traderMenu.setItem(38, blocksLuckyBookPurchase);
        traderMenu.setItem(39, ingredientsLuckyBookPurchase);
        traderMenu.setItem(40, utilitiesLuckyBookPurchase);
        traderMenu.setItem(41, foodLuckyBookPurchase);
        traderMenu.setItem(42, eggsLuckyBookPurchase);

    }

    public Entity getEntity() {
        return villager;
    }
}
