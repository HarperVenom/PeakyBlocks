package me.harpervenom.peakyBlocks.classes.game;

import me.harpervenom.peakyBlocks.classes.queue.Queue;
import me.harpervenom.peakyBlocks.classes.queue.QueueTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static me.harpervenom.peakyBlocks.classes.game.GamePlayer.gamePlayers;
import static me.harpervenom.peakyBlocks.utils.MapManager.createWorld;
import static me.harpervenom.peakyBlocks.utils.MapManager.removeWorld;

public class Game {

    public static List<Game> activeGames = new ArrayList<>();

    private final int id;
    private final List<GameTeam> teams = new ArrayList<>();
    private World world;
    private Queue queue;

    public Game(Queue queue) {
        this.queue = queue;
        this.id = queue.getId();

        for (QueueTeam queueTeam : queue.getTeams()) {
            GameTeam gameTeam = new GameTeam(queueTeam);
            gameTeam.setGame(this);
            teams.add(gameTeam);
        }

        createWorld("game_" + id, this);
    }

    public void setWorld(World world) {
        this.world = world;

        for (GameTeam team : teams) {
            if (team.getColor() == ChatColor.RED) {
                team.setSpawn(new Location(world, 26, 0, 0));
            }
            if (team.getColor() == ChatColor.BLUE) {
                team.setSpawn(new Location(world, -26, 0, 0));
            }
        }

        start();
    }

    public List<GamePlayer> getPlayers() {
        return teams.stream()
                .flatMap(team -> team.getPlayers().stream()).collect(Collectors.toList());
    }

    public void start() {
        for (GamePlayer p : getPlayers()) {
            p.getPlayer().teleport(p.getTeam().getSpawn());
            p.getPlayer().sendMessage("Игра началась!");
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
        for (GamePlayer gamePlayer : getPlayers()) {
            gamePlayer.remove();

            Player p = gamePlayer.getPlayer();
            if (!p.getWorld().equals(Bukkit.getWorld("lobby"))) {
                p.performCommand("lobby");
            }
        }
        removeWorld(world);
        activeGames.remove(this);
    }
}
