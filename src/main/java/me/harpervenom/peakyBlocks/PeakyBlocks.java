package me.harpervenom.peakyBlocks;

import me.harpervenom.peakyBlocks.commands.Lobby;
import me.harpervenom.peakyBlocks.game.GameListener;
import me.harpervenom.peakyBlocks.lobby.LobbyListener;
import me.harpervenom.peakyBlocks.lobby.MenuListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class PeakyBlocks extends JavaPlugin {

    private static PeakyBlocks plugin;

    public static PeakyBlocks getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;

        getServer().getPluginManager().registerEvents(new LobbyListener(), this);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        getServer().getPluginManager().registerEvents(new GameListener(), this);

        getCommand("lobby").setExecutor(new Lobby());

        System.out.println("[PeakyBlocks] Plugin has started!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
