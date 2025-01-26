package me.harpervenom.peakyBlocks.lastwars;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.util.List;
import java.util.Set;

import static me.harpervenom.peakyBlocks.PeakyBlocks.getPlugin;

public class GameScoreboard {

    private final Game game;

    private final Scoreboard scoreboard;
    private final Objective gameInfoObjective;

    private BukkitRunnable timer;

    private int totalSeconds;
    private String lastTimeEntry; // Keep track of the last displayed time

    public GameScoreboard(Game game) {
        this.game = game;

        // Initialize the scoreboard and objectives
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getNewScoreboard();

        gameInfoObjective = scoreboard.registerNewObjective("gameInfo", "dummy", ChatColor.GRAY + "Игра #" + game.getId());
        gameInfoObjective.setDisplaySlot(DisplaySlot.SIDEBAR);

        // Initialize last time entry
        lastTimeEntry = "";
    }

    public void startTimer() {
        // Prevent creating a new timer if one is already running
        if (timer != null && !timer.isCancelled()) return;

        timer = new BukkitRunnable() {
            @Override
            public void run() {
                totalSeconds++;
                update();
            }
        };

        timer.runTaskTimer(getPlugin(), 20, 20); // Run every 20 ticks (1 second)

        // Assign the scoreboard to all players only once
        for (GamePlayer gp : game.getPlayers()) {
            Player p = gp.getPlayer();
            p.setScoreboard(scoreboard);
        }
    }

    private void update() {
        String currentTime = ChatColor.GRAY + getTimeString();

        if (currentTime.equals(lastTimeEntry)) return;

        if (!lastTimeEntry.isEmpty()) {
            scoreboard.resetScores(lastTimeEntry);
        }

        gameInfoObjective.getScore(currentTime).setScore(0);
        lastTimeEntry = currentTime;
    }

    public String getTimeString() {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public int getTotalSeconds() {
        return totalSeconds;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public void close() {
        // Stop the timer and clear the sidebar
        if (timer != null) timer.cancel();
        scoreboard.clearSlot(DisplaySlot.SIDEBAR);
    }
}

