package me.harpervenom.peakyBlocks.queue;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static me.harpervenom.peakyBlocks.PeakyBlocks.getPlugin;

public class QueueScoreBoard {

    private final Scoreboard scoreboard;
    private final Objective objective;
    private final Queue queue;
    private int countdownId = -1;
    final int[] timeLeft = {10};

    public QueueScoreBoard(Queue queue) {
        this.queue = queue;
        // Create a new scoreboard
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getNewScoreboard();

        // Create a new objective (the display name for the scoreboard)
        objective = scoreboard.registerNewObjective("gameInfo", "dummy", "Игра " + queue.getId());
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void clear() {
        Set<String> entries = scoreboard.getEntries();
        for (String entry : entries) {
            scoreboard.resetScores(entry);
        }
    }

    public void displayMessage(String message, int position) {
        Score score = objective.getScore(message);
        score.setScore(position);
    }

    public void update() {
        clear();

        int redTeamSize = queue.getTeams().getFirst().getPlayers().size();
        int blueTeamSize = queue.getTeams().get(1).getPlayers().size();
        int maxTeamSize = queue.getTeamSize();

        int totalPlayers = queue.getPlayersCount();
        int totalMax = queue.getTotalMaxPlayers();

        List<String> lines = new ArrayList<>();
        lines.add("");
        lines.add(ChatColor.WHITE + "Карта:");
        lines.add(ChatColor.GRAY + queue.getMap().getDisplayName());
        lines.add(" ");
        lines.add(ChatColor.RED + "Красные: " + redTeamSize + "/" + maxTeamSize);
        lines.add(ChatColor.BLUE + "Синие: " + blueTeamSize + "/" + maxTeamSize);
        lines.add("  ");
        lines.add(ChatColor.WHITE + "Всего: " + ChatColor.GRAY + totalPlayers + "/" + totalMax);
        lines.add("   ");
        if (totalPlayers < totalMax - 1) {
            lines.add(ChatColor.WHITE + "Мин. игроков: " + (totalMax - 1));
        } else {
            if (timeLeft[0] <= 0) {
                lines.add(ChatColor.WHITE + "Запуск игры...");
            } else {
                lines.add(ChatColor.WHITE + "До начала:");
                lines.add(ChatColor.WHITE + "" + timeLeft[0] + " сек.");
            }
        }
        lines.add("    ");
        lines.add(ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "---PeakyBlocks---");

        for (int i = lines.size() - 1; i >= 0; i--) {
            displayMessage(lines.reversed().get(i), i);
        }

        assignToPlayers(queue.getPlayers().stream().map(QueuePlayer::getPlayer).toList());
    }

    public void removeForPlayer(Player p) {
        p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    // Assign the scoreboard to all players in the game
    public void assignToPlayers(List<Player> players) {
        for (Player player : players) {
            assignToPlayer(player);
        }
    }

    public void assignToPlayer(Player p) {
        p.setScoreboard(scoreboard);
    }

    public void startCountdown() {
        // Schedule repeating task
        if (countdownId != -1 || Bukkit.getScheduler().isCurrentlyRunning(countdownId)) return;
        countdownId = Bukkit.getScheduler().scheduleSyncRepeatingTask(getPlugin(), new Runnable() {
            @Override
            public void run() {
                timeLeft[0]--;
                if (timeLeft[0] <= 0) {
                    Bukkit.getScheduler().cancelTask(countdownId);
                    countdownId = -1;
                    queue.startGame();
                }
                update();
            }
        }, 20L, 20L); // Run task immediately (0L delay), then every 20 ticks (1 second)
    }

    public void resetCountdown() {
        Bukkit.getScheduler().cancelTask(countdownId);
    }

    public void remove() {
        resetCountdown();
        scoreboard.clearSlot(DisplaySlot.SIDEBAR);
    }
}
