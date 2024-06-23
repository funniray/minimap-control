package com.funniray.minimap.sponge;

import com.funniray.minimap.common.JavaMinimapPlugin;
import com.funniray.minimap.common.api.MinimapServer;
import com.funniray.minimap.sponge.impl.SpongePlayer;
import com.funniray.minimap.sponge.impl.SpongeServer;
import com.funniray.minimap.sponge.impl.SpongeWorld;
import com.google.inject.Inject;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.LinearComponents;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.ChangeEntityWorldEvent;
import org.spongepowered.api.event.lifecycle.*;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;
import org.spongepowered.api.network.channel.raw.RawDataChannel;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import java.io.File;
import java.nio.file.Path;

/**
 * The main class of your Sponge plugin.
 *
 * <p>All methods are optional -- some common event registrations are included as a jumping-off point.</p>
 */
@Plugin("minimap")
public class SpongeMinimap extends JavaMinimapPlugin {

    private final PluginContainer container;
    private final Logger logger;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path defaultConfig;

    @Inject
    SpongeMinimap(final PluginContainer container, final Logger logger) {
        this.container = container;
        this.logger = logger;
    }

    @Listener
    public void onServerStarting(final StartedEngineEvent<Server> event) {
        // Any setup per-game instance. This can run multiple times when
        // using the integrated (singleplayer) server.
        enableSelf();
    }

    @Listener
    public void onServerStopping(final StoppingEngineEvent<Server> event) {
        // Any tear down per-game instance. This can run multiple times when
        // using the integrated (singleplayer) server.
        disableSelf();
    }

    @Listener
    public void onJoin(final ServerSideConnectionEvent.Join event) {
        handlePlayerJoined(SpongePlayer.of(event.player()));
    }

    @Listener
    public void onLeave(final ServerSideConnectionEvent.Disconnect event) {
        handlePlayerLeft(SpongePlayer.of(event.player()));
    }

    @Listener
    public void onSwitchWorld(final ChangeEntityWorldEvent event) {
        if (event.entity() instanceof ServerPlayer) {
            handleSwitchWorld(SpongeWorld.of(event.destinationWorld()), SpongePlayer.of((ServerPlayer) event.entity()));
        }
    }

    @Override
    public MinimapServer getServer() {
        return new SpongeServer();
    }

    @Override
    public ConfigurationLoader<CommentedConfigurationNode> getConfigLoader() {
        if (!defaultConfig.toFile().exists()) defaultConfig.toFile().mkdirs();
        return YamlConfigurationLoader.builder()
                .defaultOptions(opts -> opts.shouldCopyDefaults(true))
                .path(defaultConfig.resolve("config.yml"))
                .build();
    }

    @Override
    public void registerChannel(String channel) {
        String[] key = channel.split(":");
        Sponge.channelManager().ofType(ResourceKey.of(key[0], key[1]), RawDataChannel.class)
                .play()
                .addHandler(new SpongeMessageListener(this, channel));
    }
}
