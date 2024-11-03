package me.harpervenom.peakyBlocks.utils;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Utils {

    public static ItemStack createItem(Material material, String title, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
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

    public static float getYaw(String facing) {
        BlockFace face = getBlockFace(facing);
        if (face == null) return 0;
        return getYawFromBlockFace(face);
    }

    public static BlockFace getBlockFace(String facing) {
        switch (facing) {
            case "east" -> {
                return BlockFace.EAST;
            }
            case "west" -> {
                return BlockFace.WEST;
            }
            case "north" -> {
                return BlockFace.NORTH;
            }
            case "south" -> {
                return BlockFace.SOUTH;
            }
        }
        return null;
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

    public static BlockFace yawToFace(float yaw) {
        // Normalize the yaw to 0 - 360
        yaw = (yaw % 360 + 360) % 360;

        if (yaw >= 315 || yaw < 45) {
            return BlockFace.SOUTH;
        } else if (yaw >= 45 && yaw < 135) {
            return BlockFace.WEST;
        } else if (yaw >= 135 && yaw < 225) {
            return BlockFace.NORTH;
        } else {
            return BlockFace.EAST;
        }
    }
}
