package me.harpervenom.peakyBlocks.lastwars.Spawner;

import me.harpervenom.peakyBlocks.lastwars.GamePlayer;
import me.harpervenom.peakyBlocks.lastwars.Turret.Turret;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static me.harpervenom.peakyBlocks.PeakyBlocks.getPlugin;
import static me.harpervenom.peakyBlocks.lastwars.GamePlayer.getGamePlayer;
import static me.harpervenom.peakyBlocks.lastwars.Spawner.Spawner.getEntitySpawner;

public class SpawnerListener implements Listener {

    @EventHandler
    public void EntityDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof LivingEntity entity) {
            Spawner spawner = getEntitySpawner(entity);

            if (spawner == null) return;
            e.setDamage(0.5);
        }
    }

    @EventHandler
    public void PlayerKillEntity(EntityDeathEvent e) {
        LivingEntity entity = e.getEntity();

        Turret turret = Turret.getTurret(entity);
        if (turret != null) return;

        if (entity.getKiller() instanceof Player killer) {
            GamePlayer gp = getGamePlayer(killer);
            if (gp == null) return;

            double entityMaxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
            int exp = (int) (entityMaxHealth * 3);
            if (entity.getType().equals(EntityType.CAVE_SPIDER) || entity.getType().equals(EntityType.MAGMA_CUBE)) {
                exp = (int) (entityMaxHealth * 7);
            }

            gp.changeBalance(exp);
        }
    }

    @EventHandler
    public void EntityDeath(EntityDeathEvent e) {
        Entity entity = e.getEntity();
        if (!(entity instanceof Player)){
            e.getDrops().clear();
            e.setDroppedExp(0);
        }
    }

    @EventHandler
    public void onSlimeSplit(SlimeSplitEvent e) {
        Slime slime = e.getEntity();
        Spawner spawner = getEntitySpawner(slime);
        if (spawner == null) return;

        // Schedule a task to run right after the split, to find smaller slimes nearby
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            List<Slime> childSlimes = new ArrayList<>();

            for (Entity nearbyEntity : slime.getNearbyEntities(5, 5, 5)) {
                if (nearbyEntity instanceof Slime childSlime) {
                    // Check if the nearby slime is smaller than the original
                    if (childSlime.getSize() < slime.getSize()) {
                        childSlimes.add(childSlime);
                    }
                }
            }

            for (Slime childSlime : childSlimes) {
                if (spawner.containsEntity(childSlime)) continue;
                spawner.addChildEntity(childSlime);
            }
        }, 1L);
    }

    public static List<Entity> newlySpawned = new ArrayList<>();
    @EventHandler
    public void EntitySpawn(CreatureSpawnEvent e){
        Entity entity = e.getEntity();
        newlySpawned.add(entity);
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            newlySpawned.remove(entity);
        }, 5);
    }

    @EventHandler
    public void NewEntityDamage(EntityDamageByEntityEvent e) {
        Entity attacker = e.getDamager();
        if (newlySpawned.contains(attacker)) {
            e.setCancelled(true);
        }
    }
}
