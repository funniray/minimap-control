package com.funniray.minimap.proxy;

import com.google.inject.Inject;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Plugin(
        id = "minimapcontrol-proxy",
        name = "minimapcontrol-proxy",
        version = "1.0",
        description = "Allows plugin messages for MinimapControl to work.",
        authors = {"funniray"}
)
public class VelocityPlugin {
    @Inject
    private ProxyServer server;

    public static final Logger logger = LoggerFactory.getLogger("minimapcontrol");

    private final List<ChannelIdentifier> FORWARD_CHANNEL;

    public VelocityPlugin() {
        FORWARD_CHANNEL = ChannelList.listenChannels.stream().map(MinecraftChannelIdentifier::from).collect(Collectors.toList());
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        FORWARD_CHANNEL.forEach(server.getChannelRegistrar()::register);
    }

    @Subscribe
    public void onMessage(PluginMessageEvent event) {
        if (FORWARD_CHANNEL.contains(event.getIdentifier())) {
            event.setResult(PluginMessageEvent.ForwardResult.forward());

            if (event.getSource() instanceof Player) {
                Player p = (Player) event.getSource();
                ServerConnection c = (ServerConnection) event.getTarget();
                logger.info(">>> "+ p.getUsername() + " > " + c.getServerInfo().getName() + " [" + event.getIdentifier().getId() + "]");
            } else {
                Player p = (Player) event.getTarget();
                ServerConnection c = (ServerConnection) event.getSource();
                logger.info(">>> "+ c.getServerInfo().getName() + " > " + p.getUsername() + " [" + event.getIdentifier().getId() + "]");
            }
        }
    }
}
