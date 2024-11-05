package me.harpervenom.peakyBlocks.lastwars;

import me.harpervenom.peakyBlocks.lastwars.Core.Core;
import me.harpervenom.peakyBlocks.lastwars.Map.Map;
import me.harpervenom.peakyBlocks.lastwars.Turret.Turret;
import me.harpervenom.peakyBlocks.lastwars.Core.CoreDestroyedEvent;
import me.harpervenom.peakyBlocks.lastwars.Turret.TurretDestroyEvent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSpawnChangeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static me.harpervenom.peakyBlocks.PeakyBlocks.getPlugin;
import static me.harpervenom.peakyBlocks.lastwars.Game.getGameByWorld;
import static me.harpervenom.peakyBlocks.lastwars.GamePlayer.getGamePlayer;
import static me.harpervenom.peakyBlocks.lastwars.Map.MapManager.removeWorld;

public class GameListener implements Listener {

    public static List<Location> noDamageExplosions = new ArrayList<>();
    public static List<Location> destructExplosions = new ArrayList<>();

    @EventHandler
    public void BlockBreak(BlockBreakEvent e) {
        Block b = e.getBlock();
        World world = b.getWorld();
        Game game = getGameByWorld(world);
        if (game == null) return;
        Map map = game.getMap();

        if (map.containsBlock(b)) e.setCancelled(true);
    }

    @EventHandler
    public void BlockPlace(BlockPlaceEvent e) {
        Block b = e.getBlock();
        World world = b.getWorld();
        Game game = getGameByWorld(world);
        if (game == null) return;

        if (game.isBlockProtected(b)) {
            e.setCancelled(true);
            Player p = e.getPlayer();
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "Территория защищена!"));
        }
    }

    @EventHandler
    public void onLiquidFlow(BlockFromToEvent e) {
        Block b = e.getBlock();
        World world = b.getWorld();
        Game game = getGameByWorld(world);
        if (game == null) return;

        Block toBlock = e.getToBlock();

        // Check if the target block is within the protected area
        if (game.isBlockProtected(toBlock)) {
            e.setCancelled(true); // Prevent the liquid from flowing into the protected area
        }
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent e) {
        Block b = e.getBlock();
        World world = b.getWorld();
        Game game = getGameByWorld(world);
        if (game == null) return;

        Block targetBlock = e.getBlockClicked().getRelative(e.getBlockFace());

        if (game.isBlockProtected(targetBlock)) {
            e.setCancelled(true);
            Player p = e.getPlayer();
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "Территория защищена!"));
        }
    }

    @EventHandler
    public void worldLeave(PlayerChangedWorldEvent e) {
        World world = e.getFrom();
        if (world.getName().equals("lobby")) return;

        if (world.getPlayers().isEmpty()) {
            removeWorld(world);
        }

        GamePlayer p = getGamePlayer(e.getPlayer());
        if (p == null) return;
        p.remove();
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

        team.destroyTurret(turret);
        game.sendMessage(turret.getShooter().getCustomName() + ChatColor.WHITE + " разрушена!");
    }

    @EventHandler
    public void onExplosionDamage(EntityDamageEvent e) {
        if (e.getCause() != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) return;

        Location entityLoc = e.getEntity().getLocation();

        for (Location explosionLoc : noDamageExplosions) {
            if (explosionLoc.distance(entityLoc) < 6) {
                e.setCancelled(true);
                break;
            }
        }
    }

}
