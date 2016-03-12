package com.kookykraftmc.kookylobby.listeners;

import com.kookykraftmc.api.framework.util.mc.items.ItemStackBuilder;
import com.kookykraftmc.kookylobby.KookyLobby;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class LobbyListener implements Listener {

    protected static final int COMPASSSLOT = 0;

    private ItemStackBuilder compass = new ItemStackBuilder(Material.COMPASS).withAmount(1).withName(ChatColor.AQUA + "Compass").withLore(ChatColor.GRAY + "Right-click to open up the server-menu!", ChatColor.GRAY + "You can access any gamemode");

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        LobbyScoreboard.createBoard(p);
        p.setGameMode(GameMode.ADVENTURE);
        p.setLevel(0);
        p.setFoodLevel(20);
        p.setHealth(20);
        p.getInventory().setArmorContents(new ItemStack[4]);
        p.getInventory().setContents(generateInventory());
        p.teleport(KookyLobby.getInstance().getSpawn().toLocation(KookyLobby.getInstance().getW()));
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e){
        switch (e.getSpawnReason()){
            case NATURAL:
            case REINFORCEMENTS:
            case NETHER_PORTAL:
            case SPAWNER:
            case LIGHTNING:
            case INFECTION:
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        LobbyScoreboard.removeBoard(e.getPlayer().getUniqueId());
    }

    public ItemStack[] generateInventory() {
        ItemStack[] is = new ItemStack[4 * 9];
        is[COMPASSSLOT] = compass.build();
        return is;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockDamage(BlockDamageEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockCanBuild(BlockCanBuildEvent e) {
        e.setBuildable(false);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlaceMultiple(BlockMultiPlaceEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onItemDrop(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onItemPickup(PlayerPickupItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteractMedium(PlayerInteractEvent e) {
        int slot = e.getPlayer().getInventory().getHeldItemSlot();
        if (e.getAction() != Action.LEFT_CLICK_AIR && e.getAction() != Action.LEFT_CLICK_BLOCK) {
            if (slot == COMPASSSLOT) {
                KookyLobby.getInstance().getCompass().show(e.getPlayer());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerFoodLevelChange(FoodLevelChangeEvent e) {
        e.setFoodLevel(20);
    }

}
