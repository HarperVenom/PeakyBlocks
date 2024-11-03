package me.harpervenom.peakyBlocks.queue.events;

import me.harpervenom.peakyBlocks.queue.Queue;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MapCreatedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Queue queue;

    public MapCreatedEvent(Queue queue) {
        this.queue = queue;
    }

    public Queue getQueue() {
        return queue;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
