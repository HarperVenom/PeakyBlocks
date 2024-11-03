package me.harpervenom.peakyBlocks.lastwars.Trader;

import me.harpervenom.peakyBlocks.utils.CustomMenuHolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;

import static me.harpervenom.peakyBlocks.lastwars.LuckyBook.LuckyBook.luckyBook;

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
        traderMenu.setItem(22, luckyBook);
    }
}
