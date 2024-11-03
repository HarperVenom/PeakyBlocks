package me.harpervenom.peakyBlocks.lastwars.Map;


import org.bukkit.Location;

public record LocationSet(Location spawn, Location core, Location turret, Location trader) {

    public LocationSet(Location spawn, Location core, Location turret, Location trader) {
        this.spawn = spawn.add(0.5, 1, 0.5);
        this.core = core;
        this.turret = turret;
        this.trader = trader.add(0.5, 1, 0.5);
    }

}
