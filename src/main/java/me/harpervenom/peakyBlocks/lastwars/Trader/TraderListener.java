package me.harpervenom.peakyBlocks.lastwars.Trader;

import me.harpervenom.peakyBlocks.lastwars.GamePlayer;
import me.harpervenom.peakyBlocks.utils.CustomMenuHolder;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
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

import java.util.HashMap;

import static me.harpervenom.peakyBlocks.lastwars.GamePlayer.getGamePlayer;
import static me.harpervenom.peakyBlocks.lastwars.Trader.Good.getGood;
import static me.harpervenom.peakyBlocks.lastwars.Trader.Good.getGoodId;
import static me.harpervenom.peakyBlocks.lastwars.Trader.Trader.*;
import static me.harpervenom.peakyBlocks.lobby.MenuListener.getCustomMenuHolder;

public class TraderListener implements Listener {

//    public static HashMap<String, Integer> goodsPrices = new HashMap<>();

    @EventHandler
    public void TraderInteract(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        Entity entity = e.getRightClicked();

        if (!(entity instanceof Villager)) return;

        Trader trader = getTrader(entity.getUniqueId());
        if (trader == null) return;
        e.setCancelled(true);
        p.openMerchant(trader.getShop(), true);
    }

//    @EventHandler
//    public void BuyGood(InventoryClickEvent e) {
//        CustomMenuHolder holder = getCustomMenuHolder(e);
//        if (holder == null || !holder.getType().equals("traderMenu")) return;
//
//        e.setCancelled(true);
//
//        if (e.getClick() != ClickType.LEFT && e.getClick() != ClickType.SHIFT_LEFT) return;
//
//        Inventory inv = e.getClickedInventory();
//        if (inv == null || inv.getType() == InventoryType.PLAYER) return;
//
//        Player p = (Player) e.getWhoClicked();
//        GamePlayer gp = getGamePlayer(p);
//        if (gp == null) return;
//        ItemStack item = e.getCurrentItem();
//        if (item == null) return;
//
//        Integer goodId = getGoodId(item);
//        if (goodId == null) return;
//        Good good = getGood(goodId);
//        if (good == null) return;
//        int price = good.getPrice();
//
//        if (gp.getBalance() < price) {
//            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 1);
//            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "Недостаточно опыта"));
//            return;
//        }
//
//        ItemStack purchase = good.getItem();
//        if (purchase == null) return;
//        purchase = new ItemStack(purchase);
//
//        if (e.getClick() == ClickType.SHIFT_LEFT) {
//            int multiplier = gp.getBalance() / price;
//            price *= multiplier;
//
//            purchase.setAmount(purchase.getAmount() * multiplier);
//        }
//
//        gp.changeBalance(-price);
//
//        HashMap<Integer, ItemStack> remaining = p.getInventory().addItem(purchase);
//
//        for (ItemStack droppedItem : remaining.values()) {
//            p.getWorld().dropItemNaturally(p.getLocation(), droppedItem);
//        }
//    }
}
