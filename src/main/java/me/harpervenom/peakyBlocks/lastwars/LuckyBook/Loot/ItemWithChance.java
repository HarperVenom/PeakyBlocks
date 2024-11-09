package me.harpervenom.peakyBlocks.lastwars.LuckyBook.Loot;

import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class ItemWithChance {

    private final ItemStack item;
    private final double chance;
    private final int maxAmount;

    public ItemWithChance(ItemStack item, double chance, int maxAmount) {
        this.item = item;
        this.chance = chance;
        this.maxAmount = maxAmount;
    }

    public ItemStack getItem() {
        ItemStack newItem = new ItemStack(item);
        Random random = new Random();
        newItem.setAmount(Math.max(1, random.nextInt(maxAmount)));
        return newItem;
    }

    public double getChance() {
        return chance;
    }
}
