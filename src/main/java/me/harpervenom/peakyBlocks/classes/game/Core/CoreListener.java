package me.harpervenom.peakyBlocks.classes.game.Core;

import me.harpervenom.peakyBlocks.classes.game.GamePlayer;
import me.harpervenom.peakyBlocks.classes.game.GameTeam;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import static me.harpervenom.peakyBlocks.classes.game.Core.Core.cores;
import static me.harpervenom.peakyBlocks.classes.game.GamePlayer.getGamePlayer;

public class CoreListener implements Listener {

    @EventHandler
    public void CoreDamage(BlockBreakEvent e) {
        Player p = e.getPlayer();
        GamePlayer gp = getGamePlayer(p);
        Block b = e.getBlock();

        if (gp == null) {
            e.setCancelled(true);
            return;
        }

        for (Core core : cores) {
            if (core.getBlock().equals(b.getLocation())) {
                e.setCancelled(true);

                GameTeam team = core.getTeam();
                if (gp.getTeam().equals(team)) return;

                if (team.getTurret() != null) {
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "Сперва нужно разрушить турель!"));
                    return;
                }

                core.damage(p);
            }
        }
    }
}
