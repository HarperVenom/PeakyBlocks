package me.harpervenom.peakyBlocks.lastwars.Spawner;

import me.harpervenom.peakyBlocks.lastwars.Game;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

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
    private int maxAmount = 3;
    private Game game;

    private Set<LivingEntity> entities = new HashSet<>();
    private Set<LivingEntity> childEntities = new HashSet<>();

    public Spawner(Location location, EntityType type) {
        this.location = location.add(0.5, 1, 0.5);
        this.type = type;

        spawnerSamples.add(this);

        Game game = getGameByWorld(location.getWorld());
        this.game = game;
        if (game == null) return;
        maxAmount += Math.max(game.getPlayers().size() - 2, 0);
    }

    public Spawner(Spawner sample) {
        this.location = sample.location;
        this.type = sample.type;
    }

    public void run() {
        entities.removeIf(Entity::isDead);

        if (entities.size() >= maxAmount) return;

        int amount = Math.min(maxAmount - entities.size(), 2 + Math.max(game.getPlayers().size() - 2, 0));
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

    public Location getLocation() {
        return location;
    }

    public EntityType getType() {
        return type;
    }

}
