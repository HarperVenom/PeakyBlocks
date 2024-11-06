package me.harpervenom.peakyBlocks.lastwars;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.*;

import static me.harpervenom.peakyBlocks.PeakyBlocks.getPlugin;

public class GamePlayer {

    public static Set<GamePlayer> gamePlayers = new HashSet<>();

    public static GamePlayer getGamePlayer(Player p) {
        for (GamePlayer gamePlayer : gamePlayers) {
            if (gamePlayer.getId() == p.getUniqueId()) { return gamePlayer;
            }
        }
        return null;
    }

    private final UUID id;
    private GameTeam team;
    private int deaths;
    private int expAfterDeath;

    private boolean frozen;
    private BukkitRunnable freezeTask;

    public GamePlayer(UUID id) {
        this.id = id;
        deaths = 0;
        expAfterDeath = 0;
    }

    public UUID getId() {
        return id;
    }
    public Player getPlayer(){
        return Bukkit.getPlayer(id);
    }

    public void setTeam(GameTeam team) {
        this.team = team;
    }

    public GameTeam getTeam() {
        return team;
    }

    public void addDeath() {
        deaths++;
        expAfterDeath = 0;

        team.getGame().updateBountyBoard();
    }
    public int getDeaths() {
        return deaths;
    }

    public void changeBalance(int exp) {
        getPlayer().giveExpLevels(exp);
        if (exp > 0) {
            expAfterDeath += exp;
        }

        team.getGame().updateBountyBoard();
    }

    public int getBalance() {
        return getPlayer().getLevel();
    }

    public int getBounty() {
        return expAfterDeath / 2;
    }

    public void freeze(int seconds) {
        frozen = true;

        Player p = getPlayer();
        p.setGameMode(GameMode.SPECTATOR);

        final int[] secondsLeft = {seconds};

        freezeTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (secondsLeft[0] > 0) {
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("До возрождения: " + secondsLeft[0]));
                    secondsLeft[0]--;
                } else {
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
                    unfreeze();
                    freezeTask.cancel();
                }
            }
        };

        freezeTask.runTaskTimer(getPlugin(), 0, 20);
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void unfreeze() {
        Player p = getPlayer();
        p.setGameMode(GameMode.SURVIVAL);
        p.removePotionEffect(PotionEffectType.BLINDNESS);

        frozen = false;
    }

    public void remove() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) return;

        Scoreboard emptyScoreboard = manager.getNewScoreboard();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setScoreboard(emptyScoreboard);
        }

        getTeam().removePlayer(this);
        gamePlayers.remove(this);
    }
}
