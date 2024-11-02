package me.harpervenom.peakyBlocks.classes.game.Core.LuckyBook;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static me.harpervenom.peakyBlocks.classes.game.Core.LuckyBook.LuckyBook.giveLootToPlayer;
import static me.harpervenom.peakyBlocks.classes.game.Core.LuckyBook.LuckyBook.luckyBookName;

public class LuckyBookListener implements Listener {

    @EventHandler
    public void PlayerConsumeLuckyBook(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getHand() != EquipmentSlot.HAND) return;
        ItemStack item = e.getItem();
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !luckyBookName.equals(item.getItemMeta().getDisplayName())) return;

        if (item.getAmount() > 1) {
            ItemStack remaining = new ItemStack(item);
            remaining.setAmount(item.getAmount() - 1);
            p.getInventory().setItem(p.getInventory().getHeldItemSlot(), remaining);
        } else {
            p.getInventory().setItem(p.getInventory().getHeldItemSlot(), null);
        }

        giveLootToPlayer(p);
    }
}
