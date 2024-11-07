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

import static me.harpervenom.peakyBlocks.lastwars.LuckyBook.LuckyBook.luckyBook;
import static me.harpervenom.peakyBlocks.lastwars.LuckyBook.LuckyBook.luckyBookName;
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
        ItemStack luckyBookPurchase = createItem(luckyBook.getType(), luckyBook.getItemMeta().getDisplayName(),
                List.of(ChatColor.WHITE + "Цена: " + ChatColor.GREEN + goodsPrices.get(luckyBookName) + " EXP"));

        traderMenu.setItem(22, luckyBookPurchase);
    }
}
