package me.harpervenom.peakyBlocks.lastwars.Spawner;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

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

        timer.runTaskTimer(getPlugin(),0, 16 * 20);
    }

    public void run() {
        if (location.getWorld() == null) return;
        location.getWorld().dropItemNaturally(location.clone().add(0,1,0),item);
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
