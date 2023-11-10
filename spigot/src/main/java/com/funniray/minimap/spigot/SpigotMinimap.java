package com.funniray.minimap.spigot;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;

public final class SpigotMinimap extends JavaPlugin {
    private static SpigotMinimap instance;
    private final SpigotMain main = new SpigotMain(this);

    private BukkitAudiences adventure;

    public BukkitAudiences adventure() {
        if(this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        this.adventure = BukkitAudiences.create(this);
        getServer().getPluginManager().registerEvents(main, this);
        main.enableSelf();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        main.disableSelf();
        if(this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }

    public static SpigotMinimap getInstance() {
        return instance;
    }
}
