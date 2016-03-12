package com.kookykraftmc.kookylobby.cosmetics;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.manager.MainMenuManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

//Unsafe
public class CosmeticsHook {

    private Core core;

    public CosmeticsHook(JavaPlugin plugin) {
        if (!(plugin instanceof Core)) {
            throw new IllegalArgumentException("Not UltraCosmetics");
        }
        core = (Core) plugin;
    }

    public void openMenu(Player p) {
        MainMenuManager.openMenu(p);
    }

}
