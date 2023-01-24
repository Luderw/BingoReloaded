package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.data.TranslationData;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.Map;

public class SubMenuUI extends AbstractGUIInventory
{
    private static final InventoryItem BACK = new InventoryItem(0, Material.BARRIER, TranslationData.itemName("menu.prev"));
    private final Map<InventoryItem, AbstractGUIInventory> options;

    public SubMenuUI(String title, AbstractGUIInventory parent)
    {
        super(54, title, parent);
        this.options = new HashMap<>();
    }

    public SubMenuUI addMenuOption(InventoryItem button, AbstractGUIInventory option)
    {
        options.put(button, option);
        addOption(button);
        return this;
    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
    {
        if (slotClicked == BACK.getSlot())
        {
            close(player);
            return;
        }

        for (var option : options.entrySet())
        {
            if (option.getKey().getSlot() == slotClicked)
            {
                option.getValue().open(player);
                return;
            }
        }
    }
}