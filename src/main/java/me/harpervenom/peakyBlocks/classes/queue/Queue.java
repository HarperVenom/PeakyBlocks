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


public class Queue implements Listener {

    public static List<Queue> activeQueues = new ArrayList<>();
    public static Integer lastQueueId;

    private int id;
    private final UUID creator;
    private final List<QueueTeam> teams = new ArrayList<>();
    private final int numberOfTeams;
    private final QueueScoreBoard scoreboard;
    private int playersCount = 0;

    public Queue(UUID creator, int numberOfTeams) {
        id = lastQueueId == null ? 1 : lastQueueId + 1;
        this.creator = creator;
        for (Queue activeQueue : activeQueues) {
            if (activeQueue.getId() == id) id++;
        }
        lastQueueId = id;
        this.numberOfTeams = numberOfTeams;
        scoreboard = new QueueScoreBoard(this);

        Bukkit.getPluginManager().registerEvents(this, getPlugin());
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

    @EventHandler
    public void OnPlayerAdded(PlayerAddedEvent e) {
        QueueTeam team = e.getTeam();
        if (!teams.contains(team)) return;
        QueuePlayer qp = e.getPlayer();
        Player p = qp.getPlayer();

        //Leave previous team if any
        QueueTeam oldTeam = qp.getTeam();
        qp.setTeam(team);
        if (oldTeam != null) {
            Queue oldQueue = oldTeam.getQueue();
            oldTeam.removePlayer(qp, oldQueue.getId() == id, false);
        }

        playersCount++;
        p.sendMessage(ChatColor.GRAY + "Вы присоединились к команде: " + team.getColor() + team.getTeamName());

        if (playersCount >= getTotalMaxPlayers() - 1) {
            scoreboard.startCountdown();
        }
        scoreboard.update();
        updatePlayerInventory(qp.getPlayer());
    }

    @EventHandler
    public void OnPlayerRemove(PlayerRemovedEvent e) {
        QueueTeam team = e.getTeam();
        if (!teams.contains(team)) return;

        QueuePlayer qp = e.getPlayer();
        Player p = qp.getPlayer();
        boolean isChangingTeams = e.isChangingTeams();
        boolean isSilent = e.isSilent();

        if (qp.getTeam().equals(team)) qp.setTeam(null);

        if (qp.getId() == creator && !isChangingTeams) {
            delete();
            updatePlayerInventory(qp.getPlayer());
            if (!isSilent) p.sendMessage(ChatColor.DARK_GRAY + "Вы удалили очередь.");
            return;
        }

        if (!isChangingTeams) {
            scoreboard.removeForPlayer(qp.getPlayer());
        }

        playersCount--;

        if (!isSilent && qp.getTeam().getQueue().getId() != id) {
            p.sendMessage(ChatColor.DARK_GRAY + "Вы покинули игру.");
        } else {
            p.sendMessage(ChatColor.DARK_GRAY + "Вы покинули команду.");
        }

        if (playersCount == 0 && !isChangingTeams) {
            delete();
            return;
        }

        if (!isChangingTeams && playersCount < getTotalMaxPlayers() - 1) {
            scoreboard.resetCountdown();
        }
        scoreboard.update();
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
        activeGames.add(new Game(this));
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
