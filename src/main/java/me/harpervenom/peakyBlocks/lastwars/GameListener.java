package me.harpervenom.peakyBlocks.lastwars;

import me.harpervenom.peakyBlocks.lastwars.Core.Core;
import me.harpervenom.peakyBlocks.lastwars.Map.Map;
import me.harpervenom.peakyBlocks.lastwars.Turret.Turret;
import me.harpervenom.peakyBlocks.lastwars.Core.CoreDestroyedEvent;
import me.harpervenom.peakyBlocks.lastwars.Turret.TurretDestroyEvent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static me.harpervenom.peakyBlocks.PeakyBlocks.getPlugin;
import static me.harpervenom.peakyBlocks.lastwars.Game.activeGames;
import static me.harpervenom.peakyBlocks.lastwars.Game.getGameByWorld;
import static me.harpervenom.peakyBlocks.lastwars.GamePlayer.getGamePlayer;
import static me.harpervenom.peakyBlocks.lastwars.GameTeam.getEntityTeam;
import static me.harpervenom.peakyBlocks.lastwars.Map.MapManager.removeWorld;

public class GameListener implements Listener {

    public static List<Location> noDamageExplosions = new ArrayList<>();
    public static List<Location> turretExplosions = new ArrayList<>();

    @EventHandler
    public void BlockBreak(BlockBreakEvent e) {
        Block b = e.getBlock();
        World world = b.getWorld();
        Game game = getGameByWorld(world);
        if (game == null) return;
        Map map = game.getMap();

        if (map.containsBlock(b)) e.setCancelled(true);
    }

