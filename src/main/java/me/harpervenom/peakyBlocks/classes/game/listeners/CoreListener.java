package me.harpervenom.peakyBlocks.classes.game.listeners;

import me.harpervenom.peakyBlocks.classes.game.Core;
import me.harpervenom.peakyBlocks.classes.game.Game;
import me.harpervenom.peakyBlocks.classes.game.GamePlayer;
import me.harpervenom.peakyBlocks.classes.game.GameTeam;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import static me.harpervenom.peakyBlocks.classes.game.Core.cores;
import static me.harpervenom.peakyBlocks.classes.game.Game.activeGames;
import static me.harpervenom.peakyBlocks.classes.game.GamePlayer.getGamePlayer;

public class CoreListener implements Listener {

    @EventHandler
    public void CoreDamage(BlockBreakEvent e) {
        Player p = e.getPlayer();
        GamePlayer gp = getGamePlayer(p);
        Block b = e.getBlock();

        if (gp == null) {
            e.setCancelled(true);
            return;
        }

        for (Core core : cores) {
            if (core.getBlock().equals(b.getLocation())) {
                e.setCancelled(true);

                GameTeam team = core.getTeam();
                if (gp.getTeam().equals(team)) return;

                core.damage(p);
            }
        }
    }
}
