package io.github.steaf23.bingoreloaded.gui.cards;

import io.github.steaf23.bingoreloaded.BingoGame;
import io.github.steaf23.bingoreloaded.BingoMessage;
import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.GameWorldManager;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.bingoreloaded.data.BingoCardsData;
import io.github.steaf23.bingoreloaded.data.TaskListsData;
import io.github.steaf23.bingoreloaded.data.TranslationData;
import io.github.steaf23.bingoreloaded.gui.AbstractGUIInventory;
import io.github.steaf23.bingoreloaded.event.BingoCardTaskCompleteEvent;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import io.github.steaf23.bingoreloaded.item.tasks.*;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class BingoCard extends AbstractGUIInventory implements Listener
{
    public CardSize size;
    public List<BingoTask> tasks = new ArrayList<>();

    private static final BingoTask DEFAULT_TASK = new BingoTask(new ItemTask(Material.DIRT, 1));

    public BingoCard(CardSize size)
    {
        super(9 * size.cardSize, TranslationData.translate("menu.card.title"), null);
        this.size = size;
        InventoryItem cardInfoItem = new InventoryItem(0, Material.MAP, TranslationData.itemName("menu.card.info_regular"), TranslationData.itemDescription("menu.card.info_regular"));
        addOption(cardInfoItem);

        BingoReloaded.registerListener(this);
        this.setMaxStackSizeOverride(64);
    }

    public void generateCard(String cardName, int seed)
    {
        Random shuffler;
        if (seed == 0)
        {
            shuffler = new Random();
        }
        else
        {
            shuffler = new Random(seed);
        }

        List<String> ticketList = new ArrayList<>();
        for (String listName : BingoCardsData.getListsSortedByMin(cardName))
        {
            if (TaskListsData.getTasks(listName).size() == 0) // Skip empty task lists.
            {
                continue;
            }
            for (int i = 0; i < BingoCardsData.getListMin(cardName, listName); i++)
            {
                ticketList.add(listName);
            }
        }

        List<String> overflowList = new ArrayList<>();
        for (String listName : BingoCardsData.getLists(cardName))
        {
            for (int i = 0; i < BingoCardsData.getListMax(cardName, listName) - BingoCardsData.getListMin(cardName, listName); i++)
            {
                overflowList.add(listName);
            }
        }
        Collections.shuffle(overflowList, shuffler);
        ticketList.addAll(overflowList);
        if (ticketList.size() > size.fullCardSize)
            ticketList = ticketList.subList(0, size.fullCardSize);

        List<BingoTask> newItems = new ArrayList<>();
        Map<String, List<BingoTask>> allTasks = new HashMap<>();
        for (String listName : ticketList)
        {
            if (!allTasks.containsKey(listName))
            {
                List<BingoTask> listTasks = TaskListsData.getTasks(listName);
                if (listTasks.size() == 0) // Skip empty task lists.
                {
                    continue;
                }
                Collections.shuffle(listTasks, shuffler);
                allTasks.put(listName, listTasks);
            }
            newItems.add(allTasks.get(listName).remove(allTasks.get(listName).size() - 1));
        }

        while (newItems.size() < size.fullCardSize)
        {
            newItems.add(DEFAULT_TASK.copy());
        }
        newItems = newItems.subList(0, size.fullCardSize);

        Collections.shuffle(newItems, shuffler);
        tasks = newItems;
    }

    public void showInventory(HumanEntity player)
    {
        for (int i = 0; i < tasks.size(); i++)
        {
            InventoryItem item = tasks.get(i).asStack().inSlot(size.getCardInventorySlot(i));
            addOption(item);
        }

        open(player);
    }

    public boolean hasBingo(BingoTeam team)
    {
        //check for rows and columns
        for (int y = 0; y < size.cardSize; y++)
        {
            boolean completedRow = true;
            boolean completedCol = true;
            for (int x = 0; x < size.cardSize; x++)
            {
                int indexRow = size.cardSize * y + x;
                Optional<BingoPlayer> completedBy = tasks.get(indexRow).completedBy;
                if (completedBy.isEmpty() || !team.players.contains(completedBy.get()))
                {
                    completedRow = false;
                }

                int indexCol = size.cardSize * x + y;
                completedBy = tasks.get(indexCol).completedBy;
                if (completedBy.isEmpty() || !team.players.contains(completedBy.get()))
                {
                    completedCol = false;
                }
            }

            if (completedRow || completedCol)
            {
                return true;
            }
        }

        // check for diagonals
        boolean completedDiagonal1 = true;
        for (int idx = 0; idx < size.fullCardSize; idx += size.cardSize + 1)
        {
            Optional<BingoPlayer> completedBy = tasks.get(idx).completedBy;
            if (completedBy.isEmpty() || !team.players.contains(completedBy.get()))
            {
                completedDiagonal1 = false;
                break;
            }
        }

        boolean completedDiagonal2 = true;
        for (int idx = 0; idx < size.fullCardSize; idx += size.cardSize - 1)
        {
            if (idx != 0 && idx != size.fullCardSize - 1)
            {
                Optional<BingoPlayer> completedBy = tasks.get(idx).completedBy;
                if (completedBy.isEmpty() || !team.players.contains(completedBy.get()))
                {
                    completedDiagonal2 = false;
                    break;
                }
            }
        }
        return completedDiagonal1 || completedDiagonal2;
    }

    /**
     * @param team The team.
     * @return The amount of completed items for the given team.
     */
    public int getCompleteCount(BingoTeam team)
    {
        int count = 0;
        for (var task : tasks)
        {
            if (task.completedBy.isPresent() && team.players.contains(task.completedBy.get()))
                count++;
        }

        return count;
    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
    {
        if (!size.taskSlots.contains(slotClicked))
            return;

        BingoTask task = BingoTask.fromStack(event.getCurrentItem());
        if (task == null)
            return;

        BaseComponent base = new TextComponent("\n");
        BaseComponent name = task.data.getItemDisplayName().asComponent();
        name.setBold(true);
        name.setColor(task.nameColor);

        base.addExtra(name);
        base.addExtra("\n - ");
        base.addExtra(task.data.getDescription());

        BingoMessage.sendDebug(base, player);
    }

    public BingoCard copy()
    {
        BingoCard card = new BingoCard(this.size);
        List<BingoTask> newTasks = new ArrayList<>();
        for (BingoTask slot : tasks)
        {
            newTasks.add(slot.copy());
        }
        card.tasks = newTasks;
        return card;
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event)
    {
        BingoGame game = GameWorldManager.get().getActiveGame(GameWorldManager.getWorldName(event.getWhoClicked().getWorld()));
        if (game == null || !(event.getWhoClicked() instanceof Player p))
            return;

        BingoPlayer player = game.getTeamManager().getBingoPlayer(p);
        if (player == null || !player.isInBingoWorld(game.getWorldName()))
            return;

        BingoTeam team = player.team();
        if (team == null || team.card != this)
            return;

        if (event.getSlotType() == InventoryType.SlotType.RESULT && event.getClick() != ClickType.SHIFT_LEFT)
        {
            Bukkit.getScheduler().runTask(BingoReloaded.get(), task -> {
                ItemStack resultStack = p.getItemOnCursor();
                completeItemSlot(resultStack, player, game);
            });
            return;
        }

        Bukkit.getScheduler().runTask(BingoReloaded.get(), task -> {
            for (ItemStack stack : p.getInventory().getContents())
            {
                if (stack != null)
                {
                    stack = completeItemSlot(stack, player, game);
                }
            }

            ItemStack stack = p.getItemOnCursor();
            stack = completeItemSlot(stack, player, game);
        });
    }

    @EventHandler
    public void onPlayerCollectItem(final EntityPickupItemEvent event)
    {
        BingoGame game = GameWorldManager.get().getActiveGame(GameWorldManager.getWorldName(event.getEntity().getWorld()));
        if (game == null || !(event.getEntity() instanceof Player p))
            return;

        BingoPlayer player = game.getTeamManager().getBingoPlayer(p);
        if (player == null || !player.isInBingoWorld(game.getWorldName()))
            return;

        BingoTeam team = player.team();
        if (team == null || team.card != this)
            return;

        if (team.outOfTheGame)
            return;

        ItemStack stack = event.getItem().getItemStack();
        int amount = stack.getAmount();
        stack = completeItemSlot(stack, player, game);
        if (amount != stack.getAmount())
        {
            event.setCancelled(true);
            ItemStack resultStack = stack.clone();

            Bukkit.getScheduler().runTask(BingoReloaded.get(), task -> {
                p.getWorld().dropItem(event.getItem().getLocation(), resultStack);
                event.getItem().remove();
            });
        }
    }

    @EventHandler
    public void onPlayerDroppedItem(final PlayerDropItemEvent event)
    {
        BingoGame game = GameWorldManager.get().getActiveGame(GameWorldManager.getWorldName(event.getPlayer().getWorld()));
        if (game == null)
            return;

        BingoPlayer player = game.getTeamManager().getBingoPlayer(event.getPlayer());
        if (player == null || !game.isInProgress())
            return;

        BingoTeam team = player.team();
        if (team == null || team.card != this)
            return;

        if (team.outOfTheGame)
            return;

        Bukkit.getScheduler().runTask(BingoReloaded.get(), task -> {
            ItemStack stack = event.getItemDrop().getItemStack();
            stack = completeItemSlot(stack, player, game);
        });
    }

    @EventHandler
    public void onPlayerAdvancementDone(final PlayerAdvancementDoneEvent event)
    {
        BingoGame game = GameWorldManager.get().getActiveGame(GameWorldManager.getWorldName(event.getPlayer().getWorld()));
        if (game == null)
            return;

        BingoPlayer player = game.getTeamManager().getBingoPlayer(event.getPlayer());
        if (player == null || !game.isInProgress())
            return;

        BingoTeam team = player.team();
        if (team == null || team.card != this)
            return;

        if (team.outOfTheGame)
            return;

        if (game.getSettings().deathMatchItem != null)
            return;

        for (BingoTask task : tasks)
        {
            if (task.type != BingoTask.TaskType.ADVANCEMENT)
                continue;

            AdvancementTask data = (AdvancementTask) task.data;

            if (data.advancement().equals(event.getAdvancement()))
            {
                if (!task.complete(player, game.getGameTime()))
                    continue;

                var slotEvent = new BingoCardTaskCompleteEvent(task, player, hasBingo(team));
                Bukkit.getPluginManager().callEvent(slotEvent);
                break;
            }
        }
    }

    ItemStack completeItemSlot(ItemStack item, BingoPlayer player, BingoGame game)
    {
        if (game.getSettings().deathMatchItem != null)
        {
            if (item.getType() == game.getSettings().deathMatchItem)
            {
                var slotEvent = new BingoCardTaskCompleteEvent(null, player, true);
                Bukkit.getPluginManager().callEvent(slotEvent);
            }
            return item;
        }

        for (BingoTask task : tasks)
        {
            if (task.type != BingoTask.TaskType.ITEM)
                continue;

            ItemTask data = (ItemTask)task.data;
            if (data.material().equals(item.getType()) && data.count() <= item.getAmount())
            {
                if (!task.complete(player, game.getGameTime()))
                {
                    continue;
                }
                item.setAmount(item.getAmount() - data.getCount());
                player.player().updateInventory();
                var slotEvent = new BingoCardTaskCompleteEvent(task, player, hasBingo(player.team()));
                Bukkit.getPluginManager().callEvent(slotEvent);
                break;
            }
        }
        return item;
    }
}
