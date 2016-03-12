package com.kookykraftmc.kookylobby.gui;

import com.kookykraftmc.api.framework.KookyNetwork;
import com.kookykraftmc.api.framework.util.mc.menu.Menu;
import com.kookykraftmc.api.framework.util.mc.menu.MenuManager;
import com.kookykraftmc.kookylobby.KookyLobby;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class LobbyListMenu extends Menu {

    private final List<LobbyItem> lobbies = new ArrayList<>();

    public LobbyListMenu() {
        super(ChatColor.AQUA + "Lobby Menu", 9);
        KookyNetwork.getInstance().registerMenu(KookyLobby.getInstance(), this);
    }

    public void click(Player player, ClickType clickType, int i, ItemStack itemStack) {
        if (i < lobbies.size()) {
            LobbyItem l = lobbies.get(i);
            if (l.isOnline()) {
                //TODO - SERVER SENDING
            } else {
                TextComponent c = new TextComponent("This lobby is offline!");
                c.setColor(ChatColor.RED);
                c.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.DARK_RED + "You cannot join that server")));
                player.spigot().sendMessage(c);
            }
        }
    }

    public void setLobbies(Collection<LobbyItem> lobbies) {
        this.lobbies.clear();
        this.lobbies.addAll(lobbies);
        Collections.sort(this.lobbies, new Comparator<LobbyItem>() {
            public int compare(LobbyItem o1, LobbyItem o2) {
                return o1.getId() - o2.getId();
            }
        });
        this.inventory = Bukkit.createInventory(this, MenuManager.getRoundedInventorySize(lobbies.size()), ChatColor.AQUA + "Lobby Menu");
        update();
    }

    public ItemStack[] generate() {
        ItemStack[] stack = new ItemStack[getInventory().getSize()];
        int i = 0;
        for (LobbyItem l : lobbies) {
            stack[i] = l.getItem();
            i++;
        }
        return stack;
    }

}
