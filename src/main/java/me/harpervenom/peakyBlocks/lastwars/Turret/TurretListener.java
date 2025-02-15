package me.harpervenom.peakyBlocks.lastwars.Turret;

import me.harpervenom.peakyBlocks.lastwars.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static me.harpervenom.peakyBlocks.lastwars.GamePlayer.getGamePlayer;
import static me.harpervenom.peakyBlocks.lastwars.GameTeam.getEntityTeam;
import static me.harpervenom.peakyBlocks.lastwars.Turret.Turret.getTurret;
import static me.harpervenom.peakyBlocks.lastwars.Turret.Turret.turrets;

public class TurretListener implements Listener {

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        Entity projectile = e.getEntity();
        Entity hitEntity = e.getHitEntity();
        if (hitEntity == null) return;
        if (!(e.getEntity().getShooter() instanceof Entity shooter)) return;

        projectile.setGravity(true);

        GameTeam targetTeam = getEntityTeam(hitEntity);
        GameTeam shooterTeam = getEntityTeam(shooter);

        if (targetTeam == null || shooterTeam == null) return;

        if (!targetTeam.equals(shooterTeam)) return;

        if (getTurret(hitEntity) == null){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void TurretDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof LivingEntity entity)) return;
        Turret turret = getTurret(entity);

        if (turret == null) return;
        double damage = e.getFinalDamage();

        Entity attacker = e.getDamager();
        boolean damaged = false;

        if (attacker instanceof Projectile projectile && projectile.getShooter() instanceof LivingEntity shooter) {
            damaged = turret.damage(shooter, damage);
        } else if (attacker instanceof LivingEntity livingAttacker) {
            damaged = turret.damage(livingAttacker, damage);
        } else if (e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION
                || e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
            damaged = turret.damage(attacker, damage);
        }

        if (!damaged){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void Damage(EntityDamageEvent e) {
        Entity entity = e.getEntity();
        Turret turret = getTurret(entity);
        if (turret == null) return;
        if (!(e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK
                || e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE
                || e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION
                || e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)
//                || e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK
        ) {
            e.setCancelled(true);
        }
    }

    public static List<Turret> destroyedTurrets = new ArrayList<>();
    @EventHandler
    public void ShooterDeath(SlimeSplitEvent e) {
        Slime entity = e.getEntity();
        List<Turret> listCopy = new ArrayList<>(destroyedTurrets);
        for (Turret turret : listCopy) {
            if (turret.getShooter().getUniqueId().equals(entity.getUniqueId())) {
                e.setCancelled(true);
                destroyedTurrets.remove(turret);
            }
        }
    }
    public static HashMap<UUID, UUID> attackers = new HashMap<>(); //attacker & target
    @EventHandler
    public void TeammateAttacked(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player target && e.getDamager() instanceof Player attacker)) return;
        attackers.put(attacker.getUniqueId(), target.getUniqueId());
    }


    @EventHandler
    public void DayBurn(EntityCombustEvent e) {
        Entity entity = e.getEntity();

        Turret turret = getTurret(entity);
        if (turret != null) e.setCancelled(true);
    }
}
