package me.harpervenom.peakyBlocks.lastwars.Trader;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Nameable;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static me.harpervenom.peakyBlocks.lastwars.Trader.CustomItem.*;

public class Trader{

    public static List<Trader> traders = new ArrayList<>();
    public static Trader getTrader(UUID id) {
        for (Trader trader : traders) {
            if (trader.getEntity().getUniqueId().equals(id)) {
                return trader;
            }
        }
        return null;
    }

    private static final Merchant blocksShop;
    private static final Merchant materialsShop;
    private static final Merchant foodShop;
    private static final Merchant creaturesShop;
    private static final Merchant equipmentShop;
    static {
        List<MerchantRecipe> trades = new ArrayList<>();

        blocksShop = Bukkit.createMerchant();
        trades.add(createMerchantRecipe(new ItemStack(Material.AZALEA_LEAVES, 4), new ItemStack(Material.BRICK, 1)));
        trades.add(createMerchantRecipe(new ItemStack(Material.SANDSTONE, 4), new ItemStack(Material.BRICK, 2)));
        trades.add(createMerchantRecipe(new ItemStack(Material.DEEPSLATE, 4), new ItemStack(Material.BRICK, 4)));
        trades.add(createMerchantRecipe(new ItemStack(Material.OBSIDIAN, 1), new ItemStack(Material.NETHER_BRICK, 4)));
        blocksShop.setRecipes(trades);
        trades.clear();

        materialsShop = Bukkit.createMerchant();
        trades.add(createMerchantRecipe(new ItemStack(Material.OAK_PLANKS, 4), new ItemStack(Material.BRICK, 2)));
        trades.add(createMerchantRecipe(new ItemStack(Material.COBBLESTONE, 4), new ItemStack(Material.BRICK, 4)));
        trades.add(createMerchantRecipe(new ItemStack(Material.LEATHER, 4), new ItemStack(Material.BRICK, 4)));
        trades.add(createMerchantRecipe(new ItemStack(Material.IRON_INGOT, 4), new ItemStack(Material.NETHER_BRICK, 1)));
        trades.add(createMerchantRecipe(new ItemStack(Material.DIAMOND, 2), new ItemStack(Material.NETHER_BRICK, 2)));
        trades.add(createMerchantRecipe(new ItemStack(Material.STRING, 4), new ItemStack(Material.NETHER_BRICK, 1)));
        trades.add(createMerchantRecipe(new ItemStack(Material.NETHERITE_INGOT, 1), new ItemStack(Material.RESIN_BRICK, 1)));
        materialsShop.setRecipes(trades);
        trades.clear();

        foodShop = Bukkit.createMerchant();
        trades.add(createMerchantRecipe(new ItemStack(Material.MELON_SLICE, 3), new ItemStack(Material.BRICK, 1)));
        trades.add(createMerchantRecipe(new ItemStack(Material.CARROT, 2), new ItemStack(Material.BRICK, 3)));
        trades.add(createMerchantRecipe(new ItemStack(Material.COOKED_CHICKEN, 2), new ItemStack(Material.BRICK, 6)));
        trades.add(createMerchantRecipe(new ItemStack(Material.GOLDEN_APPLE, 1), new ItemStack(Material.RESIN_BRICK, 1)));
        trades.add(createMerchantRecipe(speed, new ItemStack(Material.BRICK, 32)));
        foodShop.setRecipes(trades);
        trades.clear();

        creaturesShop = Bukkit.createMerchant();
        trades.add(createMerchantRecipe(new ItemStack(Material.SILVERFISH_SPAWN_EGG, 1), new ItemStack(Material.NETHER_BRICK, 1)));
        trades.add(createMerchantRecipe(new ItemStack(Material.ZOMBIE_SPAWN_EGG, 1), new ItemStack(Material.NETHER_BRICK, 2)));
        trades.add(createMerchantRecipe(new ItemStack(Material.GUARDIAN_SPAWN_EGG, 1), new ItemStack(Material.NETHER_BRICK, 3)));
        trades.add(createMerchantRecipe(new ItemStack(Material.SKELETON_SPAWN_EGG, 1), new ItemStack(Material.NETHER_BRICK, 4)));
        trades.add(createMerchantRecipe(new ItemStack(Material.BLAZE_SPAWN_EGG, 1), new ItemStack(Material.NETHER_BRICK, 6)));
        trades.add(createMerchantRecipe(new ItemStack(Material.SHULKER_SPAWN_EGG, 1), new ItemStack(Material.NETHER_BRICK, 7)));
        trades.add(createMerchantRecipe(new ItemStack(Material.BREEZE_SPAWN_EGG, 1), new ItemStack(Material.NETHER_BRICK, 8)));
        creaturesShop.setRecipes(trades);
        trades.clear();

        equipmentShop = Bukkit.createMerchant();
        trades.add(createMerchantRecipe(new ItemStack(Material.ARROW, 16), new ItemStack(Material.RESIN_BRICK, 1)));
        trades.add(createMerchantRecipe(new ItemStack(Material.WIND_CHARGE, 8), new ItemStack(Material.RESIN_BRICK, 1)));

        trades.add(createMerchantRecipe(new ItemStack(Material.MACE, 1), new ItemStack(Material.RESIN_BRICK, 6)));
        trades.add(createMerchantRecipe(soulCrasher, new ItemStack[] {new ItemStack(Material.MACE), new ItemStack(Material.RESIN_BRICK, 8)}, 1));

        trades.add(createMerchantRecipe(new ItemStack(Material.TRIDENT, 1), new ItemStack(Material.RESIN_BRICK, 8)));
        trades.add(createMerchantRecipe(seaReaper, new ItemStack[] {new ItemStack(Material.TRIDENT), new ItemStack(Material.RESIN_BRICK, 8)}, 1));

        trades.add(createMerchantRecipe(hellShot, new ItemStack[] {new ItemStack(Material.BOW), new ItemStack(Material.RESIN_BRICK, 8)}, 1));

        trades.add(createMerchantRecipe(boltStorm, new ItemStack[] {new ItemStack(Material.CROSSBOW), new ItemStack(Material.RESIN_BRICK, 8)}, 1));

        equipmentShop.setRecipes(trades);
        trades.clear();
    }

