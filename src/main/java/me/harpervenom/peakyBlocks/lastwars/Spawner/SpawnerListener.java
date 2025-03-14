package me.harpervenom.peakyBlocks.lastwars.Spawner;

import me.harpervenom.peakyBlocks.lastwars.GamePlayer;
import me.harpervenom.peakyBlocks.lastwars.Turret.Turret;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static me.harpervenom.peakyBlocks.PeakyBlocks.getPlugin;
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
    public void EntityDeath(EntityDeathEvent e) {
        LivingEntity entity = e.getEntity();

        if (!(entity instanceof Player)){
            e.getDrops().clear();
            e.setDroppedExp(0);
        }

        if (entity instanceof Slime slime) {
            int size = slime.getSize();
            Material material = entity instanceof MagmaCube ? Material.NETHER_BRICK : Material.EMERALD;
            ItemStack drop = new ItemStack(material, size * (entity instanceof MagmaCube ? 3 : 2));
            e.getDrops().add(drop);

            if (size > 1) return;
        }
        Spawner spawner = getEntitySpawner(entity);
        if (spawner == null) return;
        spawner.killEntity(entity);
    }

    @EventHandler
    public void onSlimeSplit(SlimeSplitEvent e) {
        Slime slime = e.getEntity();
        Spawner spawner = getEntitySpawner(slime);
        if (spawner == null) return;
        spawner.killEntity(slime);
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
