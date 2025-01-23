package me.harpervenom.peakyBlocks.lastwars;

import me.harpervenom.peakyBlocks.lastwars.Core.Core;
import me.harpervenom.peakyBlocks.lastwars.Spawner.Spawner;
import me.harpervenom.peakyBlocks.lastwars.Spawner.SpawnerListener;
import me.harpervenom.peakyBlocks.lastwars.Turret.Turret;
import me.harpervenom.peakyBlocks.queue.QueuePlayer;
import me.harpervenom.peakyBlocks.queue.QueueTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static me.harpervenom.peakyBlocks.PeakyBlocks.getPlugin;
import static me.harpervenom.peakyBlocks.lastwars.Game.getGameByWorld;
import static me.harpervenom.peakyBlocks.lastwars.GamePlayer.gamePlayers;
import static me.harpervenom.peakyBlocks.lastwars.GamePlayer.getGamePlayer;

public class GameTeam {

    private final ChatColor color;
    private final List<GamePlayer> members = new ArrayList<>();

    public static GameTeam getEntityTeam(Entity entity) {
        World world = entity.getWorld();
        Game game = getGameByWorld(world);
        if (entity instanceof Player p) {
            GamePlayer gp = getGamePlayer(p);
            if (gp == null) return null;
            return gp.getTeam();
        }
        if (game == null) return null;
        for (GameTeam team : game.getTeams()) {
            if (team.getTeam().getEntries().contains(entity.getUniqueId().toString())) return team;
        }
        return null;
    }

    public static boolean inSameTeam(Entity entity1, Entity entity2) {
        if (!(entity1 instanceof LivingEntity) || !(entity2 instanceof LivingEntity)) return false;
        GameTeam team1 = getEntityTeam(entity1);
        GameTeam team2 = getEntityTeam(entity2);
        if (team1 == null || team2 == null) return true;
        return team1.equals(team2);
    }

    private Game game;
    private Location spawn;
    private Core core;
    private List<Turret> turrets = new ArrayList<>();
    private final Team team;

    public GameTeam(Game game, QueueTeam queueTeam) {
        this.color = queueTeam.getColor();

        this.game = game;

        Scoreboard scoreboard = game.getScoreboard();
        team = scoreboard.registerNewTeam(queueTeam.getTeamName());
        team.setColor(color);
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
        team.setAllowFriendlyFire(false);
        team.setCanSeeFriendlyInvisibles(false);

        for (QueuePlayer qp : queueTeam.getPlayers()) {
            GamePlayer gamePlayer = new GamePlayer(qp.getId());
            gamePlayer.setTeam(this);
            gamePlayers.add(gamePlayer);
            members.add(gamePlayer);

            team.addEntry(gamePlayer.getPlayer().getName());
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

    public Team getTeam() {
        return team;
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

    public void sendMessage(Player p, String message) {
        if (message.startsWith("!")) {
            message = message.substring(1);
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                player.sendMessage(team.getColor() + p.getDisplayName() + ChatColor.GRAY + ": " + ChatColor.GRAY + message);
            }
            return;
        }

        for (GamePlayer gp : getPlayers()) {
            Player player = gp.getPlayer();
            player.sendMessage(team.getColor() + "[Команда] " + ChatColor.WHITE + p.getDisplayName() + ChatColor.GRAY + ": " + ChatColor.GRAY + message);
        }
    }
}
