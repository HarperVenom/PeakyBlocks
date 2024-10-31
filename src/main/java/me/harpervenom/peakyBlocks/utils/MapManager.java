package me.harpervenom.peakyBlocks.utils;

import me.harpervenom.peakyBlocks.PeakyBlocks;
import me.harpervenom.peakyBlocks.classes.game.Game;
import me.harpervenom.peakyBlocks.classes.queue.Queue;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

import static me.harpervenom.peakyBlocks.PeakyBlocks.getPlugin;
import static me.harpervenom.peakyBlocks.classes.game.Game.activeGames;

public class MapManager {

    public static void createWorld(Queue queue) {
        String worldName = "game_" + queue.getId();

        File backup = new File(getPlugin().getDataFolder(), "backup_maps" + File.separator + "game_map");  // Backup world directory
        File newWorldFolder = new File(Bukkit.getWorldContainer(), worldName);  // Target world folder in server root

        if (!backup.exists() || !backup.isDirectory()) {
            System.out.println("Backup world not found!");
        }

        try {
            if (newWorldFolder.exists()) {
                Bukkit.unloadWorld(worldName, false);
                deleteWorld(newWorldFolder);
            }
        } catch (IOException e) {
            System.out.println("Failed to copy world data: " + e.getMessage());
            e.printStackTrace();
        }

        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            try {
                // Copy from the backup to the world folder
                copyWorld(backup, newWorldFolder);
                System.out.println("[PeakyBlocks] World data copied from backup!");

                // After copying, switch back to the main thread to load the world
                Bukkit.getScheduler().runTask(getPlugin(), () -> {
                    World newWorld = Bukkit.createWorld(new WorldCreator(worldName));
                    activeGames.add(new Game(queue, newWorld));
                });

            } catch (IOException e) {
                System.out.println("Failed to copy world data: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public static void removeWorld(World world) {
        if (world == null) {
            System.out.println("World not found!");
            return;
        }

        String worldName = world.getName();

        // 1. Unload the world
        boolean success = Bukkit.unloadWorld(world, false);  // 'false' means we don't save chunks before unloading
        if (!success) {
            System.out.println("Failed to unload world " + worldName);
        }

        // 2. Delete the world folder
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            File worldFolder = new File(Bukkit.getWorldContainer(), worldName);
            if (worldFolder.exists()) {
                try {
                    FileUtils.deleteDirectory(worldFolder);  // Deletes the entire world folder
                    System.out.println("World " + worldName + " has been deleted.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("World folder for " + worldName + " not found.");
            }
        });
    }

    public static void copyWorld(File source, File target) throws IOException {
        FileUtils.copyDirectory(source, target);
    }

    // Method to delete a world folder
    public static void deleteWorld(File path) throws IOException {
        FileUtils.deleteDirectory(path);
    }
}
