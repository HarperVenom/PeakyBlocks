package me.harpervenom.peakyBlocks.lastwars;

import me.harpervenom.peakyBlocks.lastwars.Core.Core;
import me.harpervenom.peakyBlocks.lastwars.Turret.Turret;
import me.harpervenom.peakyBlocks.lastwars.Core.CoreDestroyedEvent;
import me.harpervenom.peakyBlocks.lastwars.Turret.TurretDestroyEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSpawnChangeEvent;

import java.util.HashMap;
import java.util.List;

import static me.harpervenom.peakyBlocks.PeakyBlocks.getPlugin;
import static me.harpervenom.peakyBlocks.lastwars.GamePlayer.getGamePlayer;

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
        GameTeam looser = core.getTeam();
        Game game = looser.getGame();

        looser.destroyCore();

        game.teams.remove(looser);
        game.deadTeams.add(looser);

        if (game.teams.size() <= 1) {
            GameTeam winner = game.teams.getFirst();
            game.sendMessage(looser.getColor() + "[Ядро]" + ChatColor.WHITE + " разрушено!");
            winner.win();
            looser.loose();
            game.sendMessage("Комнада " + winner.getColor() + "'" + winner.getName() + "'" + ChatColor.WHITE + " одержала победу!");
            Bukkit.getScheduler().runTaskLater(getPlugin(), new Runnable() {
                @Override
                public void run() {
                    game.finish();
                }
            },120);
        }
    }

    @EventHandler
    public void OnTurretDestroy(TurretDestroyEvent e) {
        Turret turret = e.getTurret();
        GameTeam team = turret.getTeam();
        Game game = team.getGame();

        team.destroyTurret();
        game.sendMessage(turret.getShooter().getCustomName() + ChatColor.WHITE + " разрушена!");
    }

    @EventHandler
    public void RespawnPointChange(PlayerSpawnChangeEvent e) {
        Player p = e.getPlayer();
        GamePlayer gp = getGamePlayer(p);
        if (gp == null) return;

        Bukkit.getScheduler().runTaskLater(getPlugin(), new Runnable() {
            @Override
            public void run() {
                p.setRespawnLocation(gp.getTeam().getSpawn(), true);
            }
        }, 1);
    }

}
