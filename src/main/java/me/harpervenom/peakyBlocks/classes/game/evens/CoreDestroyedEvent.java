package me.harpervenom.peakyBlocks.classes.game.evens;

import me.harpervenom.peakyBlocks.classes.game.Core;
import me.harpervenom.peakyBlocks.classes.queue.QueuePlayer;
import me.harpervenom.peakyBlocks.classes.queue.QueueTeam;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CoreDestroyedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Core core;

    public CoreDestroyedEvent(Core core) {
        this.core = core;
    }

    public Core getCore() {
        return core;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
