package me.harpervenom.peakyBlocks.lastwars.Map;


import me.harpervenom.peakyBlocks.lastwars.Turret.Turret;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LocationSet {
    private final Location spawn;
    private final Location core;
    private final List<Turret> turrets;
    private final Location trader;

    public LocationSet(Location spawn, Location core, List<Turret> turrets, Location trader) {
        this.spawn = spawn.add(0.5, 1, 0.5);
        this.core = core;
        this.turrets = turrets;
        this.trader = trader.add(0.5, 1, 0.5);
    }

    public void setWorld(World world) {
        spawn.setWorld(world);
        core.setWorld(world);
        trader.setWorld(world);

        for (Turret turret : turrets) {
            turret.getLoc().setWorld(world);
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

    public Location getTrader() {
        return trader;
    }
}
