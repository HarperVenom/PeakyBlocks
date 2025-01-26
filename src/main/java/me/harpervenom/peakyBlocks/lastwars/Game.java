package me.harpervenom.peakyBlocks.lastwars;

import me.harpervenom.peakyBlocks.lastwars.Map.LocationSet;
import me.harpervenom.peakyBlocks.lastwars.Map.Map;
import me.harpervenom.peakyBlocks.lastwars.Spawner.ItemSpawner;
import me.harpervenom.peakyBlocks.lastwars.Spawner.Spawner;
import me.harpervenom.peakyBlocks.lastwars.Turret.Turret;
import me.harpervenom.peakyBlocks.queue.Queue;
import me.harpervenom.peakyBlocks.queue.QueueTeam;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;

import static me.harpervenom.peakyBlocks.PeakyBlocks.getPlugin;
import static me.harpervenom.peakyBlocks.lastwars.GameListener.noDamageExplosions;
import static me.harpervenom.peakyBlocks.lastwars.GameListener.turretExplosions;
import static me.harpervenom.peakyBlocks.lastwars.Turret.Turret.turrets;

public class Game {

    public static List<Game> activeGames = new ArrayList<>();

    public static Game getGameByWorld(World world) {
        for (Game game : activeGames) {
            if (game.getWorld().getName().equals(world.getName())) return game;
        }
        return null;
    }

    private Map map;
    public final List<GameTeam> teams = new ArrayList<>();
    public final List<GameTeam> deadTeams = new ArrayList<>();
    public List<Spawner> spawners = new ArrayList<>();
    public List<ItemSpawner> itemSpawners = new ArrayList<>();

    private Queue queue;
    private final int id;

    private final GameScoreboard scoreboard;

    public Game(Queue queue) {
        this.queue = queue;
        this.id = queue.getId();

        scoreboard = new GameScoreboard(this);

        for (QueueTeam queueTeam : queue.getTeams()) {
            GameTeam gameTeam = new GameTeam(this, queueTeam);
            teams.add(gameTeam);
        }

        setMap(queue.getMap());
    }

    public int getId() {
        return id;
    }

    public Scoreboard getScoreboard() {
        return scoreboard.getScoreboard();
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
            locationSet.getTraders().forEach(trader -> {
                trader.spawn();
                team.getTeam().addEntry(trader.getEntity().getUniqueId().toString());
            });
        }

        setSpawners(map.getSpawners());
        setItemSpawners(map.getItemSpawners());

        map.getWorld().setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        map.getWorld().setGameRule(GameRule.DO_WEATHER_CYCLE, true);
        map.getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        map.getWorld().setDifficulty(Difficulty.HARD);
        map.getWorld().setTime(1000);
        map.getWorld().setWeatherDuration(0);

        turretExplosions.put(map.getWorld(), new ArrayList<>());
        noDamageExplosions.put(map.getWorld(), new ArrayList<>());
    }

    public void setSpawners(List<Spawner> spawners) {
        this.spawners = spawners;
    }
    public List<Spawner> getSpawners() {
        return spawners;
    }

    public void setItemSpawners(List<ItemSpawner> spawners) {
        this.itemSpawners = spawners;
    }

    public World getWorld() {
        return map.getWorld();
    }

    public List<GameTeam> getTeams() {
        return teams;
    }

    public List<GamePlayer> getPlayers() {
        List<GamePlayer> gamePlayers = new ArrayList<>();

        for (GameTeam team : teams) {
            gamePlayers.addAll(team.getPlayers());
        }

        for (GameTeam team : deadTeams) {
            gamePlayers.addAll(team.getPlayers());
        }

        return gamePlayers;
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

        for (Spawner spawner : spawners) {
            spawner.start();
        }

        for (ItemSpawner spawner : itemSpawners) {
            spawner.start();
        }

        scoreboard.startTimer();
    }

    public long getTime() {
        return scoreboard.getTotalSeconds();
    }

    public void checkTeams() {
        if (getPlayers().isEmpty()) {
            finish();
        }
    }

    public boolean isBlockProtected(Block b) {
        Location blockLoc = b.getLocation();

        for (Turret turret : turrets) {
            if (turret.getLoc().equals(blockLoc)) return true;
        }

        for (GameTeam team : teams) {
            Location spawn = team.getSpawn().clone().subtract(0.5, 0, 0.5);

            if (Math.abs(spawn.getX() - blockLoc.getX()) <= 2 &&
                    Math.abs(spawn.getZ() - blockLoc.getZ()) <= 2) {
                return true;
            }
        }
        return false;
    }

//    private void updateGameInfo() {
//        for (GamePlayer gp : getPlayers()) {
//            Player p = gp.getPlayer();
//
//            Score score = bountyObjective.getScore(p.getName());
////            score.setScore(gp.getBounty());
//        }
//    }

//    public void updateBountyBoard() {
//        for (GamePlayer gp : getPlayers()) {
//            Player p = gp.getPlayer();
//
//            Score score = bountyObjective.getScore(p.getName());
////            score.setScore(gp.getBounty());
//        }
//    }

    public void finish() {
        scoreboard.close();

        for (GameTeam gTeam : teams) {
            for (Turret turret : gTeam.getTurrets()) {
                turret.destroy();
            }

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

        for (Spawner spawner : spawners) {
            spawner.stop();
        }
        for (ItemSpawner itemSpawner : itemSpawners) {
            itemSpawner.stop();
        }

        activeGames.remove(this);
    }
}
