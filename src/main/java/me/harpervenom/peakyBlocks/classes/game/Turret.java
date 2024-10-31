package me.harpervenom.peakyBlocks.classes.game;

import me.harpervenom.peakyBlocks.classes.game.evens.CoreDestroyedEvent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Rotatable;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Turret {

    public static List<Turret> turrets = new ArrayList<>();

    private final Location base;
    private Location block;
    private final BlockFace facing;
    private GameTeam team;
    private static int maxHealth = 10;
    private int health;

    public Turret(Location base, BlockFace facing, GameTeam team) {
        this.base = base;
        this.facing = facing;
        this.team = team;
        this.health = maxHealth;

        turrets.add(this);

        buildStructure();
    }

    private void buildStructure() {
        Location location = new Location(base.getWorld(), base.getX(), base.getY() + 1, base.getZ());
        location.getBlock().setType(Material.SMOOTH_STONE);

        location = new Location(base.getWorld(), base.getX(), base.getY() + 2, base.getZ());
        block = location;
        location.getBlock().setType(Material.LODESTONE);

        location = new Location(base.getWorld(), base.getX(), base.getY() + 3, base.getZ());
        location.getBlock().setType(Material.SMOOTH_STONE_SLAB);
    }

    public int getHealth() {
        return health;
    }

//    public void damage(Player p) {
//        health--;
//        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(health + "/" + maxHealth));
//        if (health <= 0) {
//            Bukkit.getPluginManager().callEvent(new CoreDestroyedEvent(this));
//        }
//    }

    public Location getBlock() {
        return block;
    }

    public GameTeam getTeam() {
        return team;
    }
}
