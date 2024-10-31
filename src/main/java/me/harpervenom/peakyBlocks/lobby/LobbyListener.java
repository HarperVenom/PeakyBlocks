package me.harpervenom.peakyBlocks.lobby;

import me.harpervenom.peakyBlocks.classes.queue.QueuePlayer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

import static me.harpervenom.peakyBlocks.PeakyBlocks.getPlugin;
import static me.harpervenom.peakyBlocks.classes.queue.QueuePlayer.queuePlayers;
import static me.harpervenom.peakyBlocks.lobby.MenuListener.navigator;

public class LobbyListener implements Listener {

    @EventHandler
    public void PlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        e.setJoinMessage(ChatColor.GRAY + p.getDisplayName() + " в игре.");
        p.teleport(new Location(getPlugin().getServer().getWorld("lobby"), 0.5, 0, 0.5));

        setLobbyState(p);

        queuePlayers.add(new QueuePlayer(p.getUniqueId()));
    }

    public static void setLobbyState(Player p) {
        p.setFoodLevel(20);
        p.setGameMode(GameMode.ADVENTURE);

        PlayerInventory inv = p.getInventory();
        inv.clear();
        inv.setItem(0, navigator);
        inv.setHeldItemSlot(0);
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

