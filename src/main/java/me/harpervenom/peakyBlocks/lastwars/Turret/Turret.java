package me.harpervenom.peakyBlocks.lastwars.Turret;

import me.harpervenom.peakyBlocks.lastwars.Game;
import me.harpervenom.peakyBlocks.lastwars.GamePlayer;
import me.harpervenom.peakyBlocks.lastwars.GameTeam;
import me.harpervenom.peakyBlocks.lastwars.Map.Map;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.*;

import static me.harpervenom.peakyBlocks.PeakyBlocks.getPlugin;
import static me.harpervenom.peakyBlocks.lastwars.GameListener.*;
import static me.harpervenom.peakyBlocks.lastwars.GameTeam.getEntityTeam;
import static me.harpervenom.peakyBlocks.lastwars.GameTeam.inSameTeam;
import static me.harpervenom.peakyBlocks.lastwars.Turret.TurretListener.attackers;
import static me.harpervenom.peakyBlocks.lastwars.Turret.TurretListener.destroyedTurrets;
import static me.harpervenom.peakyBlocks.utils.Utils.isValidUUID;

public class Turret {

    public static List<Turret> turrets = new ArrayList<>();

    public static Turret getTurret(Entity entity) {
        for (Turret turret : turrets) {
            if (turret.getShooter().getUniqueId().equals(entity.getUniqueId())) {
                return turret;
            }
        }
        return null;
    }

    private final Location baseLoc;
    private Location loc;
    private GameTeam team;
    private static int maxHealth = 300;
    private int health;
    private boolean isBreakable;
    private String name;

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

    private Slime shooter;
    private List<LivingEntity> targets;
    private LivingEntity priorityTarget;

    private List<Location> blocks = new ArrayList<>();

    public Turret(Location baseLoc, boolean isBreakable) {
        this.baseLoc = baseLoc;
        this.isBreakable = isBreakable;

        if (!isBreakable) {
            minShootingInterval = 10;
            initialShootingInterval = 10;
            shootingInterval = 10;
            arrowSpeed = 3F;
            detectionRadius = 12;
        }

        this.health = maxHealth;

        isRunning = false;
    }

    public Turret(Turret sample) {
        this.baseLoc = sample.baseLoc.clone();
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
        Location location = new Location(baseLoc.getWorld(), baseLoc.getX(), baseLoc.getY() + 1, baseLoc.getZ());
        blocks.add(location);
        location.getBlock().setType(isBreakable ? Material.SMOOTH_STONE : Material.BEDROCK);

        location = new Location(baseLoc.getWorld(), baseLoc.getX(), baseLoc.getY() + 2, baseLoc.getZ());

        if (!isBreakable) {
            location.getBlock().setType(Material.BEDROCK);
            blocks.add(location);

            location = new Location(baseLoc.getWorld(), baseLoc.getX(), baseLoc.getY() + 3, baseLoc.getZ());
        }
        loc = location;

        Map map = team.getGame().getMap();
        blocks.forEach(map::addLoc);

        name = team.getColor() + "[Турель]";

        shooter = (Slime) location.getWorld().spawnEntity(location.clone().add(0.5, 0, 0.5), EntityType.SLIME);
        shooter.setSize(2);
        shooter.setCustomName(name + (isBreakable ?  " " + maxHealth + "/" + maxHealth : ""));
        shooter.setAI(false);
        shooter.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        shooter.setHealth(health);
        shooter.setSilent(true);
        shooter.setRotation(baseLoc.getYaw(), baseLoc.getPitch());
        shooter.setRemoveWhenFarAway(false);
        if (!isBreakable) {
            shooter.setInvulnerable(true);
        }
        team.getTeam().addEntry(shooter.getUniqueId().toString());

        startScanningTask();
    }

    public double getHealth() {
        return shooter.getHealth();
    }

    public static List<Turret> getTurrets() {
        return turrets;
    }

    public List<Location> getBlocks() {
        return blocks;
    }

    public String getName() {
        return name;
    }

    public boolean isBreakable() {
        return isBreakable;
    }

