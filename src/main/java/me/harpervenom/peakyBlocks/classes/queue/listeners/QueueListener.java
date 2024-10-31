package me.harpervenom.peakyBlocks.classes.queue.listeners;

import me.harpervenom.peakyBlocks.classes.queue.Queue;
import me.harpervenom.peakyBlocks.classes.queue.QueuePlayer;
import me.harpervenom.peakyBlocks.classes.queue.QueueTeam;
import me.harpervenom.peakyBlocks.classes.queue.events.PlayerAddedEvent;
import me.harpervenom.peakyBlocks.classes.queue.events.PlayerRemovedEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static me.harpervenom.peakyBlocks.lobby.MenuListener.updatePlayerInventory;

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
    }

    @EventHandler
    public void OnPlayerRemove(PlayerRemovedEvent e) {
        QueueTeam team = e.getTeam();
        Queue queue = team.getQueue();

        QueuePlayer qp = e.getPlayer();
        Player p = qp.getPlayer();
        boolean isChangingTeams = e.isChangingTeams();
        boolean isSilent = e.isSilent();

        if (qp.getTeam().equals(team)) qp.setTeam(null);

        if (qp.getId() == queue.creator && !isChangingTeams) {
            queue.delete();
            updatePlayerInventory(qp.getPlayer());
            if (!isSilent) p.sendMessage(ChatColor.DARK_GRAY + "Вы удалили очередь.");
            return;
        }

        if (!isChangingTeams) {
            queue.scoreboard.removeForPlayer(qp.getPlayer());
        }

        queue.playersCount--;

        if (!isSilent && qp.getTeam().getQueue().getId() != queue.id) {
            p.sendMessage(ChatColor.DARK_GRAY + "Вы покинули игру.");
        } else {
            p.sendMessage(ChatColor.DARK_GRAY + "Вы покинули команду.");
        }

        if (queue.playersCount == 0 && !isChangingTeams) {
            queue.delete();
            return;
        }

        if (!isChangingTeams && queue.playersCount < queue.getTotalMaxPlayers() - 1) {
            queue.scoreboard.resetCountdown();
        }
        queue.scoreboard.update();
    }
}
