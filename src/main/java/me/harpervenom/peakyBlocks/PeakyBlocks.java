package me.harpervenom.peakyBlocks;

import me.harpervenom.peakyBlocks.lastwars.LuckyBook.LuckyBookListener;
import me.harpervenom.peakyBlocks.lastwars.PlayerListener;
import me.harpervenom.peakyBlocks.lastwars.Spawner.SpawnerListener;
import me.harpervenom.peakyBlocks.lastwars.Trader.TraderListener;
import me.harpervenom.peakyBlocks.lastwars.Core.CoreListener;
import me.harpervenom.peakyBlocks.lastwars.GameListener;
import me.harpervenom.peakyBlocks.lastwars.Turret.TurretListener;
import me.harpervenom.peakyBlocks.queue.QueueListener;
import me.harpervenom.peakyBlocks.commands.Lobby;
import me.harpervenom.peakyBlocks.lobby.LobbyListener;
import me.harpervenom.peakyBlocks.lobby.MenuListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import static me.harpervenom.peakyBlocks.lastwars.LuckyBook.Loot.Loot.getLootItemStack;

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
        Bukkit.getPluginManager().registerEvents(new QueueListener(), getPlugin());


        getServer().getPluginManager().registerEvents(new GameListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new CoreListener(), this);
        getServer().getPluginManager().registerEvents(new TurretListener(), this);
        getServer().getPluginManager().registerEvents(new SpawnerListener(), this);

        getServer().getPluginManager().registerEvents(new TraderListener(), this);
        getServer().getPluginManager().registerEvents(new LuckyBookListener(), this);

        getCommand("lobby").setExecutor(new Lobby());

        System.out.println("[PeakyBlocks] Plugin has started!");

        getLootItemStack();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
