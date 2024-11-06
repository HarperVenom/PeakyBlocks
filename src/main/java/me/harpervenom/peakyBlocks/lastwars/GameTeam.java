package me.harpervenom.peakyBlocks.lastwars;

import me.harpervenom.peakyBlocks.lastwars.Core.Core;
import me.harpervenom.peakyBlocks.lastwars.Turret.Turret;
import me.harpervenom.peakyBlocks.queue.QueuePlayer;
import me.harpervenom.peakyBlocks.queue.QueueTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static me.harpervenom.peakyBlocks.PeakyBlocks.getPlugin;
import static me.harpervenom.peakyBlocks.lastwars.GamePlayer.gamePlayers;

public class GameTeam {

    private final ChatColor color;
    private final List<GamePlayer> members = new ArrayList<>();

    private Game game;
    private Location spawn;
    private Core core;
    private List<Turret> turrets = new ArrayList<>();
    private final Team team;

    public GameTeam(Game game, QueueTeam queueTeam) {
        this.color = queueTeam.getColor();

        this.game = game;

        Scoreboard scoreboard = game.getScoreboard();
        team = game.getScoreboard().registerNewTeam(queueTeam.getTeamName());
        team.setColor(color);
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);

        for (QueuePlayer qp : queueTeam.getPlayers()) {
            GamePlayer gamePlayer = new GamePlayer(qp.getId());
            gamePlayer.setTeam(this);
            gamePlayers.add(gamePlayer);
            members.add(gamePlayer);

            team.addEntry(gamePlayer.getPlayer().getName());

//            Scoreboard emptyScoreboard = manager.getNewScoreboard();
            Player player = gamePlayer.getPlayer();
//            player.setScoreboard(emptyScoreboard);

            // Reassign the main scoreboard with a slight delay
            Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
                player.setScoreboard(scoreboard);
            }, 2L);
        }
    }

    public ChatColor getColor() {
        return color;
    }

    public List<GamePlayer> getPlayers() {
        return members;
    }

    public boolean isMember(GamePlayer gp) {
        return members.contains(gp);
    }

    public void removePlayer(GamePlayer p) {
        members.remove(p);
        game.checkTeams();
    }

    public String getName() {
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

    public Game getGame() {
        return game;
    }
    public void setGame(Game game) {
        this.game = game;
    }

    public Location getSpawn() {
        return spawn;
    }
    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    public Core getCore() {
        return core;
    }
    public void setCore(Location coreLoc) {
        this.core = new Core(coreLoc, this);
    }
    public void destroyCore() {
        core.destroy();
        core = null;
    }

    public List<Turret> getTurrets() {
        return turrets;
    }
    public List<Turret> getBreakableTurrets() {
        return turrets.stream().filter(Turret::isBreakable).collect(Collectors.toList());
    }
    public void setTurrets(List<Turret> turrets) {
        for (Turret turret : turrets) {
            turret.setTeam(this);
            turret.buildStructure();
        }
        this.turrets = turrets;
    }
    public void destroyTurret(Turret turret) {
        turret.destroy();
        turrets.remove(turret);
    }

    public void win() {
        for (GamePlayer gp : getPlayers()) {
            Player p = gp.getPlayer();
            p.sendTitle("Победа!", "", 10, 100, 2);
        }
    }

    public void loose() {
        for (GamePlayer gp : getPlayers()) {
            Player p = gp.getPlayer();
            p.sendTitle("Поражение!", "", 10, 100, 2);
        }
    }
}
