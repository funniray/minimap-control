package com.funniray.minimap.sponge;

import com.funniray.minimap.sponge.impl.SpongePlayer;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.network.EngineConnection;
import org.spongepowered.api.network.ServerPlayerConnection;
import org.spongepowered.api.network.channel.ChannelBuf;
import org.spongepowered.api.network.channel.raw.play.RawPlayDataHandler;

public class SpongeMessageListener implements RawPlayDataHandler<EngineConnection> {
    private final SpongeMinimap plugin;
    private final String channel;

    public SpongeMessageListener(SpongeMinimap plugin, String channel) {
        this.plugin = plugin;
        this.channel = channel;
    }

    @Override
    public void handlePayload(ChannelBuf data, EngineConnection connection) {
        if (connection instanceof ServerPlayerConnection) {
            plugin.onPluginMessage(channel, SpongePlayer.of(((ServerPlayerConnection) connection).player()), data.readBytes(data.capacity()));
        }
    }
}
