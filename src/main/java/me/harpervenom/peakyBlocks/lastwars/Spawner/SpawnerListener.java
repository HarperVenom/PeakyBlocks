package me.harpervenom.peakyBlocks.lastwars.Spawner;

import me.harpervenom.peakyBlocks.lastwars.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRemoveEvent;

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
            int exp = (int) Math.max(entityMaxHealth * (entity.getType() == EntityType.MAGMA_CUBE ? 3 : 2), 1);

            gp.changeBalance(exp);
        }
    }
}
