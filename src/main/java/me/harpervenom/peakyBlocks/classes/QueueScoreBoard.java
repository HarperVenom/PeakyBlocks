package me.harpervenom.peakyBlocks.classes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class QueueScoreBoard {

    private final Scoreboard scoreboard;
    private final Objective objective;
    private final Queue queue;

    public QueueScoreBoard(Queue queue) {
        this.queue = queue;
        // Create a new scoreboard
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getNewScoreboard();

        // Create a new objective (the display name for the scoreboard)
        objective = scoreboard.registerNewObjective("gameInfo", "dummy", ChatColor.GOLD + "Игра " + queue.getId());
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void clearScoreboard() {
        Set<String> entries = scoreboard.getEntries();
        for (String entry : entries) {
            scoreboard.resetScores(entry);
        }
    }

    public void displayMessage(String message, int position) {
        Score score = objective.getScore(message); // Create the score entry for the message
        score.setScore(position); // Set the score value (for ordering)
    }

    public void update() {
        clearScoreboard();

        int redTeamSize = queue.getTeams().getFirst().getPlayers().size();
        int blueTeamSize = queue.getTeams().get(1).getPlayers().size();
        int maxTeamSize = queue.getTeamSize();

        int totalPlayers = queue.getPlayersCount();
        int totalMax = queue.getTotalMaxPlayers();

        List<String> lines = new ArrayList<>();
        lines.add(" ");
        lines.add(ChatColor.RED + "Красные: " + redTeamSize + "/" + maxTeamSize);
        lines.add(ChatColor.BLUE + "Синие: " + blueTeamSize + "/" + maxTeamSize);
        lines.add("");
        lines.add(ChatColor.WHITE + "Всего: " + totalPlayers + "/" + totalMax);
        lines.add("  ");
        if (totalPlayers < totalMax - 1) {
            lines.add(ChatColor.YELLOW + "Минимум для старта: " + (totalMax - 1));
        } else {
            lines.add(ChatColor.YELLOW + "До начала: 30 сек.");
        }

        for (int i = lines.size() - 1; i >= 0; i--) {
            displayMessage(lines.reversed().get(i), i);
        }

        assignToPlayers(queue.getPlayers());
    }

    // Assign the scoreboard to a player
    public void assignToPlayer(Player player) {
        player.setScoreboard(scoreboard);
    }

    // Assign the scoreboard to all players in the game
    public void assignToPlayers(List<Player> players) {
        for (Player player : players) {
            assignToPlayer(player);
        }
    }

}
