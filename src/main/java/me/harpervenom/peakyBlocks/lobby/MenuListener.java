package me.harpervenom.peakyBlocks.lobby;

import me.harpervenom.peakyBlocks.lastwars.Map.Map;
import me.harpervenom.peakyBlocks.queue.Queue;
import me.harpervenom.peakyBlocks.queue.QueuePlayer;
import me.harpervenom.peakyBlocks.queue.QueueTeam;
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

import static me.harpervenom.peakyBlocks.lastwars.Map.Map.sampleMaps;
import static me.harpervenom.peakyBlocks.queue.Queue.activeQueues;
import static me.harpervenom.peakyBlocks.queue.Queue.lastQueueId;
import static me.harpervenom.peakyBlocks.queue.QueuePlayer.getQueuePlayer;
import static me.harpervenom.peakyBlocks.queue.QueuePlayer.queuePlayers;
import static me.harpervenom.peakyBlocks.utils.Utils.changeItemTitle;
import static me.harpervenom.peakyBlocks.utils.Utils.createItem;

public class MenuListener implements Listener {

    private final String menuItemName = ChatColor.WHITE + "Меню";
    private final String gameButtonName = ChatColor.LIGHT_PURPLE + "LastWars";
    private final String createButtonName = ChatColor.WHITE + "Создать";
    private final static String deleteQueueButtonName = ChatColor.WHITE + "Удалить очередь";
    private final static String leaveQueueButtonName = ChatColor.WHITE + "Покинуть очередь";

    private static ItemStack createButton;

    public static ItemStack navigator;
    private final static ItemStack leaveQueueButton;

    private final Inventory menu;
    private static Inventory queuesMenu; // first and only game. When new games appear, this will turn into a hashmap game name / this game's queues menu
    private Inventory mapMenu;
    private final Inventory maxPlayersMenu;
    private static final HashMap<Integer, Inventory> teamMenus = new HashMap<>(); //game id and its inventory


    public HashMap<UUID, Queue> playerCreatingQueue = new HashMap<>();
    public HashMap<UUID, Queue> playerSelectingQueue = new HashMap<>();

    public HashMap<UUID, Boolean> switchingMenus = new HashMap<>();

    static {
        leaveQueueButton = createItem(Material.RED_TERRACOTTA, leaveQueueButtonName, null);
    }

