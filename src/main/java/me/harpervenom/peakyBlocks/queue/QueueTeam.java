package me.harpervenom.peakyBlocks.queue;

import me.harpervenom.peakyBlocks.queue.events.PlayerAddedEvent;
import me.harpervenom.peakyBlocks.queue.events.PlayerRemovedEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.HashSet;
import java.util.Set;

public class QueueTeam {

    private final Queue queue;
    private final ChatColor color;
    private final int maxMembers;
    private final Set<QueuePlayer> members = new HashSet<>();

    public QueueTeam(Queue queue, ChatColor color, int maxMembers) {
        this.queue = queue;
        this.color = color;
        this.maxMembers = maxMembers;
    }

    public Queue getQueue() {
        return queue;
    }
    public ChatColor getColor() {
        return color;
    }
    public int getMaxPlayers() {
        return maxMembers;
    }
    public Set<QueuePlayer> getPlayers() {
        return members;
    }

    public void addPlayer(QueuePlayer p) {
        if (members.contains(p)) {
            p.getPlayer().sendMessage(ChatColor.YELLOW + "Вы уже находитесь в этой команде.");
            return;
        }

        if (getMaxPlayers() <= getPlayers().size()) {
            p.getPlayer().sendMessage(ChatColor.RED + "В команде нет свободных мест.");
            return;
        }

        if (members.add(p)) {
            Bukkit.getPluginManager().callEvent(new PlayerAddedEvent(p, this));
        }
    }

    public void removePlayer(QueuePlayer p, boolean isChangingTeams, boolean isSilent) {
        if (members.remove(p)) {
            Bukkit.getPluginManager().callEvent(new PlayerRemovedEvent(p, this, isChangingTeams, isSilent));
        }
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
}
