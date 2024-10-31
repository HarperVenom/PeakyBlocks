package me.harpervenom.peakyBlocks.utils;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class Utils {

    public static ItemStack createItem(Material material, String title, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setDisplayName(title);
        if (lore != null) meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack changeItemTitle(ItemStack item, String title) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.setDisplayName(title);
        item.setItemMeta(meta);

        return item;
    }

    public static float getYawFromBlockFace(BlockFace face) {
        return switch (face) {
            case NORTH -> 180.0f;
            case EAST -> -90.0f;
            case SOUTH -> 0.0f;
            case WEST -> 90.0f;
            case NORTH_EAST -> -135.0f;
            case NORTH_WEST -> 135.0f;
            case SOUTH_EAST -> -45.0f;
            case SOUTH_WEST -> 45.0f;
            default -> 0.0f; // Default to SOUTH if face is not recognized
        };
    }

}
