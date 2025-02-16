package me.harpervenom.peakyBlocks.lastwars.Spawner;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import static me.harpervenom.peakyBlocks.PeakyBlocks.getPlugin;

public class ItemSpawner {

    private final Location location;
    private final Material material;
    private final ItemStack item;

    private BukkitRunnable timer;

    public ItemSpawner(Location location, Material material) {
        this.location = location;
        this.material = material;
        item = new ItemStack(material);
    }

    public ItemSpawner(ItemSpawner sample) {
        this.location = sample.location.clone();
        this.material = sample.material;
        item = new ItemStack(material);
        timer = null;
    }

    public void start() {
        timer = new BukkitRunnable() {
            @Override
            public void run() {
                ItemSpawner.this.run();
            }
        };

        int period = 3;
        if (material == Material.NETHER_BRICK) period = 15;
        if (material == Material.RESIN_BRICK) period = 90;

        timer.runTaskTimer(getPlugin(), period * 20, period * 20);
    }

    public void run() {
        if (location.getWorld() == null) return;

        Item droppedItem = location.getWorld().dropItem(location.clone().add(0.5, 1.5, 0.5), item);
        droppedItem.setVelocity(new Vector(0, 0, 0));
        droppedItem.setPickupDelay(0);
    }

    public void setWorld(World world) {
        location.setWorld(world);
    }

    public void stop() {
        if (!timer.isCancelled()) {
            timer.cancel();
        }
    }
}
