package com.kookykraftmc.kookylobby;

import com.google.common.collect.ImmutableMap;
import com.kookykraftmc.api.framework.KookyNetwork;
import com.kookykraftmc.api.framework.plugin.KookyAddon;
import com.kookykraftmc.api.framework.plugin.KookyRunnable;
import com.kookykraftmc.api.framework.util.mc.chat.ChatColorAppend;
import com.kookykraftmc.api.framework.util.mc.items.ItemStackBuilder;
import com.kookykraftmc.api.framework.util.mc.world.LocationObject;
import com.kookykraftmc.api.framework.util.mc.world.VoidWorldGenerator;
import com.kookykraftmc.api.global.kookypackets.messaging.messages.handshake.JoinableUpdate;
import com.kookykraftmc.api.global.file.DownloadUtil;
import com.kookykraftmc.api.global.file.FileUTIL;
import com.kookykraftmc.api.global.file.SSLUtil;
import com.kookykraftmc.api.global.sql.SQLUtil;
import com.kookykraftmc.api.global.type.ServerType;
import com.kookykraftmc.kookylobby.cosmetics.CosmeticsManager;
import com.kookykraftmc.kookylobby.gui.CompassItem;
import com.kookykraftmc.kookylobby.gui.LobbyCompass;
import com.kookykraftmc.kookylobby.listeners.LobbyListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class KookyLobby extends KookyAddon {

    public static KookyLobby getInstance() {
        return instance;
    }

    protected static void setInstance(KookyLobby instance) {
        KookyLobby.instance = instance;
    }

    private static KookyLobby instance;
    private final File lobbyfile = new File("Lobby");
    private final String lobbydownload = "https://www.dropbox.com/s/67i3vydf1pqsjjr/hub.zip?dl=1";
    private LobbyListener listener;
    private KookyNetwork network;
    private LobbyCompass compass;
    private CosmeticsManager manager;
    private World w;
    private LocationObject spawn = new LocationObject(0.5, 97, 0.5, 0F, 90F);

    public void onLoad() {
        network = KookyNetwork.getInstance();
    }

    public void onEnable() {
        setInstance(this);

        listener = new LobbyListener();

        try {
            SSLUtil.allowAnySSL();
        } catch (Exception e) {
            getNetwork().getLogger().log(Level.WARNING, "Could not allow any SSL", e);
        }

        File tempzip = new File("temp.zip");
        File temp = new File("temp");

        try {
            DownloadUtil.download(tempzip, lobbydownload);
        } catch (Exception e) {
            getNetwork().getLogger().log(Level.WARNING, "Could not download lobby", e);
        }

        FileUTIL.setPermissions(tempzip,true,true,true);
        FileUTIL.setPermissions(temp,true,true,true);

        try {
            FileUTIL.unZip(tempzip.getPath(),temp.getPath());
        } catch (IOException e) {
            getNetwork().getLogger().log(Level.WARNING, "Could not unzip lobby", e);
        }

        if(!tempzip.delete()){
            tempzip.deleteOnExit();
        }

        try {
            FileUTIL.copy(temp.listFiles()[0],lobbyfile);
        } catch (IOException e) {
            getNetwork().getLogger().log(Level.WARNING, "Could not copy lobby", e);
        }

        FileUTIL.deleteDir(temp);

        w = new WorldCreator(lobbyfile.getName()).generateStructures(false).generator(VoidWorldGenerator.getGenerator()).createWorld();

        Set<CompassItem> items = new HashSet<>();

        try {
            if (!SQLUtil.tableExists(network.getConnection(), "compass_items")) {
                getNetwork().getLogger().log(Level.INFO, "Creating compass-item DB");
                SQLUtil.createTable(network.getConnection(), "compass_items", new ImmutableMap.Builder<String, Map.Entry<SQLUtil.SQLDataType, Integer>>().put("name", new AbstractMap.SimpleImmutableEntry<>(SQLUtil.SQLDataType.TEXT, 32)).put("lore", new AbstractMap.SimpleImmutableEntry<>(SQLUtil.SQLDataType.TEXT, -1)).put("id", new AbstractMap.SimpleImmutableEntry<>(SQLUtil.SQLDataType.INT, 3)).put("data", new AbstractMap.SimpleImmutableEntry<>(SQLUtil.SQLDataType.INT, 1)).put("servertype", new AbstractMap.SimpleImmutableEntry<>(SQLUtil.SQLDataType.TEXT, 32)).put("slot", new AbstractMap.SimpleImmutableEntry<>(SQLUtil.SQLDataType.INT, 2)).build());
                getNetwork().endSetup("Could not find compass items DB");
            }
            ResultSet set = SQLUtil.query(network.getConnection(), "compass_items", "*", new SQLUtil.Where("1"));
            while (set.next()) {
                String name = ChatColorAppend.translate(set.getString("name"));
                String[] lore = ChatColorAppend.translate(set.getString("lore")).split(",");
                int materialid = set.getInt("id");
                int data = set.getInt("data");
                ServerType type;
                try {
                    type = ServerType.getType(set.getString("servertype"));
                } catch (IllegalArgumentException e) {
                    getNetwork().getLogger().log(Level.WARNING, "Error getting servertype", e);
                    continue;
                }
                int slot = set.getInt("slot");
                if (slot < -1 || slot > 54) {
                    getNetwork().getLogger().log(Level.WARNING, "Slot: {0} is invalid", new Object[]{slot});
                    continue;
                }
                Material m = Material.getMaterial(materialid);
                if (m == null) {
                    getNetwork().getLogger().log(Level.WARNING, "Invalid material ID: {0} ({1})", new Object[]{materialid, ChatColorAppend.wipe(name)});
                    continue;
                }
                getNetwork().getLogger().log(Level.INFO, "Registering new compassitem with slot: " + String.valueOf(slot));
                items.add(new CompassItem(slot, type, new ItemStackBuilder(m).withName(name).withLore(lore).withData(data)));
            }
            set.close();
        } catch (SQLException | ClassNotFoundException e) {
            getNetwork().getLogger().log(Level.WARNING, "Error loading compass items", e);
            getNetwork().endSetup("Could not load compass items");
        }

        compass = new LobbyCompass(items);

        registerListener(getListener());

        manager = new CosmeticsManager(getNetwork());
        manager.download();
        manager.load();
        manager.enable();

        new KookyRunnable() {
            public void run() {
                getNetwork().getLogger().log(Level.INFO, "Setting joinable...");
                try {
                    getNetwork().getPacketHub().sendMessage(getNetwork().getProxy(), new JoinableUpdate(true));
                } catch (IOException e) {
                    getNetwork().getLogger().log(Level.WARNING, "Error setting joinable", e);
                    getNetwork().endSetup("Could not set joinable");
                }
            }
        }.runTaskAsynchonrously(this);
    }

    public void onDisable() {
        Bukkit.unloadWorld(w,false);
        FileUTIL.deleteDir(lobbyfile);
        manager.disable();
        try {
            getNetwork().getPacketHub().sendMessage(getNetwork().getProxy(), new JoinableUpdate(false));
        } catch (IOException e) {
            getNetwork().getLogger().log(Level.WARNING, "Error setting unjoinable", e);
            getNetwork().endSetup("Could not set unjoinable");
        }
        setInstance(null);
        manager.unload();
        manager.clearUp();
        listener = null;
        network = null;
    }

    public LobbyListener getListener() {
        return listener;
    }

    public LobbyCompass getCompass() {
        return compass;
    }

    public KookyNetwork getNetwork() {
        return network;
    }

    public CosmeticsManager getManager() {
        return manager;
    }

    public World getW() {
        return w;
    }

    public File getLobbyfile() {
        return lobbyfile;
    }

    public String getLobbydownload() {
        return lobbydownload;
    }

    public LocationObject getSpawn() {
        return spawn;
    }

    public int getVersion() {
        return 0;
    }

    public long finishUp() {
        getNetwork().getLogger().log(Level.INFO, "Finishing up lobby");
        try {
            getNetwork().getPacketHub().sendMessage(getNetwork().getProxy(), new JoinableUpdate(false));
        } catch (IOException e) {
            getNetwork().getLogger().log(Level.WARNING, "Error setting unjoinable", e);
            getNetwork().endSetup("Could not set unjoinable");
        }
        return 60;
    }


}
