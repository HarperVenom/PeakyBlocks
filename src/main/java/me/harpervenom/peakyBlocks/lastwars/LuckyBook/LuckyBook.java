package me.harpervenom.peakyBlocks.lastwars.LuckyBook;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static me.harpervenom.peakyBlocks.lastwars.Items.lootItems;
import static me.harpervenom.peakyBlocks.lastwars.Trader.TraderListener.goodsPrices;

public class LuckyBook {

    public static ItemStack luckyBook;
    public static final String luckyBookName = ChatColor.YELLOW + "ЛакиБук";

    static {
        luckyBook = new ItemStack(Material.BOOK);
        ItemMeta meta = luckyBook.getItemMeta();
        if (meta != null) meta.setDisplayName(luckyBookName);
        luckyBook.setItemMeta(meta);

        goodsPrices.put(luckyBookName, 10);
    }

    public static void giveLootToPlayer(Player p) {
        List<ItemStack> loot = generateLoot();
        for (ItemStack item : loot) {
            HashMap<Integer, ItemStack> remaining = p.getInventory().addItem(item);

            for (ItemStack droppedItem : remaining.values()) {
                p.getWorld().dropItemNaturally(p.getLocation().clone().add(-0.5, 0, -0.5), droppedItem);
            }
        }
    }

    public static List<ItemStack> generateLoot() {
        List<ItemStack> items = new ArrayList<>();

        Random random = new Random();

        for (int i = 0; i < 2; i++) {
            ItemStack item = lootItems.get(random.nextInt(lootItems.size()-1));

//            applyRandomAmount(item);
            applyRandomDurability(item);
            applyRandomArmorCustomization(item);

            items.add(item);
        }

        return items;
    }

    private static void applyRandomAmount(ItemStack itemStack) {
        if (itemStack.getMaxStackSize() > 1) {
            Random rand = new Random();
            int maxAmount = 3;
            int randomAmount = rand.nextInt(maxAmount) + 1;
            itemStack.setAmount(randomAmount);
        }
    }

    private static void applyRandomDurability(ItemStack item) {
        Random random = new Random();
        if (item != null && item.getItemMeta() instanceof Damageable) {
            ItemMeta meta = item.getItemMeta();
            Damageable damageable = (Damageable) meta;

            int maxDurability = item.getType().getMaxDurability();
            if (maxDurability > 0) {

                int randomDurability = random.nextInt(maxDurability);

                damageable.setDamage(maxDurability - randomDurability);
                item.setItemMeta(meta);
            }
        }
    }

    public static void applyRandomArmorCustomization(ItemStack item) {
        if (item == null) return;

        if (item.getItemMeta() instanceof ArmorMeta meta) {

            TrimPattern[] patterns = new TrimPattern[]{TrimPattern.BOLT, TrimPattern.EYE, TrimPattern.COAST,
                    TrimPattern.RIB, TrimPattern.DUNE, TrimPattern.FLOW, TrimPattern.HOST, TrimPattern.RAISER,
                    TrimPattern.SENTRY, TrimPattern.SHAPER, TrimPattern.SILENCE, TrimPattern.SNOUT, TrimPattern.SPIRE,
                    TrimPattern.TIDE, TrimPattern.VEX, TrimPattern.WARD, TrimPattern.WAYFINDER, TrimPattern.WILD};

            TrimMaterial[] materials = new TrimMaterial[]{TrimMaterial.AMETHYST, TrimMaterial.COPPER, TrimMaterial.DIAMOND,
                    TrimMaterial.GOLD, TrimMaterial.EMERALD, TrimMaterial.IRON, TrimMaterial.LAPIS, TrimMaterial.NETHERITE,
                    TrimMaterial.QUARTZ, TrimMaterial.REDSTONE};

            Random random = new Random();

            TrimPattern randomPattern = patterns[random.nextInt(patterns.length)];
            TrimMaterial randomMaterial = materials[random.nextInt(materials.length)];
            ArmorTrim trim = new ArmorTrim(randomMaterial, randomPattern);
            meta.setTrim(trim);

            if (meta instanceof LeatherArmorMeta leatherMeta) {

                Color randomColor = Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256));
                leatherMeta.setColor(randomColor);

                item.setItemMeta(leatherMeta);
            } else {
                item.setItemMeta(meta);
            }
        }
    }

}