    public MenuListener() {
        navigator = createItem(Material.COMPASS, menuItemName, null);
        ItemStack gameButton = createItem(Material.NETHERITE_HELMET, gameButtonName, List.of(
                ChatColor.WHITE + "Командное сражение.",
                ChatColor.WHITE + "Разрушьте вражеское ядро, чтобы победить"), true);
        createButton = createItem(Material.WRITABLE_BOOK, createButtonName, null);


        menu = Bukkit.createInventory(new CustomMenuHolder("menu"), 27, "Меню");
        queuesMenu = Bukkit.createInventory(new CustomMenuHolder("selectQueue"), 27, "Выберите игру:");
        mapMenu = Bukkit.createInventory(new CustomMenuHolder("selectMap"), 27, "Выберите карту");
        maxPlayersMenu = Bukkit.createInventory(new CustomMenuHolder("selectMaxPlayer"), 27, "Выберите размер команды:");


        menu.setItem(13, gameButton);

        List<ItemStack> options = getMaxPlayersOptions();
        for (int i = 0; i < options.size(); i++) {
            maxPlayersMenu.setItem(i, options.get(i));
        }

        for (Map map : sampleMaps) {
            ItemStack mapItem = createItem(Material.FILLED_MAP, ChatColor.WHITE + map.getDisplayName(),
                    List.of(ChatColor.GRAY + "Команды: " + map.getLocSets().size()));
            mapMenu.setItem(sampleMaps.indexOf(map), mapItem);
        }

        updateQueueMenu();
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
            Queue newQueue = new Queue(p.getUniqueId(), 2);
            playerCreatingQueue.put(p.getUniqueId(), newQueue);

            //Open selectMap menu
            switchingMenus.put(p.getUniqueId(), true);
            p.openInventory(mapMenu);
        }
    }

    @EventHandler
    public void SelectMap(InventoryClickEvent e) {
        Player p = getPlayer(e);
        if (p == null) return;
        CustomMenuHolder holder = getCustomMenuHolder(e);
        if (holder == null || !holder.getType().equals("selectMap")) return;

        if (!playerCreatingQueue.containsKey(p.getUniqueId())) return;

        ItemStack item = e.getCurrentItem();
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        String name = ChatColor.stripColor(meta.getDisplayName());
        for (Map map : sampleMaps) {
            if (map.getDisplayName().equals(name)) {
                Queue queue = playerCreatingQueue.get(p.getUniqueId());
                queue.setMap(new Map(map));

                //Open selectMaxPlayer menu
                switchingMenus.put(p.getUniqueId(), true);
                p.openInventory(maxPlayersMenu);
                return;
            }
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
        QueuePlayer qp = getQueuePlayer(p);
        if (qp == null) return;
        UUID playerId = p.getUniqueId();
        CustomMenuHolder holder = getCustomMenuHolder(e);
        if (holder == null || !holder.getType().equals("selectTeam")) return;

        Queue queue = null;
        if (playerCreatingQueue.containsKey(playerId)) {
            queue = playerCreatingQueue.get(playerId);
        }
        if (playerSelectingQueue.containsKey(playerId)) {
            queue = playerSelectingQueue.get(playerId);
        }

        if (queue == null) return;

        List<QueueTeam> teams = queue.getTeams();
        List<Integer> teamButtons = getTeamButtonsSlots(teams.size());

        int slot = e.getRawSlot();
        if (!teamButtons.contains(slot)) return;

        QueueTeam team = teams.get(teamButtons.indexOf(slot));

        team.addPlayer(qp);

        if (!activeQueues.contains(queue)) activeQueues.add(queue);

        playerCreatingQueue.remove(playerId);
        playerSelectingQueue.put(playerId, queue);

        updateQueueMenu();
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

    @EventHandler
    public void LeaveQueue(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        QueuePlayer qp = getQueuePlayer(p);
        if (qp == null) return;
        if (p.getGameMode() == GameMode.CREATIVE) return;
        if (e.getHand() != EquipmentSlot.HAND) return;
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack menuItem = p.getInventory().getItemInMainHand();
        ItemMeta menuItemMeta = menuItem.getItemMeta();
        if (menuItemMeta == null ||
                (!menuItemMeta.getDisplayName().equals(leaveQueueButtonName)
                        && !menuItemMeta.getDisplayName().equals(deleteQueueButtonName))) return;

        e.setCancelled(true);

        if (qp.getTeam() != null) {
            QueueTeam team = qp.getTeam();
            team.removePlayer(qp, false, false);
        }
    }

    private Player getPlayer(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player p) {
            return p;
        }
        return null;
    }

    public static CustomMenuHolder getCustomMenuHolder(InventoryClickEvent e) {
        Inventory inv = e.getInventory();
        if (!(inv.getHolder() instanceof CustomMenuHolder holder)) {
            return null;
        }
        return holder;
    }

    private boolean isMatchingItem(ItemStack item, String displayName) {
        if (item == null) return false;
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getDisplayName().equals(displayName);
    }

    public static void updateQueueMenu() {
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
        }
    }

    private static ItemStack createGameItem(Queue queue) {
        ItemStack gameItem = new ItemStack(Material.CAMPFIRE);
        ItemMeta meta = gameItem.getItemMeta();
        if (meta == null) return null;

        int teamSize = queue.getTeamSize();
        String playerSizeString = teamSize + "x" + teamSize;

        meta.setDisplayName(ChatColor.WHITE + "Игра " + queue.getId() + " (" + playerSizeString + ")");

        List<String> lore = List.of(ChatColor.GRAY + "Игроков: " + queue.getNumberOfPlayers() + "/" + queue.getTotalMaxPlayers());
        meta.setLore(lore);

        gameItem.setItemMeta(meta);
        return gameItem;
    }

    public static void updateTeamMenu(Queue queue) {
        Inventory menu = teamMenus.get(queue.getId());
        menu.clear();

        List<QueueTeam> teams = queue.getTeams();

        updateTeamItem(menu, teams.get(0), "Красные", Material.RED_CONCRETE, 11, ChatColor.RED);
        updateTeamItem(menu, teams.get(1), "Синие", Material.BLUE_CONCRETE, 15, ChatColor.BLUE);
    }

    private static void updateTeamItem(Inventory menu, QueueTeam team, String teamName, Material material, int slot, ChatColor color) {
        ItemStack teamItem = new ItemStack(material);
        ItemMeta meta = teamItem.getItemMeta();
        if (meta == null) return;

        meta.setDisplayName(color + teamName);
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "" + team.getPlayers().size() + "/" + team.getMaxPlayers());
        for (QueuePlayer qp : team.getPlayers()) {
            lore.add(ChatColor.GRAY + qp.getPlayer().getDisplayName());
        }
        meta.setLore(lore);
        teamItem.setItemMeta(meta);

        menu.setItem(slot, teamItem);
    }

    public List<ItemStack> getMaxPlayersOptions() {
        List<ItemStack> options = new ArrayList<>();

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

        return options;
    }

    public List<Integer> getTeamButtonsSlots(int numberOfTeams) {
        switch (numberOfTeams) {
            case 2 -> {return new ArrayList<>(Arrays.asList(11, 15));}
        }
        return null;
    }

    public static void updatePlayerInventory(Player p) {
        QueuePlayer qp = getQueuePlayer(p);
        if (qp == null) return;
        Inventory inv = p.getInventory();
        ItemStack leaveButton = new ItemStack(leaveQueueButton);
        if (qp.getTeam() != null) {
            Queue queue = qp.getTeam().getQueue();
            if (queue.getCreator() == p.getUniqueId()) {
                changeItemTitle(leaveButton, deleteQueueButtonName);
            }
            inv.setItem(8, leaveButton);
        } else {
            inv.setItem(8, null);
        }
    }
}
