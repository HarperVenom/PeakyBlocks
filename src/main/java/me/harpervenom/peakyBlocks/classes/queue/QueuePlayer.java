package me.harpervenom.peakyBlocks.classes.queue;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class QueuePlayer {

    public static List<QueuePlayer> queuePlayers = new ArrayList<>();

    public static QueuePlayer getQueuePlayer(Player p) {
        for (QueuePlayer gamePlayer : queuePlayers) {
            if (gamePlayer.getId() == p.getUniqueId()) {
                return gamePlayer;
            }
        }
        return null;
    }

    private final UUID id;
    private QueueTeam team;

    public QueuePlayer(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
    public Player getPlayer(){
        return Bukkit.getPlayer(id);
    }

    public void setTeam(QueueTeam team) {
        this.team = team;
    }

    public QueueTeam getTeam() {
        return team;
    }

    public void remove() {
        queuePlayers.remove(this);
    }
}
