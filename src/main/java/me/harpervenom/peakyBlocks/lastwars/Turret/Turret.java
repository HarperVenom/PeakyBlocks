package me.harpervenom.peakyBlocks.lastwars.Turret;

import me.harpervenom.peakyBlocks.lastwars.Game;
import me.harpervenom.peakyBlocks.lastwars.GamePlayer;
import me.harpervenom.peakyBlocks.lastwars.GameTeam;
import me.harpervenom.peakyBlocks.lastwars.Map.Map;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

import static me.harpervenom.peakyBlocks.PeakyBlocks.getPlugin;
import static me.harpervenom.peakyBlocks.lastwars.GameListener.destructExplosions;
import static me.harpervenom.peakyBlocks.lastwars.GameListener.noDamageExplosions;

public class Turret {

    public static List<Turret> turrets = new ArrayList<>();

    private final Location loc;
    private Location block;
    private GameTeam team;
    private static int maxHealth = 10;
    private int health;
    private boolean isBreakable;

    private BukkitRunnable shootingTask;
    private BukkitRunnable scanningTask;

    private static final int baseShootingInterval = 30;
    private static final int baseMinShootingInterval = 30;
    private static final int baseIntervalDecrement = 1;
    private static final float baseArrowSpeed = 1.5F;

    private int detectionRadius = 10;

    private int minShootingInterval = baseMinShootingInterval;
    private int initialShootingInterval = baseShootingInterval;
    private int intervalDecrement = baseIntervalDecrement;
    private float arrowSpeed = baseArrowSpeed;

    private int shootingInterval = initialShootingInterval;

    public boolean isRunning;
    private ArmorStand shooter;
    private Player target;

    private List<Location> blocks = new ArrayList<>();

    public Turret(Location loc, boolean isBreakable) {
        this.loc = loc;
        this.isBreakable = isBreakable;

        if (!isBreakable) {
            minShootingInterval = 10;
            initialShootingInterval = 10;
            shootingInterval = 10;
            arrowSpeed = 3F;
            detectionRadius = 8;
        }

        this.health = maxHealth;

        isRunning = false;
    }

    public Turret(Turret sample) {
        this.loc = sample.loc.clone();  // Clone location to avoid shared references
        this.block = sample.block != null ? sample.block.clone() : null;  // Clone if block is not null
        this.team = sample.team;
        this.health = sample.health;
        this.isBreakable = sample.isBreakable;

        this.minShootingInterval = sample.minShootingInterval;
        this.initialShootingInterval = sample.initialShootingInterval;
        this.intervalDecrement = sample.intervalDecrement;
        this.arrowSpeed = sample.arrowSpeed;
        this.detectionRadius = sample.detectionRadius;
        this.shootingInterval = sample.shootingInterval;
        this.isRunning = sample.isRunning;

        // Clone blocks list to avoid shared references
        this.blocks = new ArrayList<>();
        for (Location blockLocation : sample.blocks) {
            this.blocks.add(blockLocation.clone());
        }

        if (isBreakable) turrets.add(this);
    }

    public void buildStructure() {
        Location location = new Location(loc.getWorld(), loc.getX(), loc.getY() + 1, loc.getZ());
        blocks.add(location);
        location.getBlock().setType(isBreakable ? Material.SMOOTH_STONE : Material.BEDROCK);

        location = new Location(loc.getWorld(), loc.getX(), loc.getY() + 2, loc.getZ());

        if (isBreakable) {
            block = location;
            location.getBlock().setType(Material.LODESTONE);
        } else {
            location.getBlock().setType(Material.BEDROCK);
            blocks.add(location);

            location = new Location(loc.getWorld(), loc.getX(), loc.getY() + 3, loc.getZ());
            location.getBlock().setType(Material.LODESTONE);
            block = location;
        }
        blocks.add(location);

        Map map = team.getGame().getMap();
        blocks.forEach(map::addLoc);

        shooter = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(0.5, 0.5, 0.5), EntityType.ARMOR_STAND);
        shooter.setCustomName(team.getColor() + "[Турель]");
        shooter.setAI(false);
        shooter.setInvulnerable(true);
        shooter.setSilent(true);
        shooter.setInvisible(true);
        shooter.setMarker(true);

