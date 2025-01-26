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

    final Game game;

    private final Scoreboard scoreboard;
    private final Objective gameInfoObjective;
    private Objective bountyObjective;

    private BukkitRunnable timer;

    private int totalSeconds;

    public GameScoreboard(Game game) {
        this.game = game;

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getNewScoreboard();

        gameInfoObjective = scoreboard.registerNewObjective("gameInfo", "dummy", ChatColor.GRAY + "Игра #" + game.getId());
        gameInfoObjective.setDisplaySlot(DisplaySlot.SIDEBAR);

        bountyObjective = scoreboard.registerNewObjective("bountyScores", "dummy", "Player Scores");
        bountyObjective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
    }

    public void startTimer() {
        if (timer != null && timer.isCancelled()) return;
        update();
        timer = new BukkitRunnable() {
            @Override
            public void run() {
                totalSeconds++;
                update();
            }
        };
        timer.runTaskTimer(getPlugin(), 20, 20);

        for (GamePlayer gp : game.getPlayers()) {
            Player p = gp.getPlayer();
            p.setScoreboard(scoreboard);
        }
    }

    private void update() {
        clear();

        Score score = gameInfoObjective.getScore(ChatColor.GRAY + getTimeString());
        score.setScore(0);

        assignToPlayers(game.getPlayers().stream().map(GamePlayer::getPlayer).toList());
    }

    public void assignToPlayers(List<Player> players) {
        for (Player player : players) {
            assignToPlayer(player);
        }
    }

    public void assignToPlayer(Player p) {
        p.setScoreboard(scoreboard);
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

    public void clear() {
        Set<String> entries = scoreboard.getEntries();
        for (String entry : entries) {
            scoreboard.resetScores(entry);
        }
    }

    public void close() {
        if (timer != null) timer.cancel();
        scoreboard.clearSlot(DisplaySlot.SIDEBAR);
    }
}
