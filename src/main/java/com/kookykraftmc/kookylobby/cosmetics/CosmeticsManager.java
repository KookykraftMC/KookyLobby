package com.kookykraftmc.kookylobby.cosmetics;

import com.kookykraftmc.api.framework.KookyNetwork;
import com.kookykraftmc.api.global.file.DownloadUtil;
import com.kookykraftmc.api.global.file.FileUTIL;
import com.kookykraftmc.api.global.file.SSLUtil;
import com.kookykraftmc.kookylobby.KookyLobby;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

public class CosmeticsManager {

    private static final String PluginURL = "https://www.dropbox.com/s/sj5fnecvumyt5kx/UltraCosmetics.jar?dl=1";
    private final File file;
    private final File jar;
    private KookyNetwork network;
    private JavaPlugin cosmetics = null;

    public CosmeticsManager(KookyNetwork network) {
        this.network = network;
        file = new File(KookyLobby.getInstance().getLoader().getJar().getParent(), "UltraCosmetics");
        jar = new File(file, "UltraCosmetics.jar");
    }

    public File getFile() {
        return file;
    }

    public File getJar() {
        return jar;
    }

    public void download() {
        if (!file.isDirectory()) {
            file.delete();
        }
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            SSLUtil.allowAnySSL();
        } catch (Exception e) {
            network.getLogger().log(Level.WARNING, "Could not allow any SSL", e);
        }
        try {
            DownloadUtil.download(jar, PluginURL);
        } catch (Exception e) {
            network.getLogger().log(Level.WARNING, "Could not download UltraCosmetics");
        }
    }

    public void load() {
        if (!jar.exists()) {
            throw new IllegalArgumentException("Jar doesn't exist");
        }
        cosmetics = network.getPlugman().load(jar);
    }

    public void enable() {
        network.getPlugman().enable(cosmetics);
    }

    public void disable() {
        network.getPlugman().disable(cosmetics);
    }

    public void unload() {
        network.getPlugman().unload(cosmetics);
        cosmetics = null;
    }

    public void clearUp() {
        FileUTIL.deleteDir(file);
    }

    public Unsafe unsafe() {
        return new Unsafe() {
            public CosmeticsHook create() {
                if (cosmetics == null || !cosmetics.isEnabled()) {
                    throw new IllegalArgumentException("Can only hook when enabled");
                }
                return new CosmeticsHook(cosmetics);
            }
        };
    }

    interface Unsafe {
        CosmeticsHook create();
    }

}
