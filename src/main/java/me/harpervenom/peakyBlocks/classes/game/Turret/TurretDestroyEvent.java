package me.harpervenom.peakyBlocks.classes.game.Turret;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TurretDestroyEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Turret turret;

    public TurretDestroyEvent(Turret core) {
        this.turret = core;
    }

    public Turret getTurret() {
        return turret;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
