package me.harpervenom.peakyBlocks.lastwars.Trader;

import me.harpervenom.peakyBlocks.utils.CustomMenuHolder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

import static me.harpervenom.peakyBlocks.lastwars.LuckyBook.LuckyBook.luckyBook;
import static me.harpervenom.peakyBlocks.lastwars.LuckyBook.LuckyBook.luckyBookName;
import static me.harpervenom.peakyBlocks.lastwars.Trader.Trader.traderMenu;
import static me.harpervenom.peakyBlocks.lastwars.Trader.Trader.traderName;
import static me.harpervenom.peakyBlocks.lobby.MenuListener.getCustomMenuHolder;

public class TraderListener implements Listener {

    @EventHandler
    public void TraderInteract(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        Entity entity = e.getRightClicked();

        if (entity instanceof Villager trader) {

            if (traderName.equals(trader.getCustomName())) {
                e.setCancelled(true);

                p.openInventory(traderMenu);
            }
        }
    }

    @EventHandler
    public void BuyLuckyBook(InventoryClickEvent e) {
        CustomMenuHolder holder = getCustomMenuHolder(e);
        if (holder == null || !holder.getType().equals("traderMenu")) return;

        e.setCancelled(true);

        if (e.getClick() != ClickType.LEFT) return;

        Inventory inv = e.getClickedInventory();
        if (inv == null || inv.getType() == InventoryType.PLAYER) return;

        Player p = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !luckyBookName.equals(meta.getDisplayName())) return;

        HashMap<Integer, ItemStack> remaining = p.getInventory().addItem(luckyBook);

        for (ItemStack droppedItem : remaining.values()) {
            p.getWorld().dropItemNaturally(p.getLocation(), droppedItem);
        }
    }
}
