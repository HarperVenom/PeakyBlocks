package me.harpervenom.peakyBlocks.classes;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static me.harpervenom.peakyBlocks.lobby.MenuListener.activeGames;

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

    public int getMaxMembers() {
        return maxMembers;
    }
    public List<UUID> getMembers() {
        return members;
    }

    public void addMember(Player p) {
        members.add(p.getUniqueId());
    }

    public void removeMember(Player p) {
        members.remove(p.getUniqueId());

        Game game = activeGames.stream().filter(currentGame -> currentGame.getId() == gameId).findFirst().orElse(null);
        if (game == null) return;
        if (game.getNumberOfPlayers() == 0) {
            activeGames.remove(game);
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
