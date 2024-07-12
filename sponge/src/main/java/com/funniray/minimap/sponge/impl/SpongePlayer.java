package com.funniray.minimap.sponge.impl;

import com.funniray.minimap.common.api.MinimapLocation;
import com.funniray.minimap.common.api.MinimapPlayer;
import com.funniray.minimap.common.version.Version;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.network.channel.raw.RawDataChannel;
import org.spongepowered.api.world.server.ServerLocation;

import java.util.UUID;

public class SpongePlayer implements MinimapPlayer {
    private final ServerPlayer nativePlayer;

    public static SpongePlayer of(ServerPlayer nativePlayer) {
        return new SpongePlayer(nativePlayer);
    }

    public SpongePlayer(ServerPlayer nativePlayer) {
        this.nativePlayer = nativePlayer;
    }

    @Override
    public void sendPluginMessage(byte[] message, String channel) {
        String[] key = channel.split(":");
        Sponge.channelManager().ofType(ResourceKey.of(key[0], key[1]), RawDataChannel.class)
                .play()
                .sendTo(nativePlayer, channelBuf -> channelBuf.writeBytes(message));
    }

    @Override
    public MinimapLocation getLocation() {
        return SpongeLocation.of(nativePlayer.serverLocation());
    }

    @Override
    public UUID getUniqueId() {
        return nativePlayer.uniqueId();
    }

    @Override
    public String getUsername() {
        return nativePlayer.name();
    }

    @Override
    public boolean hasPermission(String string) {
        return nativePlayer.hasPermission(string);
    }

    @Override
    public Version getVersion() {
        return new SpongeServer().getMinecraftVersion();
    }

    @Override
    public void sendMessage(Component message) {
        nativePlayer.sendMessage(message);
    }

    @Override
    public void disconnect(Component reason) {
        nativePlayer.connection().close(reason);
    }

    @Override
    public void teleport(MinimapLocation location) {
        ServerLocation nativeLoc = ((SpongeLocation) location).getNativeLocation();
        if (nativePlayer.world().equals(nativeLoc.world())) {
            nativePlayer.setLocation(nativeLoc);
        } else {
            nativePlayer.transferToWorld(nativeLoc.world(), nativeLoc.position());
        }

    }
}
