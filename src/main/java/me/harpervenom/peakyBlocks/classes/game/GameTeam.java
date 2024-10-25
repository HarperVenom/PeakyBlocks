package me.harpervenom.peakyBlocks.classes.game;

import me.harpervenom.peakyBlocks.classes.queue.QueuePlayer;
import me.harpervenom.peakyBlocks.classes.queue.QueueTeam;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class GameTeam {

    private final ChatColor color;
    private final List<GamePlayer> members = new ArrayList<>();

    private Game game;
    private Location spawn;

    public GameTeam(QueueTeam team) {
        this.color = team.getColor();

        for (QueuePlayer qp : team.getPlayers()) {
            GamePlayer gamePlayer = new GamePlayer(qp.getId());
            gamePlayer.setTeam(this);
            members.add(gamePlayer);
        }
    }

    public ChatColor getColor() {
        return color;
    }

    public List<GamePlayer> getPlayers() {
        return members;
    }

    public void removePlayer(GamePlayer p) {
        members.remove(p);
        game.checkTeams();
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

    public void setGame(Game game) {
        this.game = game;
    }

    public Location getSpawn() {
        return spawn;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    public void clear() {
        for (GamePlayer p : members) {
            p.remove();
        }
    }

}
