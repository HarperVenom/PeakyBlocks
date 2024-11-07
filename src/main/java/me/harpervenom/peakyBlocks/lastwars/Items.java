package me.harpervenom.peakyBlocks.lastwars;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Items {

    public static List<ItemStack> lootItems = new ArrayList<>();

    static {
        for (Material material : Material.values()) {
            if (!material.isItem()) continue;
            if (material == Material.AIR || material.name().contains("SHERD") || material.name().contains("DISC")
            || material.name().contains("DYE") || material.name().contains("TEMPLATE") || material == Material.COMMAND_BLOCK
                    || material == Material.STRUCTURE_BLOCK || material.name().contains("FERN")) continue;
            lootItems.add(new ItemStack(material));
        }
    }
}
