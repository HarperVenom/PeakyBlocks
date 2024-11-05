package me.harpervenom.peakyBlocks.lastwars;

import me.harpervenom.peakyBlocks.lastwars.Map.LocationSet;
import me.harpervenom.peakyBlocks.lastwars.Map.Map;
import me.harpervenom.peakyBlocks.lastwars.Trader.Trader;
import me.harpervenom.peakyBlocks.queue.Queue;
import me.harpervenom.peakyBlocks.queue.QueueTeam;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Game {

    public static List<Game> activeGames = new ArrayList<>();

    private final int id;
    private Map map;
    public final List<GameTeam> teams = new ArrayList<>();
    public final List<GameTeam> deadTeams = new ArrayList<>();
    private Queue queue;

    public Game(Queue queue) {
        this.queue = queue;
        this.id = queue.getId();

        for (QueueTeam queueTeam : queue.getTeams()) {
            GameTeam gameTeam = new GameTeam(queueTeam);
            gameTeam.setGame(this);
            teams.add(gameTeam);
        }

        setMap(queue.getMap());
    }


    public Map getMap() {
        return map;
    }
    public void setMap(Map map) {
        this.map = map;

        for (int i = 0; i < teams.size(); i++) {
            GameTeam team = teams.get(i);
            LocationSet locationSet = map.getLocSets().get(i);

            team.setSpawn(locationSet.getSpawn());
            team.setCore(locationSet.getCore());
            team.setTurrets(locationSet.getTurrets());
            new Trader(locationSet.getTrader());
        }

        map.getWorld().setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);

        start();
    }

    public World getWorld() {
        return map.getWorld();
    }

    public List<GameTeam> getTeams() {
        return teams;
    }

    public List<GamePlayer> getPlayers() {
        return teams.stream()
                .flatMap(team -> team.getPlayers().stream()).collect(Collectors.toList());
    }

    public void sendMessage(String message) {
        for (GamePlayer gp : getPlayers()) {
            Player p = gp.getPlayer();

            p.sendMessage(message);
        }
    }

    public void start() {
        for (GamePlayer gp : getPlayers()) {
            Player p = gp.getPlayer();

            p.getInventory().clear();
            p.getInventory().setHeldItemSlot(0);
            p.teleport(gp.getTeam().getSpawn());
            p.setRespawnLocation(gp.getTeam().getSpawn(), true);
            p.setGameMode(GameMode.SURVIVAL);
            p.setSaturation(5);
            p.sendMessage("Игра началась!");
        }
        queue.delete();
        queue = null;
    }

    public void checkTeams() {
        if (getPlayers().isEmpty()) {
            finish();
        }
    }

    public void finish() {

        for (GameTeam gTeam : teams) {
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            if (manager == null) return;

            Scoreboard scoreboard = manager.getMainScoreboard();
            Team team = scoreboard.getTeam(gTeam.getName());

            if (team != null) {
                team.unregister();  // This will remove the team and all its associated entries
            }
        }

        for (GamePlayer gamePlayer : getPlayers()) {
            gamePlayer.remove();

            Player p = gamePlayer.getPlayer();
            p.setExp(0);
            p.setLevel(0);
            p.getInventory().clear();
            if (!p.getWorld().equals(Bukkit.getWorld("lobby"))) {
                p.performCommand("lobby");
            }
        }

        activeGames.remove(this);
    }
}
