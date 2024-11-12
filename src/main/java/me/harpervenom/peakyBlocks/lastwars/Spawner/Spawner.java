package me.harpervenom.peakyBlocks.lastwars.Spawner;

import me.harpervenom.peakyBlocks.lastwars.Game;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;

import java.util.*;

import static me.harpervenom.peakyBlocks.lastwars.Game.getGameByWorld;

public class Spawner {

    private static List<Spawner> spawnerSamples = new ArrayList<>();

    public static Spawner getEntitySpawner(LivingEntity entity) {
        Game game = getGameByWorld(entity.getWorld());
        if (game == null) return null;
        for (Spawner spawner : game.getSpawners()) {
            if (spawner.entities.contains(entity) || spawner.childEntities.contains(entity)) return spawner;
        }
        return null;
    }

    private Location location;
    private EntityType type;
    private int baseMaxAmount = 4;

    private Set<LivingEntity> entities = new HashSet<>();
    private Set<LivingEntity> childEntities = new HashSet<>();

    public Spawner(Location location, EntityType type) {
        this.location = location.add(0.5, 1, 0.5);
        this.type = type;

        spawnerSamples.add(this);
    }

    public Spawner(Spawner sample) {
        this.location = sample.location.clone();
        this.type = sample.type;
    }

    public void run() {
        entities.removeIf(Entity::isDead);

        int maxAmount = baseMaxAmount + Math.max((getGame().getPlayers().size() - 2 + 1) / 2, 0);

        if (entities.size() >= maxAmount) return;
        int amount = Math.min(maxAmount - entities.size(), 3 + Math.max(getGame().getPlayers().size() - 2, 0));
        for (int i = 0; i < amount; i++) {
            spawnMob(location, type);
        }
    }

    public void spawnMob(Location centerLocation, EntityType type) {
        World world = location.getWorld();
        Random random = new Random();

        double offsetX = -1 + (random.nextDouble() * 2);
        double offsetZ = -1 + (random.nextDouble() * 2);

        Location spawnLocation = centerLocation.clone().add(offsetX, 0, offsetZ);

        LivingEntity entity = (LivingEntity) world.spawnEntity(spawnLocation.clone().add(0, 1, 0), type);

        world.spawnParticle(Particle.FLAME, spawnLocation, 20, 0.2, 0.5, 0.2, 0.05);

        entity.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(8);
        entity.setRemoveWhenFarAway(false);

        if (entity instanceof Slime slime) {
            slime.setSize(2);
        }
        if (entity instanceof MagmaCube magma) {
            magma.setSize(2);
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
    }

    public void killEntity(LivingEntity entity) {
        entities.remove(entity);
        childEntities.remove(entity);
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
}
