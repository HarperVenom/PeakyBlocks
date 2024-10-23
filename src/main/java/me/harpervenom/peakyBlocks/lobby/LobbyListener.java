package me.harpervenom.peakyBlocks.lobby;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

import static me.harpervenom.peakyBlocks.PeakyBlocks.getPlugin;

public class LobbyListener implements Listener {

    @EventHandler
    public void PlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        p.setFoodLevel(20);
        p.setGameMode(GameMode.ADVENTURE);
        p.teleport(new Location(getPlugin().getServer().getWorld("lobby"), 0.5, 0, 0.5));
        e.setJoinMessage(ChatColor.GRAY + p.getDisplayName() + " в игре.");
    }

    @EventHandler
    public void PlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        e.setQuitMessage(ChatColor.GRAY + p.getDisplayName() + " больше не в игре.");
    }

    @EventHandler
    public void InventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (!p.getWorld().getName().equals("lobby")) return;
        if (p.getGameMode() == GameMode.CREATIVE) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void ItemDrop(PlayerDropItemEvent e) {
        if (!e.getPlayer().getWorld().getName().equals("lobby")) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void SwitchHands(PlayerSwapHandItemsEvent e) {
        if (!e.getPlayer().getWorld().getName().equals("lobby")) return;

        e.setCancelled(true);
    }
    @EventHandler
    public void PlayerDamage(EntityDamageEvent e){
        if (!e.getEntity().getWorld().getName().equals("lobby")) return;

        if (!(e.getEntity() instanceof Player)) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void Hunger(FoodLevelChangeEvent e){
        if (!e.getEntity().getWorld().getName().equals("lobby")) return;

        e.setCancelled(true);
    }

}

