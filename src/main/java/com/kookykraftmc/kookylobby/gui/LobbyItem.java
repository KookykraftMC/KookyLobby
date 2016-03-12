package com.kookykraftmc.kookylobby.gui;

import com.kookykraftmc.api.framework.util.mc.items.ItemStackBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LobbyItem {

    private int online, id;

    public LobbyItem(int online, int id) {
        this.online = online;
        this.id = id;
    }

    public int getOnline() {
        if (!isOnline()) {
            return 0;
        }
        return online;
    }

    public boolean isOnline() {
        return this.online != -1;
    }

    public int getId() {
        return id;
    }

    public ItemStack getItem() {
        ItemStackBuilder builder = new ItemStackBuilder(Material.WOOL);
        DyeColor color;
        List<String> lore;
        if (isOnline()) {
            color = DyeColor.GREEN;
            lore = Arrays.asList(ChatColor.BLUE + "There are currently " + ChatColor.AQUA + String.valueOf(getOnline()) + ChatColor.BLUE + " people online in this hub", ChatColor.GREEN + "Click to join");
        } else {
            color = DyeColor.RED;
            lore = Collections.singletonList(ChatColor.RED + "This hub is currently offline");
        }
        builder.withColor(color);
        builder.withLore(lore);
        return builder.build();
    }

}
