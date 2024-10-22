package me.harpervenom.peakyBlocks.game;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import static me.harpervenom.peakyBlocks.utils.MapManager.removeWorld;

public class GameListener implements Listener {

    @EventHandler
    public void worldLeave(PlayerChangedWorldEvent e) {
        World world = e.getFrom();
        if (world.getName().equals("lobby")) return;
        if (!world.getPlayers().isEmpty()) return;

        removeWorld(world.getName());
    }

}
