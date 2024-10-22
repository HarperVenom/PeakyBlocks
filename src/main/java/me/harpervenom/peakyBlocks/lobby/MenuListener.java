package me.harpervenom.peakyBlocks.lobby;

import me.harpervenom.peakyBlocks.classes.Game;
import me.harpervenom.peakyBlocks.classes.Team;
import me.harpervenom.peakyBlocks.utils.CustomMenuHolder;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static me.harpervenom.peakyBlocks.PeakyBlocks.getPlugin;
import static me.harpervenom.peakyBlocks.classes.Game.lastGameId;
import static me.harpervenom.peakyBlocks.utils.Utils.createItem;

public class MenuListener implements Listener {

    private final String menuItemName = ChatColor.GOLD + "Меню";
    private final String gameButtonName = ChatColor.LIGHT_PURPLE + "LastWars";
    private final String createButtonName = ChatColor.YELLOW + "Создать";

    private ItemStack createButton;

    private final ItemStack navigator;
    private Inventory menu;
    private Inventory queuesMenu; // first and only game. When new games appear, this will turn into a hashmap game name / this game's queues menu
    private final HashMap<Integer, Inventory> teamMenus = new HashMap<>(); //game id and its inventory
    private Inventory maxPlayersMenu;

//    private final HashMap<ItemStack, Integer> queueButtons = new HashMap<>();
    private final HashMap<Integer, Team> teamButtons = new HashMap<>(); // slot / team
    public static HashMap<UUID, Team> playerTeam = new HashMap<>();

    public HashMap<UUID, Game> playerCreatingGame = new HashMap<>();
    public HashMap<UUID, Game> playerSelectingGame = new HashMap<>();
    public static List<Game> activeGames = new ArrayList<>();

    public HashMap<UUID, Boolean> switchingMenus = new HashMap<>();

    public MenuListener() {
        navigator = new ItemStack(Material.COMPASS);
        ItemMeta meta = navigator.getItemMeta();
        if (meta == null) return;
        meta.setDisplayName(menuItemName);
        navigator.setItemMeta(meta);

        menu = Bukkit.createInventory(new CustomMenuHolder("menu"), 27, "Меню");

        ItemStack gameButton = new ItemStack(Material.NETHERITE_HELMET);
        meta = gameButton.getItemMeta();
        if (meta == null) return;
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setDisplayName(gameButtonName);
        gameButton.setItemMeta(meta);

        menu.setItem(13, gameButton);

        createButton = new ItemStack(Material.WRITABLE_BOOK);
        meta = createButton.getItemMeta();
        if (meta == null) return;
        meta.setDisplayName(createButtonName);
        createButton.setItemMeta(meta);

        queuesMenu = Bukkit.createInventory(new CustomMenuHolder("selectQueue"), 27, "Выберите игру:");
        updateGameMenu();

        maxPlayersMenu = Bukkit.createInventory(new CustomMenuHolder("selectMaxPlayer"), 27, "Выберите размер:");
    }

