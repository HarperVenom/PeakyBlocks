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
import java.util.UUID;

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
        if (facing == null) return null;
        return switch (facing.toLowerCase()) {
            case "east" -> BlockFace.EAST;
            case "west" -> BlockFace.WEST;
            case "north" -> BlockFace.NORTH;
            case "south" -> BlockFace.SOUTH;
            case "north_east" -> BlockFace.NORTH_EAST;
            case "north_west" -> BlockFace.NORTH_WEST;
            case "south_east" -> BlockFace.SOUTH_EAST;
            case "south_west" -> BlockFace.SOUTH_WEST;
            default -> null;
        };
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
        // Normalize the yaw to a range of 0 - 360
        yaw = (yaw % 360 + 360) % 360;

        if (yaw >= 337.5 || yaw < 22.5) {
            return BlockFace.SOUTH;
        } else if (yaw >= 22.5 && yaw < 67.5) {
            return BlockFace.SOUTH_WEST;
        } else if (yaw >= 67.5 && yaw < 112.5) {
            return BlockFace.WEST;
        } else if (yaw >= 112.5 && yaw < 157.5) {
            return BlockFace.NORTH_WEST;
        } else if (yaw >= 157.5 && yaw < 202.5) {
            return BlockFace.NORTH;
        } else if (yaw >= 202.5 && yaw < 247.5) {
            return BlockFace.NORTH_EAST;
        } else if (yaw >= 247.5 && yaw < 292.5) {
            return BlockFace.EAST;
        } else {
            return BlockFace.SOUTH_EAST;
        }
    }

    public static boolean isValidUUID(String str) {
        try {
            UUID uuid = UUID.fromString(str); // Try to convert the string to a UUID
            return true; // If no exception is thrown, it's a valid UUID
        } catch (IllegalArgumentException e) {
            return false; // If an exception is thrown, it's not a valid UUID
        }
    }
}
