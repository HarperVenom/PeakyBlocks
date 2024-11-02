package me.harpervenom.peakyBlocks.classes.game.Turret;

import me.harpervenom.peakyBlocks.classes.game.Game;
import me.harpervenom.peakyBlocks.classes.game.GamePlayer;
import me.harpervenom.peakyBlocks.classes.game.GameTeam;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

import static me.harpervenom.peakyBlocks.PeakyBlocks.getPlugin;

public class Turret {

    public static List<Turret> turrets = new ArrayList<>();

    private final Location base;
    private Location block;
    private final BlockFace facing;
    private GameTeam team;
    private static int maxHealth = 10;
    private int health;

    private BukkitRunnable shootingTask;

    private static final int baseShootingInterval = 30;
    private static final float baseArrowSpeed = 1.5F;
    private static final int baseMinShootingInterval = 30;
    private static final int baseIntervalDecrement = 1;

    private static final int DETECTION_RADIUS = 10;

    private int initialShootingInterval = baseShootingInterval;
    private float arrowSpeed = baseArrowSpeed;
    private int minShootingInterval = baseMinShootingInterval;
    private int intervalDecrement = baseIntervalDecrement;

    private int shootingInterval = initialShootingInterval;

    public boolean isRunning;
    private ArmorStand shooter;
    private Player target;

    private List<Location> blocks = new ArrayList<>();

    public Turret(Location base, BlockFace facing, GameTeam team) {
        this.base = base;
        this.facing = facing;
        this.team = team;
        this.health = maxHealth;

        turrets.add(this);

        buildStructure();
        isRunning = false;
    }

    private void buildStructure() {
        Location location = new Location(base.getWorld(), base.getX(), base.getY() + 1, base.getZ());
        blocks.add(location);
        location.getBlock().setType(Material.SMOOTH_STONE);

        location = new Location(base.getWorld(), base.getX(), base.getY() + 2, base.getZ());
        blocks.add(location);
        block = location;
        location.getBlock().setType(Material.LODESTONE);

        shooter = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(0.5, 0.5, 0.5), EntityType.ARMOR_STAND);
        shooter.setCustomName(team.getColor() + "[Турель]");
        shooter.setAI(false);
        shooter.setInvulnerable(true);
        shooter.setSilent(true);
        shooter.setInvisible(true);
        shooter.setMarker(true);
    }

    public int getHealth() {
        return health;
    }

    public void damage(Player p) {
        health--;
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(health + "/" + maxHealth));
        if (health <= 0) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
            Bukkit.getPluginManager().callEvent(new TurretDestroyEvent(this));
        }
    }

    public void destroy() {
        shootingTask.cancel();
        block.getWorld().createExplosion(block, 2, false, false);
        for (Location loc : blocks) {
            loc.getBlock().setType(Material.AIR);
        }
        turrets.remove(this);
    }

    public Location getBlock() {
        return block;
    }

    public GameTeam getTeam() {
        return team;
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

                Location spawnLocation = block.clone().add(0.5, 0.5, 0.5);

                List<Location> targetLocations = new ArrayList<>();
                Location targetLocation = target.getEyeLocation().clone();
                targetLocations.add(targetLocation);
                targetLocations.add(targetLocation.clone().add(0, -1, 0));

                for (Location targetLoc : targetLocations) {
                    Vector direction = targetLoc.toVector().subtract(spawnLocation.toVector()).normalize();

                    spawnLocation.add(direction.multiply(0.8));

                    double distance = spawnLocation.distance(target.getEyeLocation());

//                    Bukkit.broadcastMessage(distance + "");

                    RayTraceResult result = spawnLocation.getWorld().rayTraceBlocks(spawnLocation, direction, distance);

                    if (target.getLocation().getY() > block.getY() ? distance < 2 : distance < 0.8) {
                        punchTarget();
                        attack = true;
                        break;
                    } else {
                        if (result == null || result.getHitBlock() == null) {
                            shootTarget(spawnLocation, direction);
                            attack = true;
                            break;
                        }
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
                if (distance < (double) DETECTION_RADIUS) {
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
