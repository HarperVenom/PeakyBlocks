package me.harpervenom.peakyBlocks.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.harpervenom.peakyBlocks.lobby.LobbyListener.setLobbyState;

public class Lobby implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) return false;

        p.sendMessage(ChatColor.GRAY + "Перемещение в Лобби.");
        p.teleport(new Location(Bukkit.getWorld("lobby"), 0.5, 0.5, 0.5));
        setLobbyState(p);
        return true;
    }
}
