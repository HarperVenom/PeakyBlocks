package me.harpervenom.peakyBlocks.classes;

import org.bukkit.ChatColor;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class Game {

    public static Integer lastGameId;

    int id;
    private World map;
    List<Team> teams = new ArrayList<>();
    private final int numberOfTeams;

    public Game(int numberOfTeams) {
        id = lastGameId == null ? 1 : lastGameId + 1;
        lastGameId = id;

        this.numberOfTeams = numberOfTeams;
    }

    public void setMaxPlayers(int maxPlayerPerTeam) {
        for (int i = 0; i < numberOfTeams; i++) {
            teams.add(new Team(id, getTeamColor(i), maxPlayerPerTeam));
        }
    }

    public int getId() {
        return id;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public int getNumberOfPlayers() {
        return teams.stream()    // Stream over the list of teams
                .mapToInt(team -> team.getMembers().size())  // Map each team to its player count
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
