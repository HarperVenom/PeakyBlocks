package me.harpervenom.peakyBlocks.lastwars.LuckyBook.Loot;

import org.bukkit.entity.Cat;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static me.harpervenom.peakyBlocks.lastwars.LuckyBook.Loot.Loot.categories;

public class Category {

    private final List<ItemWithChance> itemsWithChances = new ArrayList<>();
    private final Random random = new Random();
    private final double chance;

    public Category(double chance) {
        this.chance = chance;
    }

    public void addItem(ItemStack item, double chance, int maxAmount) {
        for (Category category : categories) {
            category.itemsWithChances.removeIf(existingItem -> existingItem.getItem().getType() == item.getType());
        }

        itemsWithChances.add(new ItemWithChance(item, chance, maxAmount));
    }

    public ItemStack pickRandomItem() {
        double totalChance = itemsWithChances.stream().mapToDouble(ItemWithChance::getChance).sum();

        // Normalize each item's chance to fit in [0, 1] range
        double randomValue = random.nextDouble();
        double cumulativeChance = 0.0;

        for (ItemWithChance itemWithChance : itemsWithChances) {
            cumulativeChance += itemWithChance.getChance() / totalChance;  // Normalize the chance
            if (randomValue <= cumulativeChance) {
                return itemWithChance.getItem();
            }
        }
        return null;
    }

    public double getChance() {
        return chance;
    }
}
