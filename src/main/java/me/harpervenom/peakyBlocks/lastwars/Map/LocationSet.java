package me.harpervenom.peakyBlocks.lastwars.Map;


import me.harpervenom.peakyBlocks.lastwars.Trader.Trader;
import me.harpervenom.peakyBlocks.lastwars.Turret.Turret;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LocationSet {
    private final Location spawn;
    private final Location core;
    private final List<Turret> turrets;
    private final List<Trader> traders;

    public LocationSet(Location spawn, Location core, List<Turret> turrets, List<Trader> traders) {
        this.spawn = spawn.add(0.5, 1, 0.5);
        this.core = core;
        this.turrets = turrets;
        this.traders = traders.stream()
                .peek(trader -> trader.loc.add(0.5, 1, 0.5))
                .collect(Collectors.toList());
    }

    public void setWorld(World world) {
        spawn.setWorld(world);
        core.setWorld(world);
        traders.forEach(trader -> trader.loc.setWorld(world));

        for (Turret turret : turrets) {
            turret.getBaseLoc().setWorld(world);
        }
    }

    public Location getSpawn() {
        return spawn;
    }

    public Location getCore() {
        return core;
    }

    public List<Turret> getTurrets() {
        List<Turret> turretCopies = new ArrayList<>();
        for (Turret turret : turrets) {
            turretCopies.add(new Turret(turret));  // Assumes Turret has a copy constructor
        }
        return turretCopies;
    }

//    public List<Spawner> getSpawners() {
//        List<Spawner> spawnerCopies = new ArrayList<>();
//        for (Spawner spawner : spawners) {
//            spawnerCopies.add(new Spawner(spawner));  // Assumes Turret has a copy constructor
//        }
//        return spawnerCopies;
//    }

    public List<Trader> getTraders() {
        return traders;
    }
}
