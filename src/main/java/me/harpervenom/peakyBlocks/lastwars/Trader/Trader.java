package me.harpervenom.peakyBlocks.lastwars.Trader;

import me.harpervenom.peakyBlocks.utils.CustomMenuHolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;

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

    private static final Inventory builderShop;
    private static final Inventory utilitiesShop;
    private static final Inventory ingredientsShop;
    private static final Inventory foodShop;
    private static final Inventory creaturesShop;
    static {
        builderShop = Bukkit.createInventory(new CustomMenuHolder("traderMenu"), 54, "Блоки");
        builderShop.addItem((new Good(Material.GRAVEL, 12, 20)).getDisplayItem());
        builderShop.addItem((new Good(Material.WHITE_WOOL, 12, 30)).getDisplayItem());
        builderShop.addItem((new Good(Material.SANDSTONE, 12, 40)).getDisplayItem());
        builderShop.addItem((new Good(Material.DEEPSLATE, 12, 80)).getDisplayItem());
        builderShop.addItem((new Good(Material.OBSIDIAN, 1, 400)).getDisplayItem());

        utilitiesShop = Bukkit.createInventory(new CustomMenuHolder("traderMenu"), 54, "Снаряжение");
        utilitiesShop.addItem((new Good(Material.WOODEN_SHOVEL, 1, 20)).getDisplayItem());
        utilitiesShop.addItem((new Good(Material.WOODEN_PICKAXE, 1, 20)).getDisplayItem());
        utilitiesShop.addItem((new Good(Material.WOODEN_AXE, 1, 30)).getDisplayItem());
        utilitiesShop.addItem((new Good(Material.WOODEN_SWORD, 1, 30)).getDisplayItem());
        utilitiesShop.addItem((new Good(Material.STONE_SWORD, 1, 60)).getDisplayItem());
        utilitiesShop.addItem((new Good(Material.IRON_SWORD, 1, 120)).getDisplayItem());
        utilitiesShop.addItem((new Good(Material.DIAMOND_SWORD, 1, 250)).getDisplayItem());
        utilitiesShop.addItem((new Good(Material.ARROW, 4, 120)).getDisplayItem());
        utilitiesShop.addItem((new Good(Material.CHEST, 1, 120)).getDisplayItem());

        ingredientsShop = Bukkit.createInventory(new CustomMenuHolder("traderMenu"), 54, "Ингредиенты");
        ingredientsShop.addItem((new Good(Material.OAK_PLANKS, 4, 100)).getDisplayItem());
        ingredientsShop.addItem((new Good(Material.COBBLESTONE, 4, 180)).getDisplayItem());
        ingredientsShop.addItem((new Good(Material.IRON_INGOT, 2, 240)).getDisplayItem());
        ingredientsShop.addItem((new Good(Material.DIAMOND, 2, 360)).getDisplayItem());

        foodShop = Bukkit.createInventory(new CustomMenuHolder("traderMenu"), 54, "Еда");
        foodShop.addItem((new Good(Material.SWEET_BERRIES, 16, 20)).getDisplayItem());
        foodShop.addItem((new Good(Material.CARROT, 16, 60)).getDisplayItem());
        foodShop.addItem((new Good(Material.COOKED_CHICKEN, 6, 80)).getDisplayItem());

        creaturesShop = Bukkit.createInventory(new CustomMenuHolder("traderMenu"), 54, "Существа");
        creaturesShop.addItem((new Good(Material.ZOMBIE_SPAWN_EGG, 1, 120)).getDisplayItem());
        creaturesShop.addItem((new Good(Material.HUSK_SPAWN_EGG, 1, 200)).getDisplayItem());
        creaturesShop.addItem((new Good(Material.LLAMA_SPAWN_EGG, 1, 230)).getDisplayItem());
        creaturesShop.addItem((new Good(Material.SKELETON_SPAWN_EGG, 1, 300)).getDisplayItem());
    }

    public final Location loc;
    private Villager villager;
    private final String type;
    private Inventory shop;
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
            case "builder" -> {
                if (villager == null) return;
                villager.setProfession(Villager.Profession.MASON);

                shop = builderShop;
                return;
            }
            case "utilities" -> {
                if (villager == null) return;
                villager.setProfession(Villager.Profession.TOOLSMITH);

                shop = utilitiesShop;
                return;
            }
            case "ingredients" -> {
                if (villager == null) return;
                villager.setProfession(Villager.Profession.CARTOGRAPHER);

                shop = ingredientsShop;
                return;
            }
            case "food" -> {
                if (villager == null) return;
                villager.setProfession(Villager.Profession.FARMER);

                shop = foodShop;
                return;
            }
            case "creatures" -> {
                if (villager == null) return;
                villager.setProfession(Villager.Profession.CLERIC);

                shop = creaturesShop;
                return;
            }
        }
    }

    public Inventory getShop() {
        return shop;
    }

    private String getName() {
        switch (type) {
            case "builder" -> {
                return "Строитель";
            }
            case "utilities" -> {
                return "Снаряжение";
            }
            case "ingredients" -> {
                return "Ингредиенты";
            }
            case "food" -> {
                return "Еда";
            }
            case "creatures" -> {
                return "Существа";
            }
        }
        return null;
    }
}
