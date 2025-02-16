package me.harpervenom.peakyBlocks.lastwars.Spawner;

import me.harpervenom.peakyBlocks.lastwars.Game;
import me.harpervenom.peakyBlocks.lastwars.GamePlayer;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

import static me.harpervenom.peakyBlocks.PeakyBlocks.getPlugin;
import static me.harpervenom.peakyBlocks.lastwars.Game.getGameByWorld;

public class Spawner {

    public static Spawner getEntitySpawner(LivingEntity entity) {
        Game game = getGameByWorld(entity.getWorld());
        if (game == null) return null;
        for (Spawner spawner : game.getSpawners()) {
            if (spawner.entities.contains(entity) || spawner.childEntities.contains(entity)) return spawner;
        }
        return null;
    }

    private final Location location;
    private final EntityType type;

    private BukkitTask task;

    private final Set<LivingEntity> entities = new HashSet<>();
    private final Set<LivingEntity> childEntities = new HashSet<>();

    public Spawner(Location location, EntityType type) {
        this.location = location.add(0.5, 1, 0.5);
        this.type = type;
    }

    public Spawner(Spawner sample) {
        this.location = sample.location.clone();
        this.type = sample.type;
        task = null;
    }

    public void start() {
        scheduleRun();
    }

    private void scheduleRun() {
        if (task != null && !task.isCancelled()) return;

        entities.removeIf(Entity::isDead);
        int baseMaxAmount = 4;
        int maxAmount = baseMaxAmount + Math.max((getGame().getPlayers().size() - 2 + 1) / 2, 0);
        if (entities.size() >= maxAmount) return;

        int delay = type == EntityType.SLIME ? 40 : 120;

        task = Bukkit.getScheduler().runTaskLater(getPlugin(), this::run, delay * 20);
    }

    public void run() {
//        getGame().getPlayers();
        spawnMob(location, type);
        task = null;
        scheduleRun();
    }

    public void spawnMob(Location centerLocation, EntityType type) {
        World world = location.getWorld();
        Random random = new Random();

        double offsetX = -1 + (random.nextDouble() * 2);
        double offsetZ = -1 + (random.nextDouble() * 2);

        Location spawnLocation = centerLocation.clone().add(offsetX, 0, offsetZ);

        LivingEntity entity = (LivingEntity) world.spawnEntity(spawnLocation.clone().add(0, 1, 0), type);

        world.spawnParticle(Particle.FLAME, spawnLocation, 20, 0.2, 0.5, 0.2, 0.05);

        entity.getAttribute(Attribute.FOLLOW_RANGE).setBaseValue(8);
        entity.setRemoveWhenFarAway(false);
        entity.setSilent(true);

        if (entity instanceof Slime slime) {
            slime.setSize(2);
        }
        if (entity instanceof MagmaCube) {
            for (GamePlayer gp : getGame().getPlayers()) {
                gp.getPlayer().playSound(gp.getPlayer().getLocation(), Sound.ENTITY_BLAZE_SHOOT, 0.3f, 0.5f);
            }
        }

        entities.add(entity);
    }

    public boolean containsEntity(Entity entity) {
        for (LivingEntity child : childEntities) {
            if (child.getUniqueId().equals(entity.getUniqueId())) return true;
        }
        return false;
    }

    public void addChildEntity(LivingEntity entity) {
        childEntities.add(entity);
        entity.setSilent(true);
    }

    public void killEntity(LivingEntity entity) {
        entities.remove(entity);
        childEntities.remove(entity);

        scheduleRun();
    }

    public void setWorld(World world) {
        location.setWorld(world);
    }

    public Location getLocation() {
        return location;
    }

    public EntityType getType() {
        return type;
    }

    public Game getGame() {
        return getGameByWorld(location.getWorld());
    }

    public void stop() {
        if (task == null) return;
        if (!task.isCancelled()) {
            task.cancel();
        }
    }
}
