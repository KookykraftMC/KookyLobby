package com.kookykraftmc.kookylobby.gui;

import com.kookykraftmc.api.framework.KookyNetwork;
import com.kookykraftmc.api.framework.util.mc.menu.Menu;
import com.kookykraftmc.api.framework.util.mc.menu.MenuManager;
import com.kookykraftmc.kookylobby.KookyLobby;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class LobbyCompass extends Menu {

    private static int getSize(Set<CompassItem> items) {
        int currentsize = 0;
        for (CompassItem item : items) {
            if (item.getNumber() > currentsize) {
                currentsize = item.getNumber();
            }
        }
        return MenuManager.getRoundedInventorySize(currentsize);
    }

    private Set<CompassItem> items;

    public LobbyCompass(Set<CompassItem> items) {
        super(ChatColor.AQUA + "KookyKraft Network", getSize(items));
        this.items = items;
        update();
        KookyNetwork.getInstance().registerMenu(KookyLobby.getInstance(), this);
    }

    public CompassItem getItem(int i) {
        for (CompassItem item : getItems()) {
            if (item.getNumber() == i) {
                return item;
            }
        }
        return null;
    }

    public Set<CompassItem> getItems() {
        return items;
    }

    public void setItems(Set<CompassItem> items) {
        this.items = items;
        inventory = Bukkit.createInventory(this, getSize(items), ChatColor.AQUA + "BubbleNetwork");
        update();
    }

    public void click(Player player, ClickType clickType, int i, ItemStack itemStack) {
        CompassItem item = getItem(i);
        if (item != null) {
            KookyNetwork.getInstance().sendPlayer(player, item.getType());
        }
    }

    public ItemStack[] generate() {
        ItemStack[] stacks = new ItemStack[getInventory().getSize()];
        CompassItem item;
        for (int i = 0; i < getInventory().getSize(); i++) {
            item = getItem(i);
            stacks[i] = item == null ? null : item.getItem().build();
        }
        return stacks;
    }

}

