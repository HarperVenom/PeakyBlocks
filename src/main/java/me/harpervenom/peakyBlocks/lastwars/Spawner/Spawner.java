package me.harpervenom.peakyBlocks.lastwars.Spawner;

import me.harpervenom.peakyBlocks.lastwars.Game;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Spawner {

    private static List<Spawner> spawners = new ArrayList<>();

    public static Spawner getEntitySpawner(LivingEntity entity) {
        for (Spawner spawner : spawners) {
            if (spawner.entities.contains(entity)) return spawner;
        }
        return null;
    }

    private Location location;
    private EntityType type;
    private int maxAmount = 2;

    private List<LivingEntity> entities = new ArrayList<>();

    public Spawner(Location location, EntityType type) {
        this.location = location.add(0.5, 1, 0.5);
        this.type = type;

        spawners.add(this);
    }

    public Spawner(Spawner sample) {
        this.location = sample.location;
        this.type = sample.type;
    }

    public void run() {
        entities.removeIf(Entity::isDead);

        if (entities.size() >= maxAmount) return;

        int amount = Math.min(maxAmount - entities.size(), 2);
        for (int i = 0; i < amount; i++) {
            spawnMob(location, type);
        }
    }

    public LivingEntity spawnMob(Location centerLocation, EntityType type) {
        World world = location.getWorld();
        Random random = new Random();

        double offsetX = -1 + (random.nextDouble() * 2);
        double offsetZ = -1 + (random.nextDouble() * 2);

        Location spawnLocation = centerLocation.clone().add(offsetX, 0, offsetZ);

        LivingEntity entity = (LivingEntity) world.spawnEntity(spawnLocation.clone().add(0, 1, 0), type);

        world.spawnParticle(Particle.FLAME, spawnLocation, 20, 0.2, 0.5, 0.2, 0.05);

        entity.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
        entity.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(8);

//        entity.setHealth(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
        if (entity instanceof Slime slime) {
            slime.setSize(2);
        }
        if (entity instanceof MagmaCube magma) {
            magma.setSize(2);
        }

        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(5);

        entities.add(entity);

        return entity;
    }

    public void killEntity(LivingEntity entity) {
        entities.remove(entity);
    }

    public Location getLocation() {
        return location;
    }

    public EntityType getType() {
        return type;
    }

}
