package me.harpervenom.peakyBlocks.lastwars.Map;

import me.harpervenom.peakyBlocks.lastwars.Turret.Turret;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static me.harpervenom.peakyBlocks.PeakyBlocks.getPlugin;
import static me.harpervenom.peakyBlocks.utils.Utils.getYaw;

public class Map {

    public static List<Map> sampleMaps = new ArrayList<>();
    private final static File mapsFolder = new File(getPlugin().getDataFolder(), "maps");

    private String name;
    private String displayName;
    private World world;
    private List<LocationSet> locSets = new ArrayList<>();

    static {
        loadMaps();
    }

    public Map(Map sample) {
        name = sample.getName();
        displayName = sample.getDisplayName();
        locSets = sampleMaps.stream()
                .filter(map -> map.getName().equals(name))
                .findFirst()
                .map(Map::getLocSets)
                .orElse(null);

        //world is null here
        world = sample.getWorld();
    }

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
}
