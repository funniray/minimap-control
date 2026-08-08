package com.funniray.minimap.common.api;

import com.funniray.minimap.common.version.Version;
import net.kyori.adventure.text.Component;

import java.util.UUID;

public interface MinimapPlayer {
    void sendPluginMessage(byte[] message, String channel);
    void sendMessage(Component message);
    /**
     * Sends a system chat message with the exact raw string preserved on the wire.
     * Required for Xaero fair-play markers (section-sign keywords); Adventure components
     * often sanitize {@code §} and break detection.
     */
    void sendRawSystemMessage(String message);
    void teleport(MinimapLocation location);
    MinimapLocation getLocation();
    void disconnect(Component reason);

    UUID getUniqueId();
    String getUsername();
    boolean hasPermission(String string);
    /**
     * True only when the permission is explicitly set on the player (not via OP/{@code *} alone).
     */
    boolean isPermissionSet(String string);
    Version getVersion();
}
