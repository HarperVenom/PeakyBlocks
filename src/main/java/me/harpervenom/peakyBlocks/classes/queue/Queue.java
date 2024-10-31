package me.harpervenom.peakyBlocks.classes.queue;

import me.harpervenom.peakyBlocks.classes.game.Game;
import me.harpervenom.peakyBlocks.classes.queue.events.PlayerAddedEvent;
import me.harpervenom.peakyBlocks.classes.queue.events.PlayerRemovedEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static me.harpervenom.peakyBlocks.PeakyBlocks.getPlugin;
import static me.harpervenom.peakyBlocks.classes.game.Game.activeGames;
import static me.harpervenom.peakyBlocks.lobby.MenuListener.updatePlayerInventory;
import static me.harpervenom.peakyBlocks.lobby.MenuListener.updateQueueMenu;
import static me.harpervenom.peakyBlocks.utils.MapManager.createWorld;


public class Queue {

    public static List<Queue> activeQueues = new ArrayList<>();
    public static Integer lastQueueId;

    public int id;
    public final UUID creator;
    public final List<QueueTeam> teams = new ArrayList<>();
    private final int numberOfTeams;
    public final QueueScoreBoard scoreboard;
    public int playersCount = 0;

    public Queue(UUID creator, int numberOfTeams) {
        id = lastQueueId == null ? 1 : lastQueueId + 1;
        this.creator = creator;
        for (Queue activeQueue : activeQueues) {
            if (activeQueue.getId() == id) id++;
        }
        lastQueueId = id;
        this.numberOfTeams = numberOfTeams;
        scoreboard = new QueueScoreBoard(this);
    }

    public void setMaxPlayers(int maxPlayerPerTeam) {
        for (int i = 0; i < numberOfTeams; i++) {
            teams.add(new QueueTeam(this, getTeamColor(i), maxPlayerPerTeam));
        }
    }

    public int getId() {
        return id;
    }

    public UUID getCreator() {
        return creator;
    }

    public int getNumberOfTeams() {
        return numberOfTeams;
    }

    public List<QueueTeam> getTeams() {
        return teams;
    }

    public List<QueuePlayer> getPlayers() {
        return teams.stream()
                .flatMap(team -> team.getPlayers().stream()).collect(Collectors.toList());  // Collect all players into a single list
    }
    public int getPlayersCount() {
        return playersCount;
    }

    public int getTeamSize() {
        return teams.getFirst().getMaxPlayers();
    }

    public int getTotalMaxPlayers() {
        return getTeamSize() * numberOfTeams;
    }

    public int getNumberOfPlayers() {
        return teams.stream()    // Stream over the list of teams
                .mapToInt(team -> team.getPlayers().size())  // Map each team to its player count
                .sum();
    }

    private ChatColor getTeamColor(int i) {
        switch (i) {
            case 0 -> {
                return ChatColor.RED;
            }
            case 1 -> {
                return ChatColor.BLUE;
            }
        }
        return null;
    }

    public void startGame() {
        createWorld(this);
    }

    public void delete() {
        for (QueuePlayer p : getPlayers()) {
            p.getTeam().removePlayer(p,false, true);
        }
        scoreboard.remove();
        activeQueues.remove(this);

        updateQueueMenu();
    }
}
