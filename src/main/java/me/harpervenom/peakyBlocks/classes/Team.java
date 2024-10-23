package me.harpervenom.peakyBlocks.classes;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static me.harpervenom.peakyBlocks.lobby.MenuListener.activeQueues;

public class Team {

    private int gameId;
    private ChatColor color;
    private int maxMembers;
    private List<UUID> members = new ArrayList<>();

    public Team(int gameId, ChatColor color, int maxMembers) {
        this.gameId = gameId;
        this.color = color;
        this.maxMembers = maxMembers;
    }

    public int getGameId() {
        return gameId;
    }

    public ChatColor getColor() {
        return color;
    }

    public int getMaxPlayers() {
        return maxMembers;
    }
    public List<UUID> getPlayers() {
        return members;
    }

    public void addPlayer(Player p) {
        members.add(p.getUniqueId());
    }

    public void removePlayer(Player p) {
        members.remove(p.getUniqueId());

        Queue queue = activeQueues.stream().filter(currentQueue -> currentQueue.getId() == gameId).findFirst().orElse(null);
        if (queue == null) return;
        if (queue.getNumberOfPlayers() == 0) {
            activeQueues.remove(queue);
        }
    }

    public String getTeamName() {
        switch (color) {
            case ChatColor.RED -> {
                return "Красные";
            }
            case ChatColor.BLUE -> {
                return "Синие";
            }
        }
        return "";
    }

}