    public final Location loc;
    private Villager villager;
    private final String type;
    private Merchant shop;
    public static String traderName;

    public Trader(Location loc, String type) {
        this.loc = loc;
        this.type = type;
    }

    public void spawn() {
        villager = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);
        villager.setCustomNameVisible(false);
        villager.setCustomName(traderName);
        villager.setAI(false);
        villager.setInvulnerable(true);
        villager.setGravity(false);
        villager.setSilent(true);

        traders.add(this);

        setUp();
    }

    public Entity getEntity() {
        return villager;
    }

    private void setUp() {
        if (type == null) {
            System.out.println("no trader type");
            return;
        }

        String name = getName();
        if (name != null) {
            traderName = name;
            villager.setCustomName(name);
        }

        switch (type) {
            case "blocks" -> {
                if (villager == null) return;
                villager.setProfession(Villager.Profession.MASON);
                shop = copyMerchant(blocksShop);
            }
            case "materials" -> {
                if (villager == null) return;
                villager.setProfession(Villager.Profession.WEAPONSMITH);
                shop = copyMerchant(materialsShop);
            }
            case "food" -> {
                if (villager == null) return;
                villager.setProfession(Villager.Profession.FARMER);
                shop = copyMerchant(foodShop);
            }
            case "creatures" -> {
                if (villager == null) return;
                villager.setProfession(Villager.Profession.CLERIC);
                shop = copyMerchant(creaturesShop);
            }
            case "equipment" -> {
                if (villager == null) return;
                villager.setProfession(Villager.Profession.CARTOGRAPHER);
                shop = copyMerchant(equipmentShop);
            }
        }
    }

    public Merchant getShop() {
        return shop;
    }

    private String getName() {
        switch (type) {
            case "blocks" -> {
                return "Блоки";
            }
            case "materials" -> {
                return "Материалы";
            }
            case "food" -> {
                return "Еда";
            }
            case "creatures" -> {
                return "Существа";
            }
            case "equipment" -> {
                return "Снаряжение";
            }
        }
        return null;
    }

    private static MerchantRecipe createMerchantRecipe(ItemStack result, ItemStack cost) {
        MerchantRecipe trade = new MerchantRecipe(result, 0, Integer.MAX_VALUE, false);
        trade.addIngredient(cost);
        return trade;
    }

    private static MerchantRecipe createMerchantRecipe(ItemStack result, ItemStack[] cost, int uses) {
        MerchantRecipe trade = new MerchantRecipe(result, 0, uses, false);
        for (ItemStack itemStack : cost) {
            trade.addIngredient(itemStack);
        }
        return trade;
    }

    private static Merchant copyMerchant(Merchant merchant) {
        // Create a new Merchant (without a direct name since Merchant doesn't store it)
        Merchant newMerchant = Bukkit.createMerchant(merchant instanceof Nameable ? ((Nameable) merchant).getCustomName() : null);

        // Copy all trade recipes
        List<MerchantRecipe> copiedRecipes = new ArrayList<>();
        for (MerchantRecipe recipe : merchant.getRecipes()) {
            MerchantRecipe copiedRecipe = new MerchantRecipe(
                    recipe.getResult(),
                    recipe.getUses(),
                    recipe.getMaxUses(),
                    recipe.hasExperienceReward(),
                    recipe.getVillagerExperience(),
                    recipe.getPriceMultiplier()
            );

            copiedRecipe.setIngredients(new ArrayList<>(recipe.getIngredients())); // Clone ingredients
            copiedRecipes.add(copiedRecipe);
        }

        newMerchant.setRecipes(copiedRecipes); // Set copied trades
        return newMerchant;
    }

}
