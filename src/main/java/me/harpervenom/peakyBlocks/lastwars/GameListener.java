package me.harpervenom.peakyBlocks.lastwars;

import me.harpervenom.peakyBlocks.lastwars.Core.Core;
import me.harpervenom.peakyBlocks.lastwars.Map.Map;
import me.harpervenom.peakyBlocks.lastwars.Turret.Turret;
import me.harpervenom.peakyBlocks.lastwars.Core.CoreDestroyedEvent;
import me.harpervenom.peakyBlocks.lastwars.Turret.TurretDestroyEvent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;
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
import static me.harpervenom.peakyBlocks.lastwars.GameTeam.inSameTeam;
import static me.harpervenom.peakyBlocks.lastwars.Map.MapManager.removeWorld;

public class GameListener implements Listener {

    public static HashMap<World, List<Location>> noDamageExplosions = new HashMap<>();
    public static HashMap<World, List<Location>> turretExplosions = new HashMap<>();

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
        game.sendMessage(turret.getName() + ChatColor.WHITE + " разрушена!");
    }

    @EventHandler
    public void Explode(BlockExplodeEvent e) {
        Location loc = e.getBlock().getLocation().clone().add(0.5, 0.5, 0.5);
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

        int radius = 5;

        for (Location location : turretExplosions.get(world)) {
            if (!(location.getX() == loc.getX() && location.getY() == loc.getY() && location.getZ() == loc.getZ())) continue;

            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        Block block = world.getBlockAt(loc.clone().add(x, y, z));

                        if ((block.getType() == Material.OBSIDIAN || block.getType() == Material.CRYING_OBSIDIAN) && !blocks.contains(block)) {
                            blocks.add(block);
                        }
                    }
                }
            }
            turretExplosions.get(world).remove(loc);
        }
    }

    @EventHandler
    public void onExplosionDamage(EntityDamageEvent e) {
        if (e.getCause() != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) return;

        Location entityLoc = e.getEntity().getLocation();

        for (Location explosionLoc : noDamageExplosions.get(entityLoc.getWorld())) {
            if (explosionLoc.distance(entityLoc) < 6) {
                e.setCancelled(true);
                break;
            }
        }

        for (Location explosionLoc : turretExplosions.get(entityLoc.getWorld())) {
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

        if (item.getType().toString().endsWith("_SPAWN_EGG")) {
            EntityType entityType = getEntityTypeFromSpawnEgg(item.getType());
            if (entityType != null) {
                spawnEggUsers.put(player.getUniqueId(), entityType);
            }
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL || e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.OCELOT_BABY) {
            e.setCancelled(true);
            return;
        }

        LivingEntity entity = e.getEntity();

        if (e.getEntity() instanceof Zombie zombie) {
            if (!zombie.isAdult()) {
                e.setCancelled(true);

                Zombie adultZombie = zombie.getWorld().spawn(zombie.getLocation(), Zombie.class);
                adultZombie.setAdult();
                entity = zombie;
            }
        }

        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) {
            EntityType entityType = entity.getType();

            for (HashMap.Entry<UUID, EntityType> entry : spawnEggUsers.entrySet()) {
                if (entry.getValue() == entityType) {
                    Player p = Bukkit.getPlayer(entry.getKey());
                    if (p == null) return;
                    GamePlayer gp = getGamePlayer(p);
                    if (gp == null) return;

                    gp.getTeam().getTeam().addEntry(entity.getUniqueId().toString());
                    entity.setRemoveWhenFarAway(false);
                    entity.setPersistent(true);
                    entity.setCustomName(gp.getTeam().getName());

                    spawnEggUsers.remove(p.getUniqueId());
                    break;
                }
            }

            if (!(e.getEntity() instanceof Mob mob)) return;
            runScanningTask(mob);
        }
    }

    private EntityType getEntityTypeFromSpawnEgg(Material material) {
        try {
            return EntityType.valueOf(material.toString().replace("_SPAWN_EGG", ""));
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent e) {
        if (!(e.getTarget() instanceof LivingEntity target)) return;
        Entity attacker = e.getEntity();
        if (attacker instanceof Mob mob) {
            if (inSameTeam(attacker, target)) {
                e.setCancelled(true);

                scanAreaForTargets(mob);
            }
        }
    }

    public void scanAreaForTargets(Mob mob) {
        if (getEntityTeam(mob) == null) return;

        double radius = mob.getAttribute(Attribute.FOLLOW_RANGE).getBaseValue();
        List<Entity> nearbyEntities = mob.getNearbyEntities(radius, radius, radius);
        LivingEntity closestTarget = null;
        double closestDistance = Double.MAX_VALUE;

        for (Entity nearby : nearbyEntities) {
            if (nearby.isInvulnerable()) continue;
            if (!(nearby instanceof LivingEntity livingNearby)) continue;

            if (inSameTeam(mob, livingNearby)) continue;

            double distance = mob.getLocation().distance(livingNearby.getLocation());

            if (distance < closestDistance) {
                closestDistance = distance;
                closestTarget = livingNearby;
            }
        }

        if (closestTarget != null) {
            mob.setTarget(closestTarget);
        }
    }


    HashMap<UUID, BukkitRunnable> scanningTasks = new HashMap<>();

    public void runScanningTask(Mob mob) {
        if (scanningTasks.containsKey(mob.getUniqueId())) return;

        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (mob.isDead() || !mob.isValid()) {
                    scanningTasks.get(mob.getUniqueId()).cancel();
                    scanningTasks.remove(mob.getUniqueId());
                }
                scanAreaForTargets(mob);
            }
        };

        task.runTaskTimer(getPlugin(), 0, 20);
        scanningTasks.put(mob.getUniqueId(), task);
    }

    @EventHandler
    public void EntityDeath(EntityDeathEvent e) {
        Entity entity = e.getEntity();
        GameTeam team = getEntityTeam(entity);
        if (team == null) return;
        team.getTeam().removeEntry(entity.getUniqueId().toString());
    }

    @EventHandler
    public void ChatMessage(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        GamePlayer gp = getGamePlayer(p);
        if (gp == null) return;
        e.setCancelled(true);
        gp.getTeam().sendMessage(p, e.getMessage());
    }

}
