package me.harpervenom.peakyBlocks.lastwars.Spawner;

import me.harpervenom.peakyBlocks.lastwars.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRemoveEvent;

import java.util.ArrayList;
import java.util.List;

import static me.harpervenom.peakyBlocks.PeakyBlocks.getPlugin;
import static me.harpervenom.peakyBlocks.lastwars.GamePlayer.getGamePlayer;
import static me.harpervenom.peakyBlocks.lastwars.Spawner.Spawner.getEntitySpawner;

public class SpawnerListener implements Listener {

    @EventHandler
    public void EntityDeath(EntityDeathEvent e) {
        LivingEntity entity = e.getEntity();

        e.setDroppedExp(0);
        if (!(entity instanceof Player)) e.getDrops().clear();

        Spawner spawner = getEntitySpawner(entity);

        if (spawner == null) return;
        spawner.killEntity(entity);

        if (e.getEntityType() == EntityType.SLIME || e.getEntityType() == EntityType.MAGMA_CUBE) {
            Slime dyingSlime = (Slime) entity;

            if (dyingSlime.getSize() > 1) {
                Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
                    List<Slime> smallSlimes = new ArrayList<>();
                    for (Entity currentEntity : dyingSlime.getNearbyEntities(5, 5, 5)) {
                        if (currentEntity instanceof Slime smallSlime) {
                            if (smallSlime.getSize() < dyingSlime.getSize()) {
                                smallSlimes.add(smallSlime);
                            }
                        }
                    }
                    for (Slime slime : smallSlimes) {
                        spawner.addChildEntity(slime);
                    }
                }, 30L);
            }
        }
    }

    @EventHandler
    public void EntityDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof LivingEntity entity) {
            Spawner spawner = getEntitySpawner(entity);

            if (spawner == null) return;
            e.setDamage(0.1);
        }
    }

    @EventHandler
    public void PlayerKillEntity(EntityDeathEvent e) {
        LivingEntity entity = e.getEntity();

        if (entity.getKiller() instanceof Player killer) {
            GamePlayer gp = getGamePlayer(killer);
            if (gp == null) return;

            double entityMaxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
            int exp = (int) Math.max(entityMaxHealth * (entity.getType() == EntityType.MAGMA_CUBE ? 4 : 2), 1);

            gp.changeBalance(exp);
        }
    }
}