    @EventHandler
    public void BlockPlace(BlockPlaceEvent e) {
        Block b = e.getBlock();
        World world = b.getWorld();
        Game game = getGameByWorld(world);
        if (game == null) return;

        if (game.isBlockProtected(b)) {
            e.setCancelled(true);
            Player p = e.getPlayer();
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "Территория защищена!"));
        }
    }

    @EventHandler
    public void onLiquidFlow(BlockFromToEvent e) {
        Block b = e.getBlock();
        World world = b.getWorld();
        Game game = getGameByWorld(world);
        if (game == null) return;

        Block toBlock = e.getToBlock();

        // Check if the target block is within the protected area
        if (game.isBlockProtected(toBlock)) {
            e.setCancelled(true); // Prevent the liquid from flowing into the protected area
        }
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent e) {
        Block b = e.getBlock();
        World world = b.getWorld();
        Game game = getGameByWorld(world);
        if (game == null) return;

        Block targetBlock = e.getBlockClicked().getRelative(e.getBlockFace());

        if (game.isBlockProtected(targetBlock)) {
            e.setCancelled(true);
            Player p = e.getPlayer();
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "Территория защищена!"));
        }
    }

    @EventHandler
    public void worldLeave(PlayerChangedWorldEvent e) {
        World world = e.getFrom();
        if (world.getName().equals("lobby")) return;

        if (world.getPlayers().isEmpty()) {
            removeWorld(world);
        }

        GamePlayer gp = getGamePlayer(e.getPlayer());
        if (gp == null) return;
        gp.remove();
    }

    @EventHandler
    public void OnCoreDestroyed(CoreDestroyedEvent e) {
        Core core = e.getCore();
        GameTeam looser = core.getTeam();
        Game game = looser.getGame();

        looser.destroyCore();

        game.teams.remove(looser);
        game.deadTeams.add(looser);

        if (game.teams.size() <= 1) {
            GameTeam winner = game.teams.getFirst();
            game.sendMessage(looser.getColor() + "[Ядро]" + ChatColor.WHITE + " разрушено!");
            winner.win();
            looser.loose();
            game.sendMessage("Комнада " + winner.getColor() + "'" + winner.getName() + "'" + ChatColor.WHITE + " одержала победу!");
            Bukkit.getScheduler().runTaskLater(getPlugin(), new Runnable() {
                @Override
                public void run() {
                    game.finish();
                }
            },120);
        }
    }

    @EventHandler
    public void OnTurretDestroy(TurretDestroyEvent e) {
        Turret turret = e.getTurret();
        GameTeam team = turret.getTeam();
        Game game = team.getGame();

        team.destroyTurret(turret);
        game.sendMessage(turret.getShooter().getCustomName() + ChatColor.WHITE + " разрушена!");
    }

    @EventHandler
    public void Explode(BlockExplodeEvent e) {
        Location loc = e.getBlock().getLocation();
        List<Block> blocks = e.blockList();

        updateBlockList(loc, blocks);
    }

    @EventHandler
    public void EntityExplode(EntityExplodeEvent e) {
        Location loc = e.getEntity().getLocation();
        List<Block> blocks = e.blockList();

        updateBlockList(loc, blocks);
    }

    public void updateBlockList(Location loc, List<Block> blocks) {
        World world = loc.getWorld();
        Game game = activeGames.stream().filter(currentGame -> currentGame.getWorld().getName().equals(world.getName())).findFirst().orElse(null);

        if (game == null) return;

        blocks.removeIf(block -> game.getMap().containsBlock(block));

        int radius = 5; // Set your explosion radius

        // Loop through blocks in the explosion radius
        if (turretExplosions.contains(loc)) {
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        Block block = world.getBlockAt(loc.clone().add(x, y, z));
                        // Check if block is obsidian and not already in the list

                        if (block.getType() == Material.OBSIDIAN && !blocks.contains(block)) {
                            blocks.add(block); // Add obsidian to the explosion-affected blocks
                        }
                    }
                }
            }
            turretExplosions.remove(loc);
        }
    }

    @EventHandler
    public void onExplosionDamage(EntityDamageEvent e) {
        if (e.getCause() != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) return;

        Location entityLoc = e.getEntity().getLocation();

        for (Location explosionLoc : noDamageExplosions) {
            if (explosionLoc.distance(entityLoc) < 6) {
                e.setCancelled(true);
                break;
            }
        }
    }

    private final HashMap<UUID, EntityType> spawnEggUsers = new HashMap<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        // Check if the item is a spawn egg
        if (item.getType().toString().endsWith("_SPAWN_EGG")) {
            EntityType entityType = getEntityTypeFromSpawnEgg(item.getType());
            if (entityType != null) {
                // Store the player and spawn egg type
                spawnEggUsers.put(player.getUniqueId(), entityType);
            }
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        // Check if the entity was spawned with a spawn egg
        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) {
            Entity entity = e.getEntity();
            EntityType entityType = entity.getType();

            // Find the player who used the spawn egg, if any
            for (HashMap.Entry<UUID, EntityType> entry : spawnEggUsers.entrySet()) {
                if (entry.getValue() == entityType) {
                    Player p = Bukkit.getPlayer(entry.getKey());
                    if (p == null) return;
                    GamePlayer gp = getGamePlayer(p);
                    if (gp == null) return;

                    gp.getTeam().getTeam().addEntry(entity.getUniqueId().toString());

                    spawnEggUsers.remove(p.getUniqueId());
                    break;
                }
            }
        }
    }

    // Helper method to get EntityType from spawn egg material
    private EntityType getEntityTypeFromSpawnEgg(Material material) {
        try {
            return EntityType.valueOf(material.toString().replace("_SPAWN_EGG", ""));
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent e) {
        Entity target = e.getTarget();

        if ((e.getEntity() instanceof Monster || e.getEntity() instanceof IronGolem) && target instanceof Player player) {
            if (isEntityOnSameTeam(e.getEntity(), player)) {
                e.setCancelled(true);
            }
        }
    }

    private boolean isEntityOnSameTeam(Entity entity, Player p) {
        GamePlayer gp = getGamePlayer(p);
        if (gp == null) return false;

        return gp.getTeam().getTeam().getEntries().contains(entity.getUniqueId().toString());
    }

    @EventHandler
    public void EntityDeath(EntityDeathEvent e) {
        Entity entity = e.getEntity();
        GameTeam team = getEntityTeam(entity);
        if (team == null) return;
        team.getTeam().removeEntry(entity.getUniqueId().toString());
    }

}
