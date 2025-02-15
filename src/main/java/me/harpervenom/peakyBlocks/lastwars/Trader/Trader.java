package me.harpervenom.peakyBlocks.lastwars.Trader;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Nameable;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    private static final Merchant equipmentShop;
    private static final Merchant foodShop;
    private static final Merchant creaturesShop;
    private static final Merchant exchangeShop;
    static {
        List<MerchantRecipe> trades = new ArrayList<>();

        blocksShop = Bukkit.createMerchant("Блоки");
        trades.add(createMerchantRecipe(new ItemStack(Material.AZALEA_LEAVES, 4), new ItemStack(Material.BRICK, 1)));
        trades.add(createMerchantRecipe(new ItemStack(Material.SANDSTONE, 16), new ItemStack(Material.EMERALD, 2)));
        trades.add(createMerchantRecipe(new ItemStack(Material.DEEPSLATE, 16), new ItemStack(Material.EMERALD, 4)));
        trades.add(createMerchantRecipe(new ItemStack(Material.OBSIDIAN, 1), new ItemStack(Material.EMERALD, 32)));
        trades.add(createMerchantRecipe(new ItemStack(Material.CHEST, 1), new ItemStack(Material.EMERALD, 10)));
        trades.add(createMerchantRecipe(new ItemStack(Material.TORCH, 6), new ItemStack(Material.EMERALD, 1)));
        blocksShop.setRecipes(trades);
        trades.clear();

        equipmentShop = Bukkit.createMerchant("Снаряжение");
        trades.add(createMerchantRecipe(new ItemStack(Material.WOODEN_SHOVEL, 1), new ItemStack(Material.EMERALD, 5)));
        trades.add(createMerchantRecipe(new ItemStack(Material.WOODEN_PICKAXE, 1), new ItemStack(Material.EMERALD, 5)));
        trades.add(createMerchantRecipe(new ItemStack(Material.WOODEN_SWORD, 1), new ItemStack(Material.EMERALD, 12)));
        trades.add(createMerchantRecipe(new ItemStack(Material.STONE_SWORD, 1), new ItemStack(Material.EMERALD, 24)));
        trades.add(createMerchantRecipe(new ItemStack(Material.IRON_SWORD, 1), new ItemStack(Material.NETHER_BRICK, 6)));
        trades.add(createMerchantRecipe(new ItemStack(Material.DIAMOND_PICKAXE, 1), new ItemStack(Material.NETHER_BRICK, 6)));
        trades.add(createMerchantRecipe(new ItemStack(Material.DIAMOND_SWORD, 1), new ItemStack(Material.NETHER_BRICK, 12)));
        trades.add(createMerchantRecipe(new ItemStack(Material.NETHERITE_SWORD, 1), new ItemStack(Material.NETHER_BRICK, 28)));
        trades.add(createMerchantRecipe(new ItemStack(Material.BOW, 1), new ItemStack(Material.NETHER_BRICK, 15)));
        trades.add(createMerchantRecipe(new ItemStack(Material.CROSSBOW, 1), new ItemStack(Material.NETHER_BRICK, 18)));
        trades.add(createMerchantRecipe(new ItemStack(Material.ARROW, 4), new ItemStack(Material.EMERALD, 4)));
        trades.add(createMerchantRecipe(new ItemStack(Material.FLINT_AND_STEEL, 1), new ItemStack(Material.EMERALD, 4)));

        trades.add(createMerchantRecipe(new ItemStack(Material.LEATHER_BOOTS, 1), new ItemStack(Material.EMERALD, 4)));
        trades.add(createMerchantRecipe(new ItemStack(Material.LEATHER_LEGGINGS, 1), new ItemStack(Material.EMERALD, 8)));
        trades.add(createMerchantRecipe(new ItemStack(Material.LEATHER_CHESTPLATE, 1), new ItemStack(Material.EMERALD, 10)));
        trades.add(createMerchantRecipe(new ItemStack(Material.LEATHER_HELMET, 1), new ItemStack(Material.EMERALD, 4)));
        equipmentShop.setRecipes(trades);
        trades.clear();

        foodShop = Bukkit.createMerchant("Еда");
        trades.add(createMerchantRecipe(new ItemStack(Material.MELON_SLICE, 8), new ItemStack(Material.EMERALD, 1)));
        trades.add(createMerchantRecipe(new ItemStack(Material.CARROT, 8), new ItemStack(Material.EMERALD, 4)));
        trades.add(createMerchantRecipe(new ItemStack(Material.COOKED_CHICKEN, 6), new ItemStack(Material.EMERALD, 8)));
        foodShop.setRecipes(trades);
        trades.clear();

        creaturesShop = Bukkit.createMerchant("Существа");
        trades.add(createMerchantRecipe(new ItemStack(Material.SILVERFISH_SPAWN_EGG, 1), new ItemStack(Material.NETHER_BRICK, 3)));
        trades.add(createMerchantRecipe(new ItemStack(Material.ZOMBIE_SPAWN_EGG, 1), new ItemStack(Material.NETHER_BRICK, 6)));
        trades.add(createMerchantRecipe(new ItemStack(Material.SKELETON_SPAWN_EGG, 1), new ItemStack(Material.NETHER_BRICK, 14)));
        trades.add(createMerchantRecipe(new ItemStack(Material.BLAZE_SPAWN_EGG, 1), new ItemStack(Material.NETHER_BRICK, 24)));
        creaturesShop.setRecipes(trades);
        trades.clear();

        exchangeShop = Bukkit.createMerchant("Обмен валют");
        trades.add(createMerchantRecipe(new ItemStack(Material.EMERALD, 1), new ItemStack(Material.BRICK, 8)));
        trades.add(createMerchantRecipe(new ItemStack(Material.NETHER_BRICK, 1), new ItemStack(Material.EMERALD, 16)));
        exchangeShop.setRecipes(trades);
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
            case "equipment" -> {
                if (villager == null) return;
                villager.setProfession(Villager.Profession.WEAPONSMITH);
                shop = copyMerchant(equipmentShop);
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
            case "exchange" -> {
                if (villager == null) return;
                villager.setProfession(Villager.Profession.CARTOGRAPHER);
                shop = copyMerchant(exchangeShop);
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
            case "equipment" -> {
                return "Снаряжение";
            }
            case "food" -> {
                return "Еда";
            }
            case "creatures" -> {
                return "Существа";
            }
            case "exchange" -> {
                return "Обмен Валют";
            }
        }
        return null;
    }

    private static MerchantRecipe createMerchantRecipe(ItemStack result, ItemStack cost) {
        MerchantRecipe trade = new MerchantRecipe(result, 0, Integer.MAX_VALUE, false);
        trade.addIngredient(cost);
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
