package me.harpervenom.peakyBlocks.lobby;

import me.harpervenom.peakyBlocks.classes.Queue;
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
import static me.harpervenom.peakyBlocks.classes.Queue.lastQueueId;
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

    public HashMap<UUID, Queue> playerCreatingQueue = new HashMap<>();
    public HashMap<UUID, Queue> playerSelectingQueue = new HashMap<>();
    public static List<Queue> activeQueues = new ArrayList<>();

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

        maxPlayersMenu = Bukkit.createInventory(new CustomMenuHolder("selectMaxPlayer"), 27, "Выберите размер команды:");
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
            Queue newQueue = new Queue(2);
            playerCreatingQueue.put(p.getUniqueId(), newQueue);


            //Open selectMaxPlayer menu
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

        if (!playerCreatingQueue.containsKey(p.getUniqueId())) return;

        int slot = e.getRawSlot() + 1;
        int maxPlayers = 0;
        switch (slot) {
            case 1 -> {maxPlayers = 1;}
            case 2 -> {maxPlayers = 2;}
            case 3 -> {maxPlayers = 3;}
            case 4 -> {maxPlayers = 4;}
            case 5 -> {maxPlayers = 5;}
        }
        Queue queue = playerCreatingQueue.get(p.getUniqueId());
        queue.setMaxPlayers(maxPlayers);

        //Create and open new game teams menu
        Inventory teamsMenu = Bukkit.createInventory(new CustomMenuHolder("selectTeam"), 27, "Выберите команду:");
        teamMenus.put(queue.getId(), teamsMenu);
        updateTeamMenu(queue);

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

        Queue queue = null;
        if (playerCreatingQueue.containsKey(id)) {
            queue = playerCreatingQueue.get(id);
        }
        if (playerSelectingQueue.containsKey(id)) {
            queue = playerSelectingQueue.get(id);
        }

        if (queue == null) return;

        List<Team> teams = queue.getTeams();
        List<Integer> teamButtons = getTeamButtonsSlots(teams.size());

        int slot = e.getRawSlot();
        if (!teamButtons.contains(slot)) return;

        Team team = teams.get(teamButtons.indexOf(slot));

        boolean playerAdded = false;

        if (playerTeam.containsKey(id)) {
            Team oldTeam = playerTeam.get(id);
            Queue oldQueue = team.getGameId() == oldTeam.getGameId() ? queue
                    : activeQueues.stream().filter(currentQueue -> currentQueue.getId() == oldTeam.getGameId()).findFirst().orElse(null);
            if (oldQueue != null){
                if (oldTeam.equals(team)) {
                    p.sendMessage(ChatColor.YELLOW + "Вы уже находитесь в этой команде.");
                    return;
                }

                if (team.getGameId() == oldTeam.getGameId()) {
                    p.sendMessage(ChatColor.DARK_GRAY + "Вы покинули команду.");
                } else {
                    p.sendMessage(ChatColor.DARK_GRAY + "Вы покинули предыдущую игру.");
                }
                playerAdded = queue.addPlayerToTeam(p, team);
                oldQueue.removePlayerFromTeam(p, oldTeam);
                playerTeam.remove(id);
            }
        } else {
            playerAdded = queue.addPlayerToTeam(p, team);
        }

        if (!playerAdded) {
            p.sendMessage(ChatColor.RED + "В команде нет свободных мест.");
            return;
        }

        playerTeam.put(id, team);
        p.sendMessage(ChatColor.GRAY + "Вы присоединились к команде: " + team.getColor() + team.getTeamName());

        if (!activeQueues.contains(queue)) activeQueues.add(queue);

        if (playerCreatingQueue.containsKey(id)) {
            playerCreatingQueue.remove(id);
            playerSelectingQueue.put(id, queue);
        }

        updateGameMenu();
        updateTeamMenu(queue);
    }

    @EventHandler
    public void SelectQueue(InventoryClickEvent e) {
        Player p = getPlayer(e);
        if (p == null) return;
        CustomMenuHolder holder = getCustomMenuHolder(e);
        if (holder == null || !holder.getType().equals("selectQueue")) return;

        int slot = e.getRawSlot();
        if (slot + 1 > activeQueues.size()) return;
        Queue queue = activeQueues.get(slot);
        playerSelectingQueue.put(p.getUniqueId(), queue);

        switchingMenus.put(p.getUniqueId(), true);
        p.openInventory(teamMenus.get(queue.getId()));
    }

    @EventHandler
    public void CloseMenu(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();

        if (switchingMenus.getOrDefault(p.getUniqueId(), false)) {
            switchingMenus.put(p.getUniqueId(), false);
            return;
        }

        if (playerCreatingQueue.containsKey(p.getUniqueId())) {
            lastQueueId--;
        }

        playerSelectingQueue.remove(p.getUniqueId());
        playerSelectingQueue.remove(p.getUniqueId());
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


    public Queue getGameById(int id) {
        for (Queue queue : activeQueues) {
            if (queue.getId() == id) return queue;
        }
        return null;
    }

    public void updateGameMenu() {
        queuesMenu.clear();

        if (activeQueues.isEmpty()) {
            queuesMenu.setItem(0, createButton);
            return;
        }

        for (int i = 0; i < activeQueues.size() + 1; i++) {
            if (i == activeQueues.size()) {
                queuesMenu.setItem(i, createButton);
                return;
            }

            Queue queue = activeQueues.get(i);
            if (queue == null) continue;
            ItemStack gameItem = createGameItem(queue);

            queuesMenu.setItem(i, gameItem);

//            updateTeamMenu(queue);
        }
    }

    private ItemStack createGameItem(Queue queue) {
        ItemStack gameItem = new ItemStack(Material.CAMPFIRE);
        ItemMeta meta = gameItem.getItemMeta();
        if (meta == null) return null;

        int teamSize = queue.getTeamSize();
        String playerSizeString = teamSize + "x" + teamSize;
        int totalMaxPlayers = queue.getNumberOfTeams() * teamSize;

        meta.setDisplayName(ChatColor.GOLD + "Игра " + queue.getId() + " (" + playerSizeString + ")");

        List<String> lore = List.of(ChatColor.GRAY + "Игроков: " + queue.getNumberOfPlayers() + "/" + queue.getTotalMaxPlayers());
        meta.setLore(lore);

        gameItem.setItemMeta(meta);
        return gameItem;
    }

    public void updateTeamMenu(Queue queue) {
        Inventory menu = teamMenus.get(queue.getId());
        menu.clear();

        List<Team> teams = queue.getTeams();

        updateTeamItem(menu, teams.get(0), "Красные", Material.RED_CONCRETE, 11, ChatColor.RED);
        updateTeamItem(menu, teams.get(1), "Синие", Material.BLUE_CONCRETE, 15, ChatColor.BLUE);
    }

    private void updateTeamItem(Inventory menu, Team team, String teamName, Material material, int slot, ChatColor color) {
        ItemStack teamItem = new ItemStack(material);
        ItemMeta meta = teamItem.getItemMeta();
        if (meta == null) return;

        meta.setDisplayName(color + teamName);
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "" + team.getPlayers().size() + "/" + team.getMaxPlayers());
        for (UUID id : team.getPlayers()) {
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
