package me.harpervenom.peakyBlocks.lastwars.LuckyBook.Loot;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
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

    public static List<PotionEffectType> drinkingPotionEffects = new ArrayList<>();
    public static List<PotionEffectType> splashPotionEffects = new ArrayList<>();
    public static List<PotionEffectType> arrowEffects = new ArrayList<>();

    public static Category blocksCategory;
    public static Category ingredientsCategory;
    public static Category utilitiesCategory;
    public static Category foodCategory;
    public static Category eggsCategory;

    static {
        blocksCategory = new Category(0.3);
        ingredientsCategory = new Category(1);
        utilitiesCategory = new Category(0.6);
        foodCategory = new Category(0.5);
        eggsCategory = new Category(0.2);

        for (Material material : Material.values()) {
            String name = material.name();
            if(name.contains("OAK") || name.contains("SPRUCE") || name.contains("BIRCH")
                    || name.contains("ACACIA") || name.contains("CHERRY") || name.contains("MANGROVE")
                    || name.contains("JUNGLE") || (name.contains("WARPED") && !name.contains("STICK")) || name.contains("CRIMSON")
                    || name.contains("BAMBOO") || name.contains("STONE") || name.contains("DEEPSLATE")
                    || name.contains("PRISMARINE") || name.contains("QUARTZ") || name.contains("COPPER")
                    || name.contains("WOOL") || name.contains("GLASS") || name.contains("CONCRETE")
                    || name.contains("TERRACOTTA") || name.contains("DIRT")
                    || name.contains("MUD") || name.contains("OBSIDIAN")) {

                if (material == Material.PRISMARINE_SHARD || material == Material.PRISMARINE_CRYSTALS || material == Material.MANGROVE_ROOTS
                        || material == Material.GLASS_BOTTLE || material == Material.REDSTONE || material == Material.SPYGLASS
                        || material == Material.REINFORCED_DEEPSLATE
                        || name.contains("BUTTON") || name.contains("PLATE") || name.contains("LOG")
                        || name.contains("PLANKS") || name.contains("WOOD") || name.contains("SAPLING")
                        || material.name().contains("STEM") || material.name().contains("HYPHAE") || material.name().contains("SIGN")
                        || name.contains("GRIND") || name.contains("CUTTER") || name.contains("ROOTS")
                        || name.contains("BOAT") || name.contains("GATE") || name.contains("PANE") || name.contains("DOOR") ||
                        !material.isBlock()) continue;

                double chance = 1;
                if (name.contains("COPPER")) chance = 0.05;

                blocksCategory.addItem(new ItemStack(material), chance, 16);
            }
        }
        blocksCategory.addItem(new ItemStack(Material.SAND), 1, 7);
        blocksCategory.addItem(new ItemStack(Material.GRAVEL), 1, 7);


        for (Material material : Material.values()) {
            String name = material.name();
            if (name.contains("PLANKS") || name.contains("LOG") || name.contains("WOOD ")
                    || name.contains("SAPLING") || name.contains("STEM") || name.contains("HYPHAE")) {
                int maxAmount = 3;
                if (name.contains("SAPLING")) maxAmount = 1;
                ingredientsCategory.addItem(new ItemStack(material), 0.08, maxAmount);
            }
        }
        ingredientsCategory.addItem(new ItemStack(Material.COBBLESTONE), 2, 3);
        ingredientsCategory.addItem(new ItemStack(Material.STONE), 2, 3);

        ingredientsCategory.addItem(new ItemStack(Material.GOLD_INGOT), 1, 3);
        ingredientsCategory.addItem(new ItemStack(Material.FEATHER), 1, 3);
        ingredientsCategory.addItem(new ItemStack(Material.PHANTOM_MEMBRANE), 1, 1);
        ingredientsCategory.addItem(new ItemStack(Material.FLINT), 1, 3);
        ingredientsCategory.addItem(new ItemStack(Material.BONE), 1, 4);
        ingredientsCategory.addItem(new ItemStack(Material.STRING), 1, 3);
        ingredientsCategory.addItem(new ItemStack(Material.SLIME_BALL), 1 ,3);
        ingredientsCategory.addItem(new ItemStack(Material.COAL), 1 ,3);

        ingredientsCategory.addItem(new ItemStack(Material.IRON_INGOT), 0.8, 5);
        ingredientsCategory.addItem(new ItemStack(Material.GUNPOWDER), 0.8, 3);
        ingredientsCategory.addItem(new ItemStack(Material.BREEZE_ROD), 0.8, 2);

        ingredientsCategory.addItem(new ItemStack(Material.DIAMOND), 0.5, 3);
        ingredientsCategory.addItem(new ItemStack(Material.NETHERITE_INGOT), 0.5, 1);

        ingredientsCategory.addItem(new ItemStack(Material.HEAVY_CORE), 0.2, 1);


        for (Material material : Material.values()) {
            String name = material.name();
            if (name.contains("HELMET") || name.contains("CHESTPLATE") || name.contains("LEGGINGS")
            || name.contains("BOOTS") || name.contains("SWORD") || name.contains("AXE ")
            || name.contains("SHOVEL") || name.contains("BOW ") || name.contains("ARROW") ||
            name.contains("SHULKER_BOX") || name.contains("POTION")) {

                double chance = 1;
                if (name.contains("NETHERITE")) chance = 0.3;
                if (name.contains("DIAMOND")) chance = 0.5;
                if (name.contains("IRON")) chance = 0.8;
                if (name.contains("ARROW")) chance = 1;
                if (material == Material.TIPPED_ARROW) chance = 5;
                if (name.contains("SHULKER_BOX")) chance = 0.1;
                if (name.contains("POTION")) chance = 2;
                if (name.contains("BOW")) chance = 3;

                int maxAmount = 1;
                if (name.contains("ARROW")) maxAmount = 5;

                utilitiesCategory.addItem(new ItemStack(material), chance, maxAmount);
            }
        }
        utilitiesCategory.addItem(new ItemStack(Material.WIND_CHARGE), 1 ,3);
        utilitiesCategory.addItem(new ItemStack(Material.ENDER_PEARL), 1 ,3);
        utilitiesCategory.addItem(new ItemStack(Material.FIREWORK_ROCKET), 1 ,3);
        utilitiesCategory.addItem(new ItemStack(Material.TNT), 1 ,2);
        utilitiesCategory.addItem(new ItemStack(Material.SHIELD), 1 ,1);
        utilitiesCategory.addItem(new ItemStack(Material.SADDLE), 1 ,1);
        utilitiesCategory.addItem(new ItemStack(Material.SPYGLASS), 0.5 ,1);
        utilitiesCategory.addItem(new ItemStack(Material.FIRE_CHARGE), 1 ,1);
//        utilitiesCategory.addItem(new ItemStack(Material.TOTEM_OF_UNDYING), 0.5 ,1);

        utilitiesCategory.addItem(new ItemStack(Material.TRIDENT), 0.5 ,1);
        utilitiesCategory.addItem(new ItemStack(Material.MACE), 0.5 ,1);
        utilitiesCategory.addItem(new ItemStack(Material.ELYTRA), 0.5,1);


        for (Material material : Material.values()) {
            if (material == Material.SPIDER_EYE || material == Material.OMINOUS_BOTTLE || material == Material.PUFFERFISH
            || material == Material.POISONOUS_POTATO) continue;
            if (material.isEdible()) {
                foodCategory.addItem(new ItemStack(material), 1, 4);
            }
            if (material == Material.ENCHANTED_GOLDEN_APPLE) {
                foodCategory.addItem(new ItemStack(material), 0.05, 1);
            }
        }


        double chance = 1;
        int maxAMount = 1;

        eggsCategory.addItem(new ItemStack(Material.ARMADILLO_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.CAMEL_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.CHICKEN_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.COW_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.DONKEY_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.GOAT_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.HORSE_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.LLAMA_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.MOOSHROOM_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.MULE_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.PANDA_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.PIG_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.POLAR_BEAR_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.SHEEP_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.SKELETON_HORSE_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.SNIFFER_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.SNOW_GOLEM_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.TURTLE_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.WOLF_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.ZOMBIE_HORSE_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.ZOMBIFIED_PIGLIN_SPAWN_EGG), chance, maxAMount);

        //ghast shalker iron golem

        chance = 0.6;
        eggsCategory.addItem(new ItemStack(Material.BLAZE_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.BOGGED_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.CREEPER_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.CAVE_SPIDER_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.ENDERMITE_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.DROWNED_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.GUARDIAN_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.HUSK_SPAWN_EGG), chance, maxAMount);
//        entitiesCategory.addItem(new ItemStack(Material.MAGMA_CUBE_SPAWN_EGG), chance, maxAMount);
//        entitiesCategory.addItem(new ItemStack(Material.PIGLIN_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.PILLAGER_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.PUFFERFISH_BUCKET), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.SHULKER_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.SILVERFISH_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.SKELETON_SPAWN_EGG), chance, maxAMount);
//        entitiesCategory.addItem(new ItemStack(Material.SLIME_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.SPIDER_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.STRAY_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.VEX_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.VINDICATOR_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.WITCH_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.WITHER_SKELETON_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.ZOMBIE_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.ZOMBIE_VILLAGER_SPAWN_EGG), chance, maxAMount);

        chance = 0.3;
//        entitiesCategory.addItem(new ItemStack(Material.HOGLIN_SPAWN_EGG), chance, maxAMount);
//        eggsCategory.addItem(new ItemStack(Material.ZOGLIN_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.BREEZE_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.ELDER_GUARDIAN_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.GHAST_SPAWN_EGG), chance, maxAMount);

        chance = 0.05;
        eggsCategory.addItem(new ItemStack(Material.IRON_GOLEM_SPAWN_EGG), chance, maxAMount);
//        entitiesCategory.addItem(new ItemStack(Material.PIGLIN_BRUTE_SPAWN_EGG), chance, maxAMount);
        eggsCategory.addItem(new ItemStack(Material.RAVAGER_SPAWN_EGG), chance, maxAMount);

        categories.add(blocksCategory);
        categories.add(ingredientsCategory);
        categories.add(utilitiesCategory);
        categories.add(foodCategory);
        categories.add(eggsCategory);


        //Potion Effects
        drinkingPotionEffects.add(PotionEffectType.HASTE);
        drinkingPotionEffects.add(PotionEffectType.RESISTANCE);
        drinkingPotionEffects.add(PotionEffectType.FIRE_RESISTANCE);
        drinkingPotionEffects.add(PotionEffectType.ABSORPTION);
        drinkingPotionEffects.add(PotionEffectType.HEALTH_BOOST);
        drinkingPotionEffects.add(PotionEffectType.INVISIBILITY);
        drinkingPotionEffects.add(PotionEffectType.INSTANT_HEALTH);
        drinkingPotionEffects.add(PotionEffectType.JUMP_BOOST);
        drinkingPotionEffects.add(PotionEffectType.LEVITATION);
        drinkingPotionEffects.add(PotionEffectType.REGENERATION);
        drinkingPotionEffects.add(PotionEffectType.SATURATION);
        drinkingPotionEffects.add(PotionEffectType.WATER_BREATHING);
        drinkingPotionEffects.add(PotionEffectType.SLOW_FALLING);
        drinkingPotionEffects.add(PotionEffectType.SPEED);
        drinkingPotionEffects.add(PotionEffectType.STRENGTH);

        arrowEffects.add(PotionEffectType.POISON);
        arrowEffects.add(PotionEffectType.INSTANT_DAMAGE);
        arrowEffects.add(PotionEffectType.MINING_FATIGUE);
        arrowEffects.add(PotionEffectType.BLINDNESS);
        arrowEffects.add(PotionEffectType.GLOWING);
        arrowEffects.add(PotionEffectType.DARKNESS);
        arrowEffects.add(PotionEffectType.HUNGER);
        arrowEffects.add(PotionEffectType.INFESTED);
        arrowEffects.add(PotionEffectType.NAUSEA);
        arrowEffects.add(PotionEffectType.OOZING);
        arrowEffects.add(PotionEffectType.SLOWNESS);
        arrowEffects.add(PotionEffectType.WEAKNESS);
        arrowEffects.add(PotionEffectType.WEAVING);
        arrowEffects.add(PotionEffectType.WITHER);
        arrowEffects.add(PotionEffectType.WIND_CHARGED);

        splashPotionEffects.addAll(drinkingPotionEffects);
        splashPotionEffects.addAll(arrowEffects);
    }

    public static ItemStack getLootItemStack(Category category) {
        if (category == null) {
            category = pickRandomCategory();
        }
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
        List<PotionEffectType> potionEffectTypes = new ArrayList<>();

        if (item.getType() == Material.POTION) {
            potionEffectTypes = drinkingPotionEffects;
        } else if (item.getType() == Material.SPLASH_POTION || item.getType() == Material.LINGERING_POTION) {
            potionEffectTypes = splashPotionEffects;
        } else {
            return;
        }

        PotionMeta potionMeta = (PotionMeta) item.getItemMeta();

        if (potionMeta != null) {
            PotionEffectType randomEffectType = potionEffectTypes.get(random.nextInt(potionEffectTypes.size()));

            int duration = (random.nextInt(31) + 10) * 20 * getEffectMultiplier(randomEffectType); // Random duration between 10 and 40 seconds (20 ticks = 1 second)
            int amplifier = random.nextInt(3); // Random amplifier between 0 and 2

            PotionEffect randomEffect = new PotionEffect(randomEffectType, duration, amplifier);
            potionMeta.addCustomEffect(randomEffect, true);

            item.setItemMeta(potionMeta);
        }
    }

    public static void applyRandomFireworkEffect(ItemStack item) {
        if (item.getType() == Material.FIREWORK_ROCKET) {
            FireworkMeta fireworkMeta = (FireworkMeta) item.getItemMeta();

            if (fireworkMeta != null) {
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

                fireworkMeta.addEffect(effect);

                fireworkMeta.setPower(random.nextInt(3) + 1);

                item.setItemMeta(fireworkMeta);
            }
        }
    }

    public static void applyRandomTippedArrowEffect(ItemStack item) {
        List<PotionEffectType> potionEffectTypes = arrowEffects;

        if (item.getType() == Material.TIPPED_ARROW) {
            PotionMeta potionMeta = (PotionMeta) item.getItemMeta();

            if (potionMeta != null) {
                PotionEffectType randomEffectType = potionEffectTypes.get(random.nextInt(potionEffectTypes.size()));

                int duration = (random.nextInt(21) + 10) * 20 * 4; // Random duration between 10 and 30 seconds (20 ticks = 1 second)
                int amplifier = random.nextInt(2); // Random amplifier between 0 and 1 (Level I to II)

                PotionEffect randomEffect = new PotionEffect(randomEffectType, duration, amplifier);
                potionMeta.addCustomEffect(randomEffect, true);

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

        if (!availableEnchantments.isEmpty()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                int numEnchantments = random.nextInt(3) + 1; // Random number of enchantments (1 to 3)

                for (int i = 0; i < numEnchantments; i++) {
                    Enchantment enchantment = availableEnchantments.get(random.nextInt(availableEnchantments.size()));

                    int maxLevel = enchantment.getMaxLevel();

                    int level = random.nextInt(maxLevel) + 1; // Random level from 1 to maxLevel

                    meta.addEnchant(enchantment, level, true);
                }

                item.setItemMeta(meta);
            }
        }
    }

    public static int getEffectMultiplier(PotionEffectType effect) {
        if (effect == PotionEffectType.INVISIBILITY || effect == PotionEffectType.HEALTH_BOOST
                || effect == PotionEffectType.ABSORPTION) return 3;
        if (effect == PotionEffectType.SPEED || effect == PotionEffectType.JUMP_BOOST) return 2;
        return 1;
    }
}
