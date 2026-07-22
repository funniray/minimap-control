package com.funniray.minimap.spigot.scheduler;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public interface SchedulerAdapter {
    void runTask(Player player, Runnable task);

    void runTaskLater(Player player, long delayTicks, Runnable task);

    static SchedulerAdapter create(Plugin plugin) {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return new FoliaSchedulerAdapter(plugin);
        } catch (ClassNotFoundException exception) {
            return new BukkitSchedulerAdapter(plugin);
        }
    }
}