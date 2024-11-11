package me.harpervenom.peakyBlocks.lastwars.Trader;

import me.harpervenom.peakyBlocks.lastwars.GamePlayer;
import me.harpervenom.peakyBlocks.utils.CustomMenuHolder;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
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

import static me.harpervenom.peakyBlocks.lastwars.GamePlayer.getGamePlayer;
import static me.harpervenom.peakyBlocks.lastwars.LuckyBook.LuckyBook.*;
import static me.harpervenom.peakyBlocks.lastwars.Trader.Trader.traderMenu;
import static me.harpervenom.peakyBlocks.lastwars.Trader.Trader.traderName;
import static me.harpervenom.peakyBlocks.lobby.MenuListener.getCustomMenuHolder;

public class TraderListener implements Listener {

    public static HashMap<String, Integer> goodsPrices = new HashMap<>();

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

        if (e.getClick() != ClickType.LEFT && e.getClick() != ClickType.SHIFT_LEFT) return;

        Inventory inv = e.getClickedInventory();
        if (inv == null || inv.getType() == InventoryType.PLAYER) return;

        Player p = (Player) e.getWhoClicked();
        GamePlayer gp = getGamePlayer(p);
        if (gp == null) return;
        ItemStack item = e.getCurrentItem();
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        String name = meta.getDisplayName();

        if (!goodsPrices.containsKey(name)) return;
        int price = goodsPrices.get(name);

        if (gp.getBalance() < price) {
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 1);
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "Недостаточно опыта"));
            return;
        }

        ItemStack purchase = getPurchase(name);
        if (purchase == null) return;
        purchase = new ItemStack(purchase);

        if (e.getClick() == ClickType.SHIFT_LEFT) {
            int amount = gp.getBalance() / price;
            price *= amount;

            purchase.setAmount(amount);
        }

        gp.changeBalance(-price);

        HashMap<Integer, ItemStack> remaining = p.getInventory().addItem(purchase);

        for (ItemStack droppedItem : remaining.values()) {
            p.getWorld().dropItemNaturally(p.getLocation(), droppedItem);
        }
    }
}