    public boolean damage(Entity attacker, double damage) {
        if (inSameTeam(shooter, attacker)) return false;

        Player p = null;
        if (attacker instanceof Player player) {
            p = player;
        }

        if (!isRunning) {
            if (attacker instanceof LivingEntity livingAttacker && targets.contains(livingAttacker)) {
                Location loc = shooter.getLocation();
                turretExplosions.get(loc.getWorld()).add(loc);
                noDamageExplosions.get(loc.getWorld()).add(loc);
                loc.getWorld().createExplosion(loc, 6);

                int backFireDamage = 7;

                livingAttacker.setHealth(livingAttacker.getHealth() < backFireDamage ? 0 : livingAttacker.getHealth() - backFireDamage);
                if (p != null) p.playSound(p, Sound.ENTITY_PLAYER_HURT, 1, 1);

                scanArea();
            } else {
                return false;
            }
        }

        double newHealth = getHealth() - damage;
        newHealth = Math.round(newHealth * 10.0) / 10.0;
        if (p != null) p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.YELLOW + "" + newHealth + "/" + maxHealth));
        shooter.setCustomName(name + " " + newHealth + "/" + maxHealth);

        if (newHealth <= 0) {
            if (p != null) p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
            Bukkit.getPluginManager().callEvent(new TurretDestroyEvent(this));
        }

        for (GamePlayer gp : getTeam().getPlayers()) {
            Player player = gp.getPlayer();
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_HURT, 0.5f, 1);
        }
        shooter.getLocation().getWorld().playSound(shooter.getLocation(), Sound.ENTITY_ENDER_DRAGON_HURT, 1, 1);
        return true;
    }

    public void destroy() {
        shootingTask.cancel();
        scanningTask.cancel();
        noDamageExplosions.get(baseLoc.getWorld()).add(shooter.getLocation());
        shooter.getWorld().createExplosion(shooter.getLocation(), 2, false, false);

        for (Location loc : blocks) {
            loc.getBlock().setType(Material.AIR);
        }
        destroyedTurrets.add(this);

        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            turrets.remove(this);
        }, 5);
    }

    public Location getBaseLoc() {
        return baseLoc;
    }
    public Location getLoc() {
        return loc;
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

                Location spawnLocation = shooter.getLocation().clone().add(0, 0.5, 0);
                for (LivingEntity target : targets) {
                    boolean attack = false;
                    if (target == null || target.isDead()) continue;

                    List<Location> targetLocations = new ArrayList<>();
                    Location targetLocation = target.getEyeLocation().clone();
                    targetLocations.add(targetLocation);
                    targetLocations.add(targetLocation.clone().add(0, -1, 0));

                    for (Location targetLoc : targetLocations) {

                        Vector direction = targetLoc.toVector().subtract(spawnLocation.toVector()).normalize();
                        double distance = spawnLocation.distance(target.getEyeLocation());
                        RayTraceResult result = spawnLocation.getWorld().rayTraceBlocks(spawnLocation.clone().subtract(direction.multiply(0.2)),
                                direction, distance);

                        if (result != null && priorityTarget instanceof Player && distance < 6) {
                            Location loc = shooter.getLocation();
                            turretExplosions.get(loc.getWorld()).add(loc);
                            noDamageExplosions.get(loc.getWorld()).add(loc);
                            loc.getWorld().createExplosion(loc, 6);
                        }

                        if (result == null || result.getHitBlock() == null) {
                            if (target.getLocation().clone().add(0, 0.7, 0).distance(spawnLocation) < (isBreakable ? 0.8 : 4)) {
                                punchTarget(target);
                            } else {
                                shootTarget(direction);
                            }
                            attack = true;
                            priorityTarget = target;
                            break;
                        }
                    }

                    if (attack) {
                        isRunning = true;
                        this.cancel();
                        if (shootingInterval > minShootingInterval) shootingInterval -= intervalDecrement;
                        if (shootingInterval < minShootingInterval) shootingInterval = minShootingInterval;
                        startShootingTask();
                        return;
                    }
                }

                isRunning = false;
                shootingInterval = initialShootingInterval;
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

        scanningTask.runTaskTimer(getPlugin(), 0, 20);
    }

    public void scanArea() {
        targets = getEntitiesInRadius();

        if (!isRunning) startShootingTask();
    }

    public List<LivingEntity> getEntitiesInRadius() {
        List<LivingEntity> entitiesInRadius = new ArrayList<>();
        Game game = team.getGame();

        for (GameTeam team : game.getTeams()) {
            if (team.equals(this.team)) continue;

            for (String entityId : team.getTeam().getEntries()) {
                if (!isValidUUID(entityId)) continue;
                LivingEntity entity = (LivingEntity) Bukkit.getEntity(UUID.fromString(entityId));
                if (entity == null || entity.isDead()) continue;
                double distance = entity.getLocation().distance(shooter.getLocation());
                if (distance > (double) detectionRadius) continue;
                entitiesInRadius.add(entity);
            }

            for (GamePlayer gp : team.getPlayers()) {
                LivingEntity entity = gp.getPlayer();
                double distance = entity.getLocation().distance(shooter.getLocation());
                if (distance > (double) detectionRadius) continue;
                entitiesInRadius.add(entity);
            }
        }

        if (priorityTarget == null || !entitiesInRadius.contains(priorityTarget)
                || !(priorityTarget instanceof Player)) {

            if (priorityTarget instanceof Player) priorityTarget = null;

            boolean selected = false;

            Iterator<HashMap.Entry<UUID, UUID>> iterator = attackers.entrySet().iterator();
            while (iterator.hasNext()) {
                HashMap.Entry<UUID, UUID> entry = iterator.next();
                Player attacker = Bukkit.getPlayer(entry.getKey());
                Player target = Bukkit.getPlayer(entry.getValue());

                if (attacker == null || target == null) continue;

                if (team.equals(getEntityTeam(attacker))) continue;

                if (selected) {
                    iterator.remove(); // Safe removal
                }

                if (entitiesInRadius.contains(attacker)) {
                    priorityTarget = attacker;
                    iterator.remove(); // Safe removal
                    selected = true;
                }
            }
        }

        entitiesInRadius.remove(priorityTarget);

        entitiesInRadius.sort((e1, e2) -> {
            if (e1 instanceof Player && !(e2 instanceof Player)) {
                return 1; // Move e1 (player) after e2 (entity)
            } else if (!(e1 instanceof Player) && e2 instanceof Player) {
                return -1; // Move e1 (entity) before e2 (player)
            }
            return 0; // If both are players or both are entities, maintain their order
        });

        if (priorityTarget != null) entitiesInRadius.addFirst(priorityTarget);

        return entitiesInRadius;
    }

    private void shootTarget(Vector direction) {
        Location shooterLocation = shooter.getLocation().add(0, shooter.getHeight() * 0.5, 0);

        Arrow arrow = shooter.getWorld().spawnArrow(shooterLocation, direction, arrowSpeed, 0);
        arrow.setShooter(shooter);
        arrow.setGravity(false);

        arrow.getWorld().spawnParticle(Particle.SMOKE, arrow.getLocation(), 20, 0.5, 0.2, 0.2, 0.02);
        arrow.getWorld().playSound(arrow.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1F, (float) (1 + 0.1 * (30.0 / shootingInterval)));

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

    public void punchTarget(LivingEntity target) {
        target.damage(1);

        Vector direction = target.getEyeLocation().toVector().subtract(shooter.getLocation().toVector()).normalize();

        direction = direction.multiply(1.5);

        target.setVelocity(direction);

        shooter.getLocation().getWorld()
                .spawnParticle(Particle.FLAME, shooter.getLocation(), 20, 0.5, 0.5, 0.5, 0);
        shooter.getLocation().getWorld().playSound(shooter.getLocation(), Sound.ENTITY_BLAZE_HURT, 1, 0.8f);
    }

    public Slime getShooter() {
        return shooter;
    }
}
