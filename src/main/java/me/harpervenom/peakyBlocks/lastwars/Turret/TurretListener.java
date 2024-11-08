package me.harpervenom.peakyBlocks.lastwars.Turret;

import me.harpervenom.peakyBlocks.lastwars.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import static me.harpervenom.peakyBlocks.lastwars.GamePlayer.getGamePlayer;
import static me.harpervenom.peakyBlocks.lastwars.GameTeam.getEntityTeam;
import static me.harpervenom.peakyBlocks.lastwars.Turret.Turret.turrets;

public class TurretListener implements Listener {

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Arrow arrow)) return;

        Entity hitEntity = event.getHitEntity();
        if (hitEntity == null) return;
        if (!(arrow.getShooter() instanceof ArmorStand shooter)) return;

        arrow.setGravity(true);

        GameTeam targetTeam = getEntityTeam(hitEntity);
        GameTeam shooterTeam = getEntityTeam(shooter);

        if (targetTeam == null || shooterTeam == null) return;

        if (!targetTeam.equals(shooterTeam)) return;

        event.setCancelled(true);
        arrow.setVelocity(arrow.getVelocity());
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

                GameTeam team = turret.getTeam();
                if (gp.getTeam().equals(team)) {
                    return;
                }

                turret.damage(p);
            }
        }
    }
}
