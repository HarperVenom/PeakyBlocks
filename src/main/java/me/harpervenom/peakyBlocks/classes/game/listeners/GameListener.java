package me.harpervenom.peakyBlocks.classes.game.listeners;

import me.harpervenom.peakyBlocks.classes.game.Core;
import me.harpervenom.peakyBlocks.classes.game.Game;
import me.harpervenom.peakyBlocks.classes.game.GamePlayer;
import me.harpervenom.peakyBlocks.classes.game.GameTeam;
import me.harpervenom.peakyBlocks.classes.game.evens.CoreDestroyedEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.List;

import static me.harpervenom.peakyBlocks.PeakyBlocks.getPlugin;
import static me.harpervenom.peakyBlocks.classes.game.GamePlayer.getGamePlayer;

public class GameListener implements Listener {

    public static HashMap<Chunk, List<Location>> placedBlocks = new HashMap<>();

    @EventHandler
    public void BlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        if (p.getGameMode() == GameMode.CREATIVE) return;
        Block b = e.getBlock();
        Chunk chunk = b.getChunk();
        if (placedBlocks.containsKey(chunk) && placedBlocks.get(chunk).contains(b.getLocation())) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void worldLeave(PlayerChangedWorldEvent e) {
        World world = e.getFrom();
        if (world.getName().equals("lobby")) return;

        GamePlayer p = getGamePlayer(e.getPlayer());
        if (p == null) return;
        p.remove();
    }

    @EventHandler
    public void PlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        p.performCommand("lobby");
    }

    @EventHandler
    public void OnCoreDestroyed(CoreDestroyedEvent e) {
        Core core = e.getCore();
        GameTeam team = core.getTeam();
        Game game = team.getGame();

        game.teams.remove(team);
        game.deadTeams.add(team);

        if (game.teams.size() <= 1) {
            GameTeam winner = game.teams.getFirst();
            winner.win();
            team.loose(winner);
            Bukkit.getScheduler().runTaskLater(getPlugin(), new Runnable() {
                @Override
                public void run() {
                    game.finish();
                }
            },120);
        }
    }

}
