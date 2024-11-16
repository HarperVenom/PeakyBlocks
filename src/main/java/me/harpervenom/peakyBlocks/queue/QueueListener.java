package me.harpervenom.peakyBlocks.queue;

import me.harpervenom.peakyBlocks.lastwars.Game;
import me.harpervenom.peakyBlocks.queue.events.PlayerAddedEvent;
import me.harpervenom.peakyBlocks.queue.events.PlayerRemovedEvent;
import me.harpervenom.peakyBlocks.queue.events.MapCreatedEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static me.harpervenom.peakyBlocks.lastwars.Game.activeGames;
import static me.harpervenom.peakyBlocks.lobby.MenuListener.updatePlayerInventory;
import static me.harpervenom.peakyBlocks.lobby.MenuListener.updateTeamMenu;

public class QueueListener implements Listener {

    @EventHandler
    public void OnPlayerAdded(PlayerAddedEvent e) {
        QueueTeam team = e.getTeam();
        Queue queue = team.getQueue();

        QueuePlayer qp = e.getPlayer();
        Player p = qp.getPlayer();

        //Leave previous team if any
        QueueTeam oldTeam = qp.getTeam();
        qp.setTeam(team);
        if (oldTeam != null) {
            Queue oldQueue = oldTeam.getQueue();
            oldTeam.removePlayer(qp, oldQueue.getId() == queue.id, false);
        }

        queue.playersCount++;
        p.sendMessage(ChatColor.GRAY + "Вы присоединились к команде: " + team.getColor() + team.getTeamName());

        if (queue.playersCount >= queue.getTotalMaxPlayers() - 1) {
            queue.scoreboard.startCountdown();
        }
        queue.scoreboard.update();
        updatePlayerInventory(qp.getPlayer());
        updateTeamMenu(queue);
    }

    @EventHandler
    public void OnPlayerRemove(PlayerRemovedEvent e) {
        QueueTeam team = e.getTeam();
        Queue queue = team.getQueue();

        QueuePlayer qp = e.getPlayer();
        Player p = qp.getPlayer();
        boolean isChangingTeams = e.isChangingTeams();
        boolean isSilent = e.isSilent();

        if (qp.getTeam().equals(team)) {
            qp.setTeam(null);
        }

        updatePlayerInventory(qp.getPlayer());
        updateTeamMenu(queue);

        if (qp.getId() == queue.creator && !isChangingTeams) {
            queue.delete(false);
            if (!isSilent) p.sendMessage(ChatColor.DARK_GRAY + "Вы удалили очередь.");
            return;
        }

        if (!isChangingTeams) {
            queue.scoreboard.removeForPlayer(qp.getPlayer());
        }

        queue.playersCount--;

        if (!isSilent) {
            if (qp.getTeam() == null || team.getQueue().getId() != queue.id) {
                p.sendMessage(ChatColor.DARK_GRAY + "Вы покинули игру.");
            } else {
                p.sendMessage(ChatColor.DARK_GRAY + "Вы покинули команду.");
            }
        }

        if (queue.playersCount == 0 && !isChangingTeams) {
            queue.delete(false);
            return;
        }

        if (!isChangingTeams && queue.playersCount < queue.getTotalMaxPlayers() - 1) {
            queue.scoreboard.resetCountdown();
        }
        queue.scoreboard.update();
    }

    @EventHandler
    public void OnWorldCreated(MapCreatedEvent e) {
        Queue queue = e.getQueue();

        activeGames.add(new Game(queue));
    }
}
