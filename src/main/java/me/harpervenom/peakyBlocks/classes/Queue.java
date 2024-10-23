package me.harpervenom.peakyBlocks.classes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static me.harpervenom.peakyBlocks.lobby.MenuListener.activeQueues;

public class Queue {

    public static Integer lastQueueId;

    private int id;
    private World map;
    List<Team> teams = new ArrayList<>();
    private final int numberOfTeams;
    private final QueueScoreBoard scoreboard;
    int playersCount = 0;

    public Queue(int numberOfTeams) {
        id = lastQueueId == null ? 1 : lastQueueId + 1;
        for (Queue activeQueue : activeQueues) {
            if (activeQueue.getId() == id) id++;
        }
        lastQueueId = id;
        this.numberOfTeams = numberOfTeams;
        scoreboard = new QueueScoreBoard(this);
    }

    public void setMaxPlayers(int maxPlayerPerTeam) {
        for (int i = 0; i < numberOfTeams; i++) {
            teams.add(new Team(id, getTeamColor(i), maxPlayerPerTeam));
        }
    }

    public int getId() {
        return id;
    }

    public int getNumberOfTeams() {
        return numberOfTeams;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public List<Player> getPlayers() {
        return teams.stream()
                .flatMap(team -> team.getPlayers().stream())  // Flatten the streams of each team's players
                .toList().stream().map(Bukkit::getPlayer).collect(Collectors.toList());  // Collect all players into a single list
    }
    public int getPlayersCount() {
        return playersCount;
    }

    public boolean addPlayerToTeam(Player p, Team team) {
        if (team.getMaxPlayers() > team.getPlayers().size()) {
            team.addPlayer(p);
            playersCount++;
            scoreboard.update();
            return true;
        }
        return false;
    }

    public void removePlayerFromTeam(Player p, Team team) {
        team.removePlayer(p);
        playersCount--;
        scoreboard.update();
    }

    public int getTeamSize() {
        return teams.getFirst().getMaxPlayers();
    }

    public int getTotalMaxPlayers() {
        return getTeamSize() * numberOfTeams;
    }

    public int getNumberOfPlayers() {
        return teams.stream()    // Stream over the list of teams
                .mapToInt(team -> team.getPlayers().size())  // Map each team to its player count
                .sum();
    }

    private ChatColor getTeamColor(int i) {
        switch (i) {
            case 0 -> {
                return ChatColor.RED;
            }
            case 1 -> {
                return ChatColor.BLUE;
            }
        }
        return null;
    }
}
