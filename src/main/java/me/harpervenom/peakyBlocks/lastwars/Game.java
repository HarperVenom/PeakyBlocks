package me.harpervenom.peakyBlocks.lastwars;

import me.harpervenom.peakyBlocks.lastwars.Map.LocationSet;
import me.harpervenom.peakyBlocks.lastwars.Map.Map;
import me.harpervenom.peakyBlocks.lastwars.Spawner.Spawner;
import me.harpervenom.peakyBlocks.lastwars.Trader.Trader;
import me.harpervenom.peakyBlocks.queue.Queue;
import me.harpervenom.peakyBlocks.queue.QueueTeam;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static me.harpervenom.peakyBlocks.PeakyBlocks.getPlugin;

public class Game {

    public static List<Game> activeGames = new ArrayList<>();

    public static Game getGameByWorld(World world) {
        for (Game game : activeGames) {
            if (game.getWorld().getName().equals(world.getName())) return game;
        }
        return null;
    }

    private final int id;
    private Map map;
    public final List<GameTeam> teams = new ArrayList<>();
    public final List<GameTeam> deadTeams = new ArrayList<>();
    public List<Spawner> spawners = new ArrayList<>();

    private Queue queue;
    private long time;
    private Scoreboard scoreboard;
    private Objective bountyObjective;

    private BukkitRunnable timer;

    public Game(Queue queue) {
        this.queue = queue;
        this.id = queue.getId();

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getNewScoreboard();
        bountyObjective = scoreboard.registerNewObjective("bountyScores", "dummy", "Player Scores");
        bountyObjective.setDisplaySlot(DisplaySlot.PLAYER_LIST);

        for (QueueTeam queueTeam : queue.getTeams()) {
            GameTeam gameTeam = new GameTeam(this, queueTeam);
            teams.add(gameTeam);
        }

        setMap(queue.getMap());
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
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
            Trader trader = new Trader(locationSet.getTrader());
            team.getTeam().addEntry(trader.getEntity().getUniqueId().toString());
        }

        setSpawners(map.getSpawners());

        map.getWorld().setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);

        start();
    }

    public void setSpawners(List<Spawner> spawners) {
        this.spawners = spawners;
    }
    public List<Spawner> getSpawners() {
        return spawners;
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
            p.setExp(0);
            p.setLevel(0);
            p.sendMessage("Игра началась!");
        }
        queue.delete(true);
        queue = null;

        runTimer();

        updateBountyBoard();
    }

    private void runTimer() {
        timer = new BukkitRunnable() {
            @Override
            public void run() {
                if (time != 0 && time % 60 == 0) {
                    sendMessage(ChatColor.GRAY + "Минута: " + (time / 60));
                }

                if (time != 0 && time % 90 == 0) {
                    for (Spawner spawner : spawners) {
                        if (spawner.getType() == EntityType.MAGMA_CUBE) {
                            spawner.run();
                        }
                    }
                }

                if (time == 0 || time % 60 == 0) {
                    for (Spawner spawner : spawners) {
                        if (spawner.getType() == EntityType.SLIME) {
                            spawner.run();
                        }
                    }
                }

                time++;
            }
        };

        timer.runTaskTimer(getPlugin(), 0 ,20);
    }

    public long getTime() {
        return time;
    }

    public void checkTeams() {
        if (getPlayers().isEmpty()) {
            finish();
        }
    }

    public boolean isBlockProtected(Block b) {
        Location blockLoc = b.getLocation();

        for (GameTeam team : teams) {
            Location spawn = team.getSpawn().clone().subtract(0.5, 0, 0.5);

            if (Math.abs(spawn.getX() - blockLoc.getX()) <= 3 &&
                    Math.abs(spawn.getZ() - blockLoc.getZ()) <= 3) {
                return true;
            }
        }
        return false;
    }

    public void updateBountyBoard() {
        for (GamePlayer gp : getPlayers()) {
            Player p = gp.getPlayer();

            Score score = bountyObjective.getScore(p.getName());
            score.setScore(gp.getBounty());
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
