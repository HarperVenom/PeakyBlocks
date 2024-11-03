package me.harpervenom.peakyBlocks.lastwars.Core;


import me.harpervenom.peakyBlocks.lastwars.GameTeam;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Rotatable;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static me.harpervenom.peakyBlocks.utils.Utils.yawToFace;

public class Core {

    public static List<Core> cores = new ArrayList<>();

    private final Location base;
    private Location block;
    private GameTeam team;
    private static int maxHealth = 10;
    private int health;

    private List<Location> blocks = new ArrayList<>();

    public Core(Location base, GameTeam team) {
        this.base = base;
        this.team = team;
        this.health = maxHealth;

        cores.add(this);

        buildStructure();
    }

    private void buildStructure() {
        Location location = new Location(base.getWorld(), base.getX(), base.getY() + 1, base.getZ());
        blocks.add(location);
        location.getBlock().setType(Material.SMOOTH_STONE);

        location = new Location(base.getWorld(), base.getX(), base.getY() + 2, base.getZ(), base.getYaw(), base.getPitch());
        blocks.add(location);
        block = location;
        location.getBlock().setType(Material.AMETHYST_BLOCK);

        location = new Location(base.getWorld(), base.getX(), base.getY() + 3, base.getZ());
        blocks.add(location);
        location.getBlock().setType(Material.SMOOTH_STONE);

        location = new Location(base.getWorld(), base.getX(), base.getY() + 4, base.getZ());
        blocks.add(location);

        Block b = location.getBlock();
        b.setType(team.getColor() == ChatColor.RED ? Material.RED_BANNER : Material.BLUE_BANNER);

        if (b.getBlockData() instanceof Rotatable rotatable) {
            rotatable.setRotation(yawToFace(block.getYaw())); // Or any other direction: SOUTH, EAST, WEST, etc.
            b.setBlockData(rotatable);
        }
    }

    public int getHealth() {
        return health;
    }

    public void damage(Player p) {
        health--;
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(health + "/" + maxHealth));
        if (health <= 0) {
            Bukkit.getPluginManager().callEvent(new CoreDestroyedEvent(this));
        }
    }

    public void destroy() {
        block.getWorld().createExplosion(block, 3, false, false);
        for (Location loc : blocks) {
            loc.getBlock().setType(Material.AIR);
        }
    }

    public Location getBlock() {
        return block;
    }

    public GameTeam getTeam() {
        return team;
    }

}
