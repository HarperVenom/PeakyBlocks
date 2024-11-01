package me.harpervenom.peakyBlocks.classes.game;

import me.harpervenom.peakyBlocks.classes.queue.Queue;
import me.harpervenom.peakyBlocks.classes.queue.QueueTeam;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static me.harpervenom.peakyBlocks.utils.MapManager.removeWorld;
import static me.harpervenom.peakyBlocks.utils.Utils.getYawFromBlockFace;

public class Game {

    //red spawn 26, 0, 0
    //red core 20, -1, 0, 90
    //turret 14, -1, 0
    //facing west

    //blue spawn -26, 0, 0
    //blue core -20, -1, 0, -90
    //turret -14, -1, 0
    //facing east

    public static List<Game> activeGames = new ArrayList<>();

    private final int id;
    private World world;
    public final List<GameTeam> teams = new ArrayList<>();
    public final List<GameTeam> deadTeams = new ArrayList<>();
    private Queue queue;

    public Game(Queue queue, World world) {
        this.queue = queue;
        this.id = queue.getId();

        for (QueueTeam queueTeam : queue.getTeams()) {
            GameTeam gameTeam = new GameTeam(queueTeam);
            gameTeam.setGame(this);
            teams.add(gameTeam);
        }

        setWorld(world);
    }

    public World getWorld() {
        return world;
    }
    public void setWorld(World world) {
        this.world = world;

        for (GameTeam team : teams) {
            if (team.getColor() == ChatColor.RED) {
                team.setSpawn(new Location(world, 26 + 0.5, 0, 0 + 0.5, getYawFromBlockFace(BlockFace.WEST), 0));
                team.setFacing(BlockFace.WEST);
                team.setCore(new Location(world, 20, -1, 0));
                team.setTurret(new Location(world, 14, -1, 0));
            }
            if (team.getColor() == ChatColor.BLUE) {
                team.setSpawn(new Location(world, -26 + 0.5, 0, 0 + 0.5, getYawFromBlockFace(BlockFace.EAST), 0));
                team.setFacing(BlockFace.EAST);
                team.setCore(new Location(world, -20, -1, 0));
                team.setTurret(new Location(world, -14, -1, 0));
            }
        }

        start();
    }

    public List<GameTeam> getTeams() {
        return teams;
    }

    public List<GamePlayer> getPlayers() {
        return teams.stream()
                .flatMap(team -> team.getPlayers().stream()).collect(Collectors.toList());
    }

    public void start() {
        for (GamePlayer gp : getPlayers()) {
            Player p = gp.getPlayer();

            p.getInventory().clear();
            p.getInventory().setHeldItemSlot(0);
            p.teleport(gp.getTeam().getSpawn());
//            p.setBedSpawnLocation(gp.getTeam().getSpawn(), true);
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

        removeWorld(world);
        activeGames.remove(this);
    }
}
