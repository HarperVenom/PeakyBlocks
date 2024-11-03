package me.harpervenom.peakyBlocks.lastwars.Turret;

import me.harpervenom.peakyBlocks.lastwars.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import static me.harpervenom.peakyBlocks.lastwars.Game.activeGames;
import static me.harpervenom.peakyBlocks.lastwars.GamePlayer.getGamePlayer;
import static me.harpervenom.peakyBlocks.lastwars.Turret.Turret.turrets;

public class TurretListener implements Listener {

    @EventHandler
    public void EnemyMove(PlayerMoveEvent e) {
        Location from = e.getFrom();
        Location to = e.getTo();
        if (to == null || !hasPlayerMoved(from, to)) return;

        World world = e.getTo().getWorld();
        if (world == null) return;
        for (Game game : activeGames) {
            if (game.getWorld().getName().equals(world.getName())) {
                for (GameTeam team : game.getTeams()) {
                    Turret turret = team.getTurret();
                    if (turret == null) return;
                    turret.scanArea();
                }
            }
        }
    }

    private boolean hasPlayerMoved(Location from, Location to) {
        return from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ();
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        // Check if the projectile is an arrow
        if (event.getEntity() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getEntity();
            Entity hitEntity = event.getHitEntity();
            arrow.setGravity(true);
            if (hitEntity == null) return;
            if (hitEntity instanceof Player p) {
                GamePlayer gp = getGamePlayer(p);
                if (gp == null) return;

                Entity shooter = (Entity) arrow.getShooter();

                if (shooter instanceof Villager) {
                    String shooterTeamName = shooter.getCustomName();

                    GameTeam hitTeam = gp.getTeam();

                    if (hitTeam.getName().equals(shooterTeamName)) {
                        event.setCancelled(true); // Cancel the hit event
                        arrow.setVelocity(arrow.getVelocity());
                    } else {
                        Bukkit.broadcastMessage("here");

                    }
                }
            }
        }
    }

    @EventHandler
    public void TurretDamage(BlockBreakEvent e) {
        Player p = e.getPlayer();
        GamePlayer gp = getGamePlayer(p);
        Block b = e.getBlock();

        if (gp == null) {
            e.setCancelled(true);
            return;
        }

        int turretsSize = turrets.size();
        for (int i = 0; i < turretsSize; i++) {
            Turret turret = turrets.get(i);
            if (turret == null) return;
            if (turret.getBlock().equals(b.getLocation())) {
                e.setCancelled(true);

                GameTeam team = turret.getTeam();
                if (gp.getTeam().equals(team)) {
                    return;
                }

                turret.damage(p);
            }
        }
    }
}
