package me.harpervenom.peakyBlocks.classes.game;

import me.harpervenom.peakyBlocks.classes.queue.QueuePlayer;
import me.harpervenom.peakyBlocks.classes.queue.QueueTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

import static me.harpervenom.peakyBlocks.PeakyBlocks.getPlugin;
import static me.harpervenom.peakyBlocks.classes.game.GamePlayer.gamePlayers;

public class GameTeam {

    private final ChatColor color;
    private final List<GamePlayer> members = new ArrayList<>();

    private Game game;
    private Location spawn;
    private Core core;
    private Turret turret;
    private BlockFace facing;
    private Scoreboard scoreboard;

    public GameTeam(QueueTeam queueTeam) {
        this.color = queueTeam.getColor();

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager != null) {
            scoreboard = manager.getMainScoreboard();
        }

        Team team = scoreboard.getTeam(queueTeam.getTeamName());
        if (team == null) {
            team = scoreboard.registerNewTeam(queueTeam.getTeamName());
            team.setColor(color);
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
        }

        for (QueuePlayer qp : queueTeam.getPlayers()) {
            GamePlayer gamePlayer = new GamePlayer(qp.getId());
            gamePlayer.setTeam(this);
            gamePlayers.add(gamePlayer);
            members.add(gamePlayer);

            team.addEntry(gamePlayer.getPlayer().getName());

            Scoreboard emptyScoreboard = manager.getNewScoreboard();
            Player player = gamePlayer.getPlayer();
            player.setScoreboard(emptyScoreboard);

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
        this.core = new Core(coreLoc, facing, this);
    }

    public Turret getTurret() {
        return turret;
    }
    public void setTurret(Location turretLoc) {
        this.turret = new Turret(turretLoc, facing, this);
    }

    public BlockFace getFacing() {
        return facing;
    }
    public void setFacing(BlockFace facing) {
        this.facing = facing;
    }

    public void win() {
        for (GamePlayer gp : getPlayers()) {
            Player p = gp.getPlayer();
            p.sendTitle( ChatColor.YELLOW + "Победа!", "Команда " + getColor() + getName() +  ChatColor.WHITE + " одержала победу.", 10, 100, 2);
        }
    }

    public void loose(GameTeam winner) {
        for (GamePlayer gp : getPlayers()) {
            Player p = gp.getPlayer();
            p.sendTitle( ChatColor.RED + "Поражение!", "Команда " + winner.getColor() + winner.getName() + ChatColor.WHITE + " одержала победу.", 10, 100, 2);
        }
    }

//    public void loose() {
//        for (GamePlayer gp : getPlayers()) {
//            Player p = gp.getPlayer();
//            p.sendTitle( ChatColor.RED + "Поражение!", "Команда" + winner.getColor() + winner.getTeamName() + "одержала победу.", 10, 100, 2);
//        }
//    }
}
