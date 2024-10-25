package me.harpervenom.peakyBlocks.classes.queue.events;

import me.harpervenom.peakyBlocks.classes.queue.QueuePlayer;
import me.harpervenom.peakyBlocks.classes.queue.QueueTeam;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerRemovedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final QueuePlayer player;
    private final QueueTeam team;
    private final boolean isChangingTeams;
    private final boolean isSilent;

    public PlayerRemovedEvent(QueuePlayer player, QueueTeam team, boolean isChangingTeams, boolean isSilent) {
        this.player = player;
        this.team = team;
        this.isChangingTeams = isChangingTeams;
        this.isSilent = isSilent;
    }

    public QueuePlayer getPlayer() {
        return player;
    }

    public QueueTeam getTeam() {
        return team;
    }

    public boolean isChangingTeams() {
        return isChangingTeams;
    }

    public boolean isSilent() {
        return isSilent;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
