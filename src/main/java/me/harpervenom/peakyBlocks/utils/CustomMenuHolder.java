package me.harpervenom.peakyBlocks.utils;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class CustomMenuHolder implements InventoryHolder {

    private final String menuType;

    public CustomMenuHolder(String menuType) {
        this.menuType = menuType;
    }

    public String getType() {
        return menuType;
    }

    @Override
    public Inventory getInventory() {
        return null;  // Not needed unless you want to return a specific inventory.
    }
}