    @EventHandler
    public void PlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Inventory inv = p.getInventory();
        inv.clear();
        inv.setItem(0, navigator);
    }

    @EventHandler
    public void OpenMenu(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (p.getGameMode() == GameMode.CREATIVE) return;
        if (e.getHand() != EquipmentSlot.HAND) return;
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack menuItem = p.getInventory().getItemInMainHand();
        ItemMeta menuItemMeta = menuItem.getItemMeta();
        if (menuItemMeta == null || !menuItemMeta.getDisplayName().equals(menuItemName)) return;

        e.setCancelled(true);

        p.openInventory(menu);
    }

    //Click of the game button
    @EventHandler
    public void SelectGame(InventoryClickEvent e) {
        Player p = getPlayer(e);
        if (p == null) return;
        CustomMenuHolder holder = getCustomMenuHolder(e);
        if (holder == null || !holder.getType().equals("menu")) return;

        if (isMatchingItem(e.getCurrentItem(), gameButtonName)) {
            switchingMenus.put(p.getUniqueId(), true);
            p.openInventory(queuesMenu);
        }
    }

    @EventHandler
    public void CreateGame(InventoryClickEvent e) {
        Player p = getPlayer(e);
        if (p == null) return;
        CustomMenuHolder holder = getCustomMenuHolder(e);
        if (holder == null || !holder.getType().equals("selectQueue")) return;

        if (isMatchingItem(e.getCurrentItem(), createButtonName)) {
            Game newGame = new Game(2);
            playerCreatingGame.put(p.getUniqueId(), newGame);

            List<ItemStack> options = getMaxPlayersOptions(2);
            for (int i = 0; i < options.size(); i++) {
                maxPlayersMenu.setItem(i, options.get(i));
            }
            switchingMenus.put(p.getUniqueId(), true);
            p.openInventory(maxPlayersMenu);
        }
    }

    @EventHandler
    public void SelectMaxPlayers(InventoryClickEvent e) {
        Player p = getPlayer(e);
        if (p == null) return;
        CustomMenuHolder holder = getCustomMenuHolder(e);
        if (holder == null || !holder.getType().equals("selectMaxPlayer")) return;

        if (!playerCreatingGame.containsKey(p.getUniqueId())) return;

        int slot = e.getRawSlot() + 1;
        int maxPlayers = 0;
        switch (slot) {
            case 1 -> {maxPlayers = 1;}
            case 2 -> {maxPlayers = 2;}
            case 3 -> {maxPlayers = 3;}
            case 4 -> {maxPlayers = 4;}
            case 5 -> {maxPlayers = 5;}
        }
        Game game = playerCreatingGame.get(p.getUniqueId());
        game.setMaxPlayers(maxPlayers);

        //Create a new game teams menu
        Inventory teamsMenu = Bukkit.createInventory(new CustomMenuHolder("selectTeam"), 27, "Выберите команду:");
        teamMenus.put(game.getId(), teamsMenu);
        updateTeamMenu(game);

        switchingMenus.put(p.getUniqueId(), true);
        p.openInventory(teamsMenu);
    }

    @EventHandler
    public void SelectTeam(InventoryClickEvent e) {
        Player p = getPlayer(e);
        if (p == null) return;
        UUID id = p.getUniqueId();
        CustomMenuHolder holder = getCustomMenuHolder(e);
        if (holder == null || !holder.getType().equals("selectTeam")) return;

        Game game = null;
        if (playerCreatingGame.containsKey(id)) {
            game = playerCreatingGame.get(id);
        }
        if (playerSelectingGame.containsKey(id)) {
            game = playerSelectingGame.get(id);
        }

        if (game == null) return;
        p.sendMessage(game.getId() + "");

        List<Team> teams = game.getTeams();
        List<Integer> teamButtons = getTeamButtonsSlots(teams.size());

        int slot = e.getRawSlot();
        if (!teamButtons.contains(slot)) return;

        Team team = teams.get(teamButtons.indexOf(slot));

        if (playerTeam.containsKey(id)) {
            Team oldTeam = playerTeam.get(id);
            if (oldTeam.equals(team)) {
                p.sendMessage(ChatColor.YELLOW + "Вы уже находитесь в этой команде.");
                return;
            }

            if (team.getGameId() == oldTeam.getGameId()) {
                p.sendMessage(ChatColor.DARK_GRAY + "Вы покинули команду.");
            } else {
                p.sendMessage(ChatColor.DARK_GRAY + "Вы покинули предыдущую игру.");
            }
            team.addMember(p);
            oldTeam.removeMember(p);
            playerTeam.remove(id);
        } else {
            team.addMember(p);
        }
        playerTeam.put(id, team);
        p.sendMessage(ChatColor.GRAY + "Вы присоединились к команде: " + team.getColor() + team.getTeamName());

        if (!activeGames.contains(game)) activeGames.add(game);

        if (playerCreatingGame.containsKey(id)) {
            playerCreatingGame.remove(id);
            playerSelectingGame.put(id, game);
        }

        updateGameMenu();
        updateTeamMenu(game);
    }

    @EventHandler
    public void SelectQueue(InventoryClickEvent e) {
        Player p = getPlayer(e);
        if (p == null) return;
        CustomMenuHolder holder = getCustomMenuHolder(e);
        if (holder == null || !holder.getType().equals("selectQueue")) return;

        int slot = e.getRawSlot();
        if (slot + 1 > activeGames.size()) return;
        Game game = activeGames.get(slot);
        playerSelectingGame.put(p.getUniqueId(), game);

        switchingMenus.put(p.getUniqueId(), true);
        p.openInventory(teamMenus.get(game.getId()));
    }

    @EventHandler
    public void CloseMenu(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();

        if (switchingMenus.getOrDefault(p.getUniqueId(), false)) {
            switchingMenus.put(p.getUniqueId(), false);
            return;
        }

        if (playerCreatingGame.containsKey(p.getUniqueId())) {
            lastGameId--;
        }

        playerSelectingGame.remove(p.getUniqueId());
        playerSelectingGame.remove(p.getUniqueId());
    }

    private Player getPlayer(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player p) {
            return p;
        }
        return null;
    }

    private CustomMenuHolder getCustomMenuHolder(InventoryClickEvent e) {
        Inventory inv = e.getClickedInventory();
        if (inv == null || !(inv.getHolder() instanceof CustomMenuHolder holder)) {
            return null;
        }
        return holder;
    }

    private boolean isMatchingItem(ItemStack item, String displayName) {
        if (item == null) return false;
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getDisplayName().equals(displayName);
    }


    public Game getGameById(int id) {
        for (Game game : activeGames) {
            if (game.getId() == id) return game;
        }
        return null;
    }

    public void updateGameMenu() {
        queuesMenu.clear();

        if (activeGames.isEmpty()) {
            queuesMenu.setItem(0, createButton);
            return;
        }

        for (int i = 0; i < activeGames.size() + 1; i++) {
            if (i == activeGames.size()) {
                queuesMenu.setItem(i, createButton);
                return;
            }

            Game game = activeGames.get(i);
            if (game == null) continue;
            ItemStack gameItem = createGameItem(game);

            queuesMenu.setItem(i, gameItem);

            updateTeamMenu(game);
        }
    }

    private ItemStack createGameItem(Game game) {
        ItemStack gameItem = new ItemStack(Material.CAMPFIRE);
        ItemMeta meta = gameItem.getItemMeta();
        if (meta == null) return null;



        meta.setDisplayName(ChatColor.GOLD + "Игра " + game.getId());

        List<String> lore = List.of(ChatColor.GRAY + "Игроков: " + game.getNumberOfPlayers());
        meta.setLore(lore);

        gameItem.setItemMeta(meta);
        return gameItem;
    }

    public void updateTeamMenu(Game game) {
        Inventory menu = teamMenus.get(game.getId());
        menu.clear();

        List<Team> teams = game.getTeams();

        updateTeamItem(menu, teams.get(0), "Красные", Material.RED_CONCRETE, 11, ChatColor.RED);
        updateTeamItem(menu, teams.get(1), "Синие", Material.BLUE_CONCRETE, 15, ChatColor.BLUE);
    }

    private void updateTeamItem(Inventory menu, Team team, String teamName, Material material, int slot, ChatColor color) {
        ItemStack teamItem = new ItemStack(material);
        ItemMeta meta = teamItem.getItemMeta();
        if (meta == null) return;

        meta.setDisplayName(color + teamName);
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "" + team.getMembers().size() + "/" + team.getMaxMembers());
        for (UUID id : team.getMembers()) {
            lore.add(ChatColor.GRAY + Bukkit.getPlayer(id).getDisplayName());
        }
        meta.setLore(lore);
        teamItem.setItemMeta(meta);

        menu.setItem(slot, teamItem);
    }

    public List<ItemStack> getMaxPlayersOptions(int numberOfTeams) {
        List<ItemStack> options = new ArrayList<>();

        if (numberOfTeams == 2) {
            ItemStack option1 = createItem(Material.PLAYER_HEAD, ChatColor.WHITE + "1x1", null);
            ItemStack option2 = createItem(Material.PLAYER_HEAD, ChatColor.WHITE + "2x2", null);
            ItemStack option3 = createItem(Material.PLAYER_HEAD, ChatColor.WHITE + "3x3", null);
            ItemStack option4 = createItem(Material.PLAYER_HEAD, ChatColor.WHITE + "4x4", null);
            ItemStack option5 = createItem(Material.PLAYER_HEAD, ChatColor.WHITE + "5x5", null);

            options.add(option1);
            options.add(option2);
            options.add(option3);
            options.add(option4);
            options.add(option5);
        }

        return options;
    }

    public List<Integer> getTeamButtonsSlots(int numberOfTeams) {
        switch (numberOfTeams) {
            case 2 -> {return new ArrayList<>(Arrays.asList(11, 15));}
        }
        return null;
    }
}
