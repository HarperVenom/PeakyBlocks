package me.harpervenom.peakyBlocks.lastwars.Map;

import me.harpervenom.peakyBlocks.lastwars.Turret.Turret;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static me.harpervenom.peakyBlocks.PeakyBlocks.getPlugin;
import static me.harpervenom.peakyBlocks.lastwars.Map.MapManager.createWorld;
import static me.harpervenom.peakyBlocks.lastwars.Map.MapManager.removeWorld;
import static me.harpervenom.peakyBlocks.utils.Utils.getYaw;

public class Map {

    public static List<Map> sampleMaps = new ArrayList<>();
    private final static File mapsFolder = new File(getPlugin().getDataFolder(), "maps");

    private String name;
    private String displayName;
    private World world;
    private List<LocationSet> locSets = new ArrayList<>();
    private HashMap<Chunk, List<Location>> blocks = new HashMap<>();

    private Location corner1;
    private Location corner2;

    static {
        loadMaps();
    }
//
//    public Map(Map sample) {
//        name = sample.getName();
//        displayName = sample.getDisplayName();
//        locSets = sampleMaps.stream()
//                .filter(map -> map.getName().equals(name))
//                .findFirst()
//                .map(Map::getLocSets)
//                .orElse(null);
//
//        //world is null here
//        world = sample.getWorld();
//    }

    public Map(String name) {
        this.name = name;

        FileConfiguration config;

        File mapsFolder = new File(getPlugin().getDataFolder(),"maps");
        File mapFolder = new File(mapsFolder, name); // Locate the specific map folder by name
        File configFile = new File(mapFolder, "config.yml"); // Adjust the YAML file name if it's different

        if (!configFile.exists()) {
            System.out.println("Map config file not found for map: " + name);
            return;
        }

        config = YamlConfiguration.loadConfiguration(configFile);

        displayName = config.getString("name");

        ConfigurationSection teamsSection = config.getConfigurationSection("teams");
        if (teamsSection == null) {
            System.out.println("No teams section found in the config for map: " + name);
            return;
        }

        for (String teamKey : teamsSection.getKeys(false)) {
            ConfigurationSection teamSection = teamsSection.getConfigurationSection(teamKey);
            if (teamSection != null) {
                // Extract each location for the team
                Location spawn = getLocationFromConfig(teamSection, "spawn");
                Location core = getLocationFromConfig(teamSection, "core");

                ConfigurationSection turretsSection = teamSection.getConfigurationSection("turrets");
                List<Turret> turrets = new ArrayList<>();
                if (turretsSection == null) {
                    System.out.println("No turrets section found in the config for map: " + name);
                } else {
                    for (String turretKey : turretsSection.getKeys(false)) {
                        ConfigurationSection turretSection = turretsSection.getConfigurationSection(turretKey);
                        if (turretSection != null) {
                            Turret turret = new Turret(getLocationFromConfig(turretsSection, turretKey), turretSection.getBoolean("breakable"));
                            turrets.add(turret);
                        }
                    }
                }

                Location trader = getLocationFromConfig(teamSection, "trader");

                // Process locations or store them as needed
                System.out.println("Loaded locations for team: " + teamKey);

                locSets.add(new LocationSet(spawn, core, turrets, trader));
            }
        }

        ConfigurationSection borderSection = config.getConfigurationSection("borders");

        createWorld(name, null, () -> {
            this.world = Bukkit.createWorld(new WorldCreator(name));

            if (borderSection != null) {
                corner1 = new Location(world, borderSection.getDouble("x1"),
                        borderSection.getDouble("y1"),
                        borderSection.getDouble("z1"));

                corner2 = new Location(world, borderSection.getDouble("x2"),
                        borderSection.getDouble("y2"),
                        borderSection.getDouble("z2"));
            }

            scanArea();

            removeWorld(world);
            world = null;
        });
    }

    private Location getLocationFromConfig(ConfigurationSection section, String path) {
        if (section == null) return null;

        double x = section.getDouble(path + ".x");
        double y = section.getDouble(path + ".y");
        double z = section.getDouble(path + ".z");
        String facing = section.getString(path + ".facing");

        return facing == null ? new Location(null, x, y, z) : new Location(null, x, y, z, getYaw(facing), 0); // Adjust with world if necessary
    }

    public String getName() {
        return name;
    }
    public String getDisplayName() {
        return displayName;
    }
    public List<LocationSet> getLocSets() {
        return locSets;
    }
    public void setWorld(World world) {
        this.world = world;

        for (LocationSet locationSet : locSets) {
            locationSet.setWorld(world);
        }
    }
    public World getWorld() {
        return world;
    }

    public static void loadMaps() {
        if (!mapsFolder.exists() || !mapsFolder.isDirectory()) {
            System.out.println("Maps folder not found or is not a directory. No maps to load.");
            return;
        }

        File[] files = mapsFolder.listFiles();
        if (files == null) {
            System.out.println("Failed to retrieve files in the maps folder.");
            return;
        }

        // Iterate over each directory in the maps folder
        for (File file : files) {
            if (file.isDirectory()) {
                String mapName = file.getName();

                sampleMaps.add(new Map(mapName));
                System.out.println("Loaded map: " + mapName);
            }
        }
    }

    public void scanArea() {
        HashMap<Chunk, List<Location>> chunkBlocksMap = new HashMap<>();

        // Ensure we have the minimum and maximum coordinates for each axis
        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int minY = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int maxY = Math.max(corner1.getBlockY(), corner2.getBlockY());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        World world = corner1.getWorld(); // Assume both corners are in the same world

        // Loop through all blocks within the specified area
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location loc = new Location(world, x, y, z);
                    Block block = loc.getBlock();

                    if (block.getType() != Material.AIR && block.getType() != Material.WATER
                            && block.getType() != Material.LAVA) {
                        Chunk chunk = block.getChunk();

                        // Initialize the list if the chunk isn't in the map
                        chunkBlocksMap.computeIfAbsent(chunk, k -> new ArrayList<>());

                        // Add the location to the list of this chunk
                        chunkBlocksMap.get(chunk).add(loc);
                    }
                }
            }
        }
        blocks = chunkBlocksMap;
    }

    public boolean containsBlocks(Block block) {
        int chunkX = block.getChunk().getX();
        int chunkZ = block.getChunk().getZ();
        Location blockLocation = block.getLocation();

        // Iterate through the map entries to check each chunk's coordinates
        for (HashMap.Entry<Chunk, List<Location>> entry : blocks.entrySet()) {
            Chunk storedChunk = entry.getKey();

            // Check if the chunk's x and z coordinates match the block's chunk
            if (storedChunk.getX() == chunkX && storedChunk.getZ() == chunkZ) {
                List<Location> locations = entry.getValue();

                // Check if the block's X, Y, Z match any location in the list, ignoring world
                for (Location location : locations) {
                    if (blockLocation.getBlockX() == location.getBlockX() &&
                            blockLocation.getBlockY() == location.getBlockY() &&
                            blockLocation.getBlockZ() == location.getBlockZ()) {
                        return true;
                    }
                }
            }
        }

        return false; // No matching location found in any chunk
    }

}
