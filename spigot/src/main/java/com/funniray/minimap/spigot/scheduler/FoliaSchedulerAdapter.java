package com.funniray.minimap.spigot.scheduler;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class FoliaSchedulerAdapter implements SchedulerAdapter {
    private final Plugin plugin;

    public FoliaSchedulerAdapter(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void runTask(Player player, Runnable task) {
        schedule(player, task, 1L);
    }

    @Override
    public void runTaskLater(Player player, long delayTicks, Runnable task) {
        schedule(player, task, delayTicks);
    }

    private void schedule(Player player, Runnable task, long delayTicks) {
        try {
            Method getScheduler = player.getClass().getMethod("getScheduler");
            Object entityScheduler = getScheduler.invoke(player);

            Method execute = entityScheduler.getClass().getMethod(
                    "execute",
                    Plugin.class,
                    Runnable.class,
                    Runnable.class,
                    long.class
            );

            execute.invoke(entityScheduler, plugin, task, null, delayTicks);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            throw new IllegalStateException("Unable to schedule a task on Folia", exception);
        }
    }
}