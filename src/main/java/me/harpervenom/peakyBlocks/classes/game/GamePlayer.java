package me.harpervenom.peakyBlocks.classes.game;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.*;

public class GamePlayer {

    public static Set<GamePlayer> gamePlayers = new HashSet<>();

    public static GamePlayer getGamePlayer(Player p) {
        for (GamePlayer gamePlayer : gamePlayers) {
            if (gamePlayer.getId() == p.getUniqueId()) { return gamePlayer;
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
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) return;

        Scoreboard emptyScoreboard = manager.getNewScoreboard();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setScoreboard(emptyScoreboard);
        }

        getTeam().removePlayer(this);
        gamePlayers.remove(this);
    }
}
