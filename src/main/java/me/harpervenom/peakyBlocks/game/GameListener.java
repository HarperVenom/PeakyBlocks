package me.harpervenom.peakyBlocks.game;

import me.harpervenom.peakyBlocks.classes.game.GamePlayer;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static me.harpervenom.peakyBlocks.classes.game.GamePlayer.getGamePlayer;
import static me.harpervenom.peakyBlocks.utils.MapManager.removeWorld;

public class GameListener implements Listener {

    @EventHandler
    public void worldLeave(PlayerChangedWorldEvent e) {
        World world = e.getFrom();
        if (world.getName().equals("lobby")) return;
        if (!world.getPlayers().isEmpty()) return;

        removeWorld(world);
    }

    @EventHandler
    public void PlayerQuit(PlayerQuitEvent e) {
        GamePlayer p = getGamePlayer(e.getPlayer());
        if (p == null) return;
        p.getTeam().removePlayer(p);
    }

}
