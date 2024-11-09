package me.harpervenom.peakyBlocks.lastwars.LuckyBook.Loot;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class Loot {

    static Random random = new Random();
    public static List<Category> categories = new ArrayList<>();

    static {
        Category decorativeBlocksCategory = new Category(2);
        Category usefulResourcesCategory = new Category(3);
        Category utilitiesCategory = new Category(2);
        Category consumingCategory = new Category(2);
        Category entitiesCategory = new Category(0.5);

        for (Material material : Material.values()) {
            if(material.name().contains("OAK") || material.name().contains("SPRUCE") || material.name().contains("BIRCH")
                    || material.name().contains("ACACIA") || material.name().contains("CHERRY") || material.name().contains("MANGROVE")
                    || material.name().contains("JUNGLE") || material.name().contains("WARPED") || material.name().contains("CRIMSON")
                    || material.name().contains("BAMBOO") || material.name().contains("STONE") || material.name().contains("DEEPSLATE")
                    || material.name().contains("PRISMARINE") || material.name().contains("QUARTZ") || material.name().contains("COPPER")
                    || material.name().contains("WOOL") || material.name().contains("GLASS") || material.name().contains("CONCRETE")
                    || material.name().contains("TERRACOTTA") || material.name().contains("SHULKER") || material.name().contains("DIRT")) {
                decorativeBlocksCategory.addItem(new ItemStack(material), 10, 3);
            }
        }
        decorativeBlocksCategory.addItem(new ItemStack(Material.SAND), 10, 3);
        decorativeBlocksCategory.addItem(new ItemStack(Material.GRAVEL), 10, 3);


        for (Material material : Material.values()) {
            if (material.name().contains("PLANKS") || material.name().contains("LOG")) {
                usefulResourcesCategory.addItem(new ItemStack(material), 0.5, 3);
            }
        }
        usefulResourcesCategory.addItem(new ItemStack(Material.STONE), 3, 3);
        usefulResourcesCategory.addItem(new ItemStack(Material.COBBLESTONE), 3, 3);
        usefulResourcesCategory.addItem(new ItemStack(Material.LAPIS_LAZULI), 3, 3);
        usefulResourcesCategory.addItem(new ItemStack(Material.GOLD_INGOT), 3, 3);
        usefulResourcesCategory.addItem(new ItemStack(Material.GOLD_NUGGET), 3, 3);
        usefulResourcesCategory.addItem(new ItemStack(Material.FEATHER), 3, 3);
        usefulResourcesCategory.addItem(new ItemStack(Material.FLINT), 3, 1);

        usefulResourcesCategory.addItem(new ItemStack(Material.IRON_INGOT), 2, 3);
        usefulResourcesCategory.addItem(new ItemStack(Material.IRON_NUGGET), 2, 3);
        usefulResourcesCategory.addItem(new ItemStack(Material.STRING), 2, 3);

        usefulResourcesCategory.addItem(new ItemStack(Material.DIAMOND), 1, 3);
        usefulResourcesCategory.addItem(new ItemStack(Material.NETHERITE_INGOT), 1, 3);
        usefulResourcesCategory.addItem(new ItemStack(Material.NETHERITE_SCRAP), 1, 3);
        usefulResourcesCategory.addItem(new ItemStack(Material.BREEZE_ROD), 1, 1);
        usefulResourcesCategory.addItem(new ItemStack(Material.HEAVY_CORE), 1, 1);


        for (Material material : Material.values()) {
            if (material.name().contains("HELMET") || material.name().contains("CHESTPLATE") || material.name().contains("LEGGINGS")
            || material.name().contains("BOOTS") || material.name().contains("SWORD") || material.name().contains("AXE")
            || material.name().contains("SHOVEL") || material.name().contains("BOW") || material.name().contains("ARROW")) {
                double chance = 1;
                if (material.name().contains("NETHERITE")) chance -= 0.5;
                if (material.name().contains("DIAMOND")) chance -= 0.4;
                if (material.name().contains("IRON")) chance -= 0.3;
                if (material.name().contains("ARROW")) chance = 2;

                utilitiesCategory.addItem(new ItemStack(material), chance ,1);
            }
        }
        utilitiesCategory.addItem(new ItemStack(Material.WIND_CHARGE), 1 ,3);
        utilitiesCategory.addItem(new ItemStack(Material.ENDER_PEARL), 1 ,3);
        utilitiesCategory.addItem(new ItemStack(Material.FIREWORK_ROCKET), 1 ,3);
        utilitiesCategory.addItem(new ItemStack(Material.TNT), 1 ,2);
        utilitiesCategory.addItem(new ItemStack(Material.SHIELD), 1 ,2);
//        utilitiesCategory.addItem(new ItemStack(Material.TOTEM_OF_UNDYING), 0.5 ,1);

        utilitiesCategory.addItem(new ItemStack(Material.TRIDENT), 0.5 ,1);
        utilitiesCategory.addItem(new ItemStack(Material.MACE), 0.5 ,1);
        utilitiesCategory.addItem(new ItemStack(Material.ELYTRA), 0.5,1);


        for (Material material : Material.values()) {
            if (material.isEdible()) {
                consumingCategory.addItem(new ItemStack(material), 1, 3);
            }
            if (material.name().contains("POTION")) {
                consumingCategory.addItem(new ItemStack(material), 1, 1);
            }
        }


        for (Material material : Material.values()) {
            if (material.name().contains("SPAWN_EGG") && material != Material.ENDER_DRAGON_SPAWN_EGG){
                entitiesCategory.addItem(new ItemStack(material), 1, 1);
            }
        }
        entitiesCategory.addItem(new ItemStack(Material.WARDEN_SPAWN_EGG), 0.1, 1);
        entitiesCategory.addItem(new ItemStack(Material.WITHER_SPAWN_EGG), 0.1, 1);


        categories.add(decorativeBlocksCategory);
        categories.add(usefulResourcesCategory);
        categories.add(utilitiesCategory);
        categories.add(consumingCategory);
        categories.add(entitiesCategory);
    }

    public static ItemStack getLootItemStack() {
        Category category = pickRandomCategory();
        if (category == null) return null;

        ItemStack item = category.pickRandomItem();

        randomizeItem(item);

        return item;
    }

    private static Category pickRandomCategory() {
        double totalChance = categories.stream().mapToDouble(Category::getChance).sum();
        double randomValue = random.nextDouble() * totalChance;

        double cumulativeChance = 0.0;
        for (Category category : categories) {
            cumulativeChance += category.getChance();
            if (randomValue <= cumulativeChance) {
                return category;
            }
        }
        return null;
    }

    private static void randomizeItem(ItemStack item) {
        applyRandomDurability(item);
        applyRandomFireworkEffect(item);
        applyRandomPotionEffect(item);
        applyRandomTippedArrowEffect(item);
        applyRandomArmorCustomization(item);
        applyRandomEnchantments(item);
    }

    private static void applyRandomDurability(ItemStack item) {
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

    public static void applyRandomPotionEffect(ItemStack item) {
        List<PotionEffectType> potionEffectTypes = List.of(PotionEffectType.values());

        if (item.getType() == Material.POTION || item.getType() == Material.SPLASH_POTION || item.getType() == Material.LINGERING_POTION) {
            PotionMeta potionMeta = (PotionMeta) item.getItemMeta();

            if (potionMeta != null) {
                PotionEffectType randomEffectType = potionEffectTypes.get(random.nextInt(potionEffectTypes.size()));

                int duration = (random.nextInt(31) + 10) * 20; // Random duration between 10 and 40 seconds (20 ticks = 1 second)
                int amplifier = random.nextInt(3); // Random amplifier between 0 and 2

                PotionEffect randomEffect = new PotionEffect(randomEffectType, duration, amplifier);
                potionMeta.addCustomEffect(randomEffect, true);

                item.setItemMeta(potionMeta);
            }
        }
    }

    public static void applyRandomFireworkEffect(ItemStack item) {
        // Check if the item is a firework rocket
        if (item.getType() == Material.FIREWORK_ROCKET) {
            FireworkMeta fireworkMeta = (FireworkMeta) item.getItemMeta();

            if (fireworkMeta != null) {
                // Generate a random firework effect
                FireworkEffect.Type type = FireworkEffect.Type.values()[random.nextInt(FireworkEffect.Type.values().length)];

                Color color1 = Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256));
                Color color2 = Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256));

                FireworkEffect effect = FireworkEffect.builder()
                        .with(type)
                        .withColor(color1)
                        .withFade(color2)
                        .flicker(random.nextBoolean())
                        .trail(random.nextBoolean())
                        .build();

                // Add the random effect to the firework
                fireworkMeta.addEffect(effect);

                // Set a random power level between 1 and 3
                fireworkMeta.setPower(random.nextInt(3) + 1);

                // Apply the modified meta back to the item
                item.setItemMeta(fireworkMeta);
            }
        }
    }

    public static void applyRandomTippedArrowEffect(ItemStack item) {
        // List of all potion effect types
        List<PotionEffectType> potionEffectTypes = List.of(PotionEffectType.values());

        // Check if the item is a tipped arrow
        if (item.getType() == Material.TIPPED_ARROW) {
            PotionMeta potionMeta = (PotionMeta) item.getItemMeta();

            if (potionMeta != null) {
                // Get a random potion effect type
                PotionEffectType randomEffectType = potionEffectTypes.get(random.nextInt(potionEffectTypes.size()));

                // Randomize duration and amplifier
                int duration = (random.nextInt(21) + 10) * 20; // Random duration between 10 and 30 seconds (20 ticks = 1 second)
                int amplifier = random.nextInt(2); // Random amplifier between 0 and 1 (Level I to II)

                // Add the random potion effect to the PotionMeta
                PotionEffect randomEffect = new PotionEffect(randomEffectType, duration, amplifier);
                potionMeta.addCustomEffect(randomEffect, true);

                // Apply the modified meta back to the item
                item.setItemMeta(potionMeta);
            }
        }
    }

    public static void applyRandomEnchantments(ItemStack item) {
        if (random.nextInt(100) > 60) return;

        List<Enchantment> availableEnchantments = new ArrayList<>();

        if (item != null && item.getType() != Material.AIR) {

            String name = item.getType().name();
            if (!(name.contains("HELMET") || name.contains("CHESTPLATE") || name.contains("LEGGINGS") || name.contains("BOOTS")
                    || name.contains(" AXE") || name.contains("PICKAXE") || name.contains("SHOVEL") || name.contains("SWORD")
                    || name.contains("TRIDENT") || name.contains("MACE") || name.contains("SHIELD") || name.contains("BOW")
                    || name.contains("ELYTRA"))) return;


            availableEnchantments.add(Enchantment.UNBREAKING);
            availableEnchantments.add(Enchantment.VANISHING_CURSE);

            if (name.contains("HELMET") || name.contains("CHESTPLATE") || name.contains("LEGGINGS") || name.contains("BOOTS")) {
                availableEnchantments.add(Enchantment.PROTECTION);
                availableEnchantments.add(Enchantment.PROJECTILE_PROTECTION);
                availableEnchantments.add(Enchantment.FIRE_PROTECTION);
                availableEnchantments.add(Enchantment.BLAST_PROTECTION);
                availableEnchantments.add(Enchantment.BINDING_CURSE);
                availableEnchantments.add(Enchantment.THORNS);

                if (name.contains("HELMET")) {
                    availableEnchantments.add(Enchantment.AQUA_AFFINITY);
                    availableEnchantments.add(Enchantment.RESPIRATION);
                    availableEnchantments.add(Enchantment.THORNS);
                }

                if (name.contains("BOOTS")) {
                    availableEnchantments.add(Enchantment.FEATHER_FALLING);
                    availableEnchantments.add(Enchantment.SWIFT_SNEAK);
                    availableEnchantments.add(Enchantment.FROST_WALKER);
                    availableEnchantments.add(Enchantment.DEPTH_STRIDER);
                    availableEnchantments.add(Enchantment.SOUL_SPEED);
                }
            }

            if(name.contains("AXE") || name.contains("SHOVEL")) {
                availableEnchantments.add(Enchantment.EFFICIENCY);
                availableEnchantments.add(Enchantment.FORTUNE);
                availableEnchantments.add(Enchantment.SILK_TOUCH);
            }

            if (name.contains(" AXE") || name.contains("SWORD")) {
                availableEnchantments.add(Enchantment.SHARPNESS);
                availableEnchantments.add(Enchantment.KNOCKBACK);
                availableEnchantments.add(Enchantment.FIRE_ASPECT);

                if (name.contains("SWORD")) {
                    availableEnchantments.add(Enchantment.SWEEPING_EDGE);
                    availableEnchantments.add(Enchantment.SMITE);
                    availableEnchantments.add(Enchantment.BANE_OF_ARTHROPODS);
                }
            }

            if (name.equals("MACE")) {
                availableEnchantments.add(Enchantment.DENSITY);
                availableEnchantments.add(Enchantment.WIND_BURST);
                availableEnchantments.add(Enchantment.BREACH);
            }

            if (name.equals("TRIDENT")) {
                availableEnchantments.add(Enchantment.CHANNELING);
                availableEnchantments.add(Enchantment.IMPALING);
                availableEnchantments.add(Enchantment.LOYALTY);
                availableEnchantments.add(Enchantment.RIPTIDE);
            }

            if (name.equals("BOW")) {
                availableEnchantments.add(Enchantment.FLAME);
                availableEnchantments.add(Enchantment.INFINITY);
                availableEnchantments.add(Enchantment.FIRE_ASPECT);
                availableEnchantments.add(Enchantment.PUNCH);
                availableEnchantments.add(Enchantment.POWER);
            }

            if (name.equals("CROSSBOW")) {
                availableEnchantments.add(Enchantment.QUICK_CHARGE);
                availableEnchantments.add(Enchantment.PIERCING);
                availableEnchantments.add(Enchantment.MULTISHOT);
            }
        }

        // If the item can be enchanted
        if (!availableEnchantments.isEmpty()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                int numEnchantments = random.nextInt(3) + 1; // Random number of enchantments (1 to 3)

                // Apply random enchantments
                for (int i = 0; i < numEnchantments; i++) {
                    Enchantment enchantment = availableEnchantments.get(random.nextInt(availableEnchantments.size()));

                    // Get the maximum level of the enchantment
                    int maxLevel = enchantment.getMaxLevel();

                    // Generate a random level within the valid range (1 to maxLevel)
                    int level = random.nextInt(maxLevel) + 1; // Random level from 1 to maxLevel

                    // Apply the enchantment
                    meta.addEnchant(enchantment, level, true);
                }

                item.setItemMeta(meta);
            }
        }
    }
}
