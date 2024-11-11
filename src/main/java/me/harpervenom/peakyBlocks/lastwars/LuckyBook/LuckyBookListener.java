package me.harpervenom.peakyBlocks.lastwars.LuckyBook;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.UUID;

import static me.harpervenom.peakyBlocks.PeakyBlocks.getPlugin;
import static me.harpervenom.peakyBlocks.lastwars.LuckyBook.LuckyBook.*;

public class LuckyBookListener implements Listener {

    public HashMap<UUID, Boolean> cooldown = new HashMap<>();

    @EventHandler
    public void PlayerConsumeLuckyBook(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getHand() != EquipmentSlot.HAND) return;
        ItemStack item = e.getItem();
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        String name = item.getItemMeta().getDisplayName();


        ItemStack book = getPurchase(name);
        if (book == null){
            if (cooldown.containsKey(p.getUniqueId())) {
                e.setCancelled(true);
            }
            return;
        }

        if (item.getAmount() > 1) {
            ItemStack remaining = new ItemStack(item);
            remaining.setAmount(item.getAmount() - 1);
            p.getInventory().setItem(p.getInventory().getHeldItemSlot(), remaining);
        } else {
            p.getInventory().setItem(p.getInventory().getHeldItemSlot(), null);
        }

        giveLootToPlayer(p, name);
        cooldown.put(p.getUniqueId(), true);
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            cooldown.remove(p.getUniqueId());
        }, 20);
    }
}
