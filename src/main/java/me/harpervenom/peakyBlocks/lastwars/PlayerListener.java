package me.harpervenom.peakyBlocks.lastwars;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

import static me.harpervenom.peakyBlocks.PeakyBlocks.getPlugin;
import static me.harpervenom.peakyBlocks.lastwars.GamePlayer.getGamePlayer;

public class PlayerListener implements Listener {

    @EventHandler
    public void PlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        p.performCommand("lobby");
    }

    private final HashMap<UUID, Boolean> spawnPointUpdate = new HashMap<>();

    @EventHandler
    public void RespawnPointChange(PlayerSpawnChangeEvent e) {
        Player p = e.getPlayer();
        if (spawnPointUpdate.containsKey(p.getUniqueId())) {
            spawnPointUpdate.remove(p.getUniqueId());
            return;
        }
        GamePlayer gp = getGamePlayer(p);
        if (gp == null) return;

        Bukkit.getScheduler().runTaskLater(getPlugin(), new Runnable() {
            @Override
            public void run() {
                spawnPointUpdate.put(p.getUniqueId(), true);
                p.setRespawnLocation(gp.getTeam().getSpawn(), true);
            }
        }, 1);
    }

    @EventHandler
    public void PlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        GamePlayer gp = getGamePlayer(p);
        if (gp == null) return;

        Player killer = e.getEntity().getKiller();
        if (killer != null) {
            GamePlayer gKiller = getGamePlayer(killer);
            gp.addDeath(gKiller != null);
        }
    }

    @EventHandler
    public void PlayerRespawnEvent(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        GamePlayer gp = getGamePlayer(p);
        if (gp == null) return;
        Game game = gp.getTeam().getGame();

        int freezeSeconds = Math.min((int) (game.getTime() / 40), 15);

        gp.freeze(freezeSeconds);
    }

    @EventHandler
    public void PlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        GamePlayer gp = getGamePlayer(p);
        if (gp == null) return;
        if (gp.isFrozen() && hasPlayerMoved(e.getFrom(), e.getTo() == null ? e.getFrom() : e.getTo())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void PlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        GamePlayer gp = getGamePlayer(p);
        if (gp == null) return;
        if (gp.isFrozen()) {
            e.setCancelled(true);
        }
    }

    private boolean hasPlayerMoved(Location from, Location to) {
        return from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ();
    }
}
