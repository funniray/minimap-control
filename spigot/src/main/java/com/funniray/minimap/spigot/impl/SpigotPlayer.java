package com.funniray.minimap.spigot.impl;

import com.funniray.minimap.common.api.MinimapLocation;
import com.funniray.minimap.common.api.MinimapPlayer;
import com.funniray.minimap.spigot.SpigotMinimap;
import net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SpigotPlayer implements MinimapPlayer {
    private Player nativePlayer;

    public SpigotPlayer(Player player) {
        nativePlayer = player;
    }

    @Override
    public void sendPluginMessage(byte[] message, String channel) {
        Bukkit.getScheduler().runTask(SpigotMinimap.getInstance(), ()->nativePlayer.sendPluginMessage(SpigotMinimap.getInstance(), channel, message));
    }

    @Override
    public void sendMessage(Component message) {
        SpigotMinimap.getInstance().adventure().player(nativePlayer).sendMessage(message);
    }

    @Override
    public void teleport(MinimapLocation location) {
        nativePlayer.teleport(((SpigotLocation) location).getNativeLocation());
    }

    @Override
    public MinimapLocation getLocation() {
        return new SpigotLocation(nativePlayer.getLocation());
    }

    @Override
    public void disconnect(Component reason) {
        nativePlayer.kickPlayer(LegacyComponentSerializer.legacy('\u00a7').serialize(reason));
    }

    @Override
    public UUID getUniqueId() {
        return nativePlayer.getUniqueId();
    }

    @Override
    public String getUsername() {
        return nativePlayer.getName();
    }

    @Override
    public boolean hasPermission(String string) {
        return nativePlayer.hasPermission(string);
    }
}
