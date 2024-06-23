package com.funniray.minimap.spigot;

import com.funniray.minimap.common.JavaMinimapPlugin;
import com.funniray.minimap.common.api.MinimapServer;
import com.funniray.minimap.spigot.impl.SpigotPlayer;
import com.funniray.minimap.spigot.impl.SpigotServer;
import com.funniray.minimap.spigot.impl.SpigotWorld;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;

public class SpigotMain extends JavaMinimapPlugin implements PluginMessageListener, Listener {
    public SpigotMinimap plugin;

    public SpigotMain(SpigotMinimap plugin) {
        this.plugin = plugin;
    }

    @Override
    public void registerChannel(String channel) {
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, channel);
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, channel, this);
    }

    @Override
    public MinimapServer getServer() {
        return new SpigotServer();
    }

    @Override
    public ConfigurationLoader<CommentedConfigurationNode> getConfigLoader() {
        File defaultConfig = plugin.getDataFolder();

        if (!defaultConfig.exists()) defaultConfig.mkdirs();
        return YamlConfigurationLoader.builder()
                .defaultOptions(opts -> opts.shouldCopyDefaults(true))
                .path(defaultConfig.toPath().resolve("config.yml"))
                .build();
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
        this.onPluginMessage(channel, new SpigotPlayer(player), message);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        // The player join event is slightly too early. I unfortunately don't know an event that fires late enough for Xaeros to recognize the packet
        // If anyone knows, please let me know
        plugin.getServer().getScheduler().runTaskLater(plugin, ()->this.handlePlayerJoined(new SpigotPlayer(event.getPlayer())), 40L);
    }

    @EventHandler
    public void onLeft(PlayerQuitEvent event) {
        this.handlePlayerLeft(new SpigotPlayer(event.getPlayer()));
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        this.handleSwitchWorld(new SpigotWorld(event.getPlayer().getWorld()), new SpigotPlayer(event.getPlayer()));
    }
}