        startScanningTask();
    }

    public int getHealth() {
        return health;
    }

    public List<Location> getBlocks() {
        return blocks;
    }

    public boolean isBreakable() {
        return isBreakable;
    }

    public void damage(Player p) {
        health--;
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(health + "/" + maxHealth));

        if (!isRunning) {
            Location loc = block.clone().add(0.5, 0, 0.5);
            destructExplosions.add(block);
            noDamageExplosions.add(block);
            block.getWorld().createExplosion(loc, 6);

            p.setHealth(p.getHealth() < 5 ? 0 : p.getHealth() - 5);
            p.playSound(p, Sound.ENTITY_PLAYER_HURT, 1, 1);

            scanArea();
        }

        if (health <= 0) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
            Bukkit.getPluginManager().callEvent(new TurretDestroyEvent(this));
        }
    }

    public void destroy() {
        shootingTask.cancel();
        noDamageExplosions.add(block);
        block.getWorld().createExplosion(block, 2, false, false);

        for (Location loc : blocks) {
            loc.getBlock().setType(Material.AIR);
        }
        turrets.remove(this);
    }

    public Location getLoc() {
        return loc;
    }
    public Location getBlock() {
        return block;
    }

    public GameTeam getTeam() {
        return team;
    }
    public void setTeam(GameTeam team) {
        this.team = team;
    }

    public void startShootingTask() {

        shootingTask = new BukkitRunnable() {
            @Override
            public void run() {
                isRunning = true;
                scanArea();
                boolean attack = false;
                if (target == null || target.isDead()) {
                    isRunning = false;
                    shootingInterval = initialShootingInterval;
                    return;
                }

                Location blockCenter = block.clone().add(0.5, 0.5, 0.5);
                Location spawnLocation = blockCenter.clone();

                List<Location> targetLocations = new ArrayList<>();
                Location targetLocation = target.getEyeLocation().clone();
                targetLocations.add(targetLocation);
                targetLocations.add(targetLocation.clone().add(0, -1, 0));

                for (Location targetLoc : targetLocations) {
                    Vector direction = targetLoc.toVector().subtract(spawnLocation.toVector()).normalize();

                    spawnLocation.add(direction.multiply(0.8));

                    double distance = spawnLocation.distance(target.getEyeLocation());

                    RayTraceResult result = spawnLocation.getWorld().rayTraceBlocks(spawnLocation.clone().subtract(direction.multiply(0.2)),
                            direction, distance);

                    if (result == null || result.getHitBlock() == null) {
                        if (target.getLocation().getY() > block.getY() ? distance < 2 : distance < 0.9) {
                            punchTarget();
                        } else {
                            shootTarget(spawnLocation, direction);
                        }
                        attack = true;
                        break;
                    }
                }

                if (attack) {
                    isRunning = true;
                    this.cancel();
                    if (shootingInterval > minShootingInterval) shootingInterval -= intervalDecrement;

                    if (shootingInterval < minShootingInterval) shootingInterval = minShootingInterval;
                    startShootingTask();
                } else {
                    isRunning = false;
                }
            }
        };
        shootingTask.runTaskLater(getPlugin(), isRunning ? shootingInterval : 0);
    }

    public void startScanningTask() {
        scanningTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!isRunning) scanArea();
            }
        };

        scanningTask.runTaskTimer(getPlugin(), 0, 10);
    }

    public void scanArea() {

        List<Player> players = getPlayersInRadius();
        if (players.isEmpty()) {
            target = null;
            return;
        }

        if (target == null || !players.contains(target)) {
            target = players.getFirst();
        }

        if (!isRunning) startShootingTask();
    }

    public List<Player> getPlayersInRadius() {
        List<Player> playersInRadius = new ArrayList<>();
        Game game = team.getGame();

        for (GamePlayer gp : game.getPlayers()) {
            Player p = gp.getPlayer();

            if (!team.isMember(gp)) {  // Assuming GameTeam has an isMember method to check team membership
                double distance = p.getLocation().distance(block);
                if (distance < (double) detectionRadius) {
                    playersInRadius.add(p);
                }
            }
        }

        return playersInRadius;
    }

    private void shootTarget(Location spawnLocation, Vector direction) {
        Arrow arrow = block.getWorld().spawnArrow(spawnLocation, direction, arrowSpeed, 0);  // Adjust arrow speed as needed

        arrow.getWorld().spawnParticle(Particle.SMOKE, arrow.getLocation(), 10, 0, 0, 0, 0.02);
        arrow.getWorld().playSound(arrow.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1F, (float) (1 + 0.1*((float) 30 / shootingInterval)));

        arrow.setShooter(shooter);
        arrow.setGravity(false);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!arrow.isDead()) {
                    arrow.setGravity(true);
                }
            }
        }.runTaskLater(getPlugin(), 5);

        int customLifespan = 20;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!arrow.isDead()) {
                    arrow.remove();
                }
            }
        }.runTaskLater(getPlugin(), customLifespan);
    }

    public void punchTarget() {
        target.damage(1);

        Vector direction = target.getEyeLocation().toVector().subtract(block.clone().add(0.5, 0.5, 0.5).toVector()).normalize();

        direction = direction.multiply(1.75);

        target.setVelocity(direction);

        block.getWorld()
                .spawnParticle(Particle.FLAME, block.clone().add(0.5, 0.5, 0.5), 20, 0.5, 0.5, 0.5, 0);
        block.getWorld().playSound(block, Sound.ENTITY_BLAZE_HURT, 1, 0.8f);
    }

    public ArmorStand getShooter() {
        return shooter;
    }
}
