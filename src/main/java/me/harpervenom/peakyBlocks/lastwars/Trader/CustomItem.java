package me.harpervenom.peakyBlocks.lastwars.Trader;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

import static me.harpervenom.peakyBlocks.utils.Utils.createItem;

public class CustomItem implements Listener {

    public static final ItemStack seaReaper;
    public static final ItemStack soulCrasher;
    public static final ItemStack boltStorm;
    public static final ItemStack hellShot;

    public static final ItemStack speed;
    public static final ItemStack levitation;
    public static final ItemStack invisibility;
    public static final ItemStack slowFalling;

    static  {
        seaReaper = createItem(Material.TRIDENT, ChatColor.AQUA + "Морской Жнец", null);
        seaReaper.addUnsafeEnchantment(Enchantment.LOYALTY, 3);
        seaReaper.addUnsafeEnchantment(Enchantment.CHANNELING, 1);
        seaReaper.addUnsafeEnchantment(Enchantment.IMPALING, 2);

        soulCrasher = createItem(Material.MACE, ChatColor.LIGHT_PURPLE + "Крушитель Душ", null);
        soulCrasher.addUnsafeEnchantment(Enchantment.BREACH, 2);
        soulCrasher.addUnsafeEnchantment(Enchantment.DENSITY, 2);
        soulCrasher.addUnsafeEnchantment(Enchantment.WIND_BURST, 2);

        hellShot = createItem(Material.BOW, ChatColor.RED + "Адский Выстрел", null);
        hellShot.addUnsafeEnchantment(Enchantment.PUNCH, 2);
        hellShot.addUnsafeEnchantment(Enchantment.FLAME, 1);
        hellShot.addEnchantment(Enchantment.POWER, 1);

        boltStorm = createItem(Material.CROSSBOW, ChatColor.RED + "Гроза Болтов", null);
        boltStorm.addUnsafeEnchantment(Enchantment.MULTISHOT, 1);
        boltStorm.addUnsafeEnchantment(Enchantment.QUICK_CHARGE, 3);
        boltStorm.addEnchantment(Enchantment.PIERCING, 1);

        speed = new ItemStack(Material.POTION, 1);
        PotionMeta meta = (PotionMeta) speed.getItemMeta();
        if (meta != null) {
            meta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 3 * 60 * 20, 0), true);
            meta.setDisplayName(ChatColor.BLUE + "Скорость");
            speed.setItemMeta(meta);
        }

        levitation = new ItemStack(Material.POTION, 1);
        meta = (PotionMeta) levitation.getItemMeta();
        if (meta != null) {
            meta.addCustomEffect(new PotionEffect(PotionEffectType.LEVITATION, 23 * 20, 2), true);
            meta.setDisplayName(ChatColor.YELLOW + "Левитация");
            levitation.setItemMeta(meta);
        }

        invisibility = new ItemStack(Material.POTION, 1);
        meta = (PotionMeta) invisibility.getItemMeta();
        if (meta != null) {
            meta.addCustomEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 7 * 15 * 20, 1), true);
            meta.setDisplayName(ChatColor.WHITE + "Невидимость");
            invisibility.setItemMeta(meta);
        }

        slowFalling = new ItemStack(Material.POTION, 1);
        meta = (PotionMeta) slowFalling.getItemMeta();
        if (meta != null) {
            meta.addCustomEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 10 * 20, 0), true);
            meta.setDisplayName(ChatColor.GRAY + "Парение");
            slowFalling.setItemMeta(meta);
        }
    }


    //seaReaper
    HashMap<Entity, Player> shotTrident = new HashMap<>();

    @EventHandler
    public void onTridentShoot(ProjectileLaunchEvent e) {
        if (e.getEntity() instanceof Trident trident) {
            if (trident.getShooter() instanceof Player p) {
                shotTrident.put(trident, p);
            }
        }
    }

    @EventHandler
    public void onEntityMove(EntityRemoveEvent e) {
        if (e.getEntity() instanceof Trident trident) {
            if (e.getCause() != EntityRemoveEvent.Cause.OUT_OF_WORLD) return;

            ItemStack item = trident.getItem();

            Player p = shotTrident.get(trident);

            if (p == null) return;
            shotTrident.remove(trident);
            p.getInventory().addItem(item);
        }
    }


}
