package com.funniray.minimap.common.api;

import com.funniray.minimap.common.version.Version;
import net.kyori.adventure.text.Component;

import java.util.UUID;

public interface MinimapPlayer {
    void sendPluginMessage(byte[] message, String channel);
    void sendMessage(Component message);
    void teleport(MinimapLocation location);
    MinimapLocation getLocation();
    void disconnect(Component reason);

    UUID getUniqueId();
    String getUsername();
    boolean hasPermission(String string);
    Version getVersion();
}
