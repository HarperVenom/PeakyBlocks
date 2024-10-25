package me.harpervenom.peakyBlocks.classes.game;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GamePlayer {

    public static List<GamePlayer> gamePlayers = new ArrayList<>();

    public static GamePlayer getGamePlayer(Player p) {
        for (GamePlayer gamePlayer : gamePlayers) {
            if (gamePlayer.getId() == p.getUniqueId()) {
                return gamePlayer;
            }
        }
        return null;
    }

    private final UUID id;
    private GameTeam team;

    public GamePlayer(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
    public Player getPlayer(){
        return Bukkit.getPlayer(id);
    }

    public void setTeam(GameTeam team) {
        this.team = team;
    }

    public GameTeam getTeam() {
        return team;
    }

    public void remove() {
        gamePlayers.remove(this);
    }
}
