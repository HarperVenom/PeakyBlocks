package me.harpervenom.peakyBlocks.classes.queue.events;

import me.harpervenom.peakyBlocks.classes.queue.QueuePlayer;
import me.harpervenom.peakyBlocks.classes.queue.QueueTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerAddedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final QueuePlayer player;
    private final QueueTeam team;

    public PlayerAddedEvent(QueuePlayer player, QueueTeam team) {
        this.player = player;
        this.team = team;
    }

    public QueuePlayer getPlayer() {
        return player;
    }

    public QueueTeam getTeam() {
        return team;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

