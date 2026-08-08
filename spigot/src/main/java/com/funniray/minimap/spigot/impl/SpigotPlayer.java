package com.funniray.minimap.spigot.impl;

import com.funniray.minimap.common.api.MinimapLocation;
import com.funniray.minimap.common.api.MinimapPlayer;
import com.funniray.minimap.common.version.Version;
import com.funniray.minimap.spigot.SpigotMinimap;
import io.papermc.lib.PaperLib;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.UUID;

public class SpigotPlayer implements MinimapPlayer {
    private final Player nativePlayer;

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
    public void sendRawSystemMessage(String message) {
        // Match XaeroForceDisabler: tellraw keeps § markers intact for the client scanner.
        // Adventure Component.text(§...) is sanitized on many Paper builds and never triggers Xaero.
        SpigotMinimap plugin = SpigotMinimap.getInstance();
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (!nativePlayer.isOnline()) {
                return;
            }
            String escaped = message
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "tellraw " + nativePlayer.getName() + " \"" + escaped + "\"");
        });
    }

    @Override
    public void teleport(MinimapLocation location) {
        PaperLib.teleportAsync(nativePlayer, ((SpigotLocation) location).getNativeLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
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

    @Override
    public boolean isPermissionSet(String string) {
        return nativePlayer.isPermissionSet(string);
    }

    @Override
    public Version getVersion() {
        SpigotMinimap plugin = SpigotMinimap.getInstance();
        if (plugin.viaHooked) {
            return plugin.viaHook.getPlayerVersion(this);
        } else {
            return new SpigotServer().getMinecraftVersion();
        }
    }

    public Player getNativePlayer() {
        return nativePlayer;
    }
}
