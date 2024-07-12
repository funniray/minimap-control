package com.funniray.minimap.spigot;

import com.funniray.minimap.common.api.MinimapPlayer;
import com.funniray.minimap.common.version.Version;
import com.funniray.minimap.spigot.impl.SpigotPlayer;
import com.funniray.minimap.spigot.impl.SpigotServer;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.ViaAPI;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.protocol.version.VersionType;

import static java.lang.Integer.parseInt;

public class ViaHook {
    private final ViaAPI api;

    public ViaHook() throws ClassNotFoundException {
        api = Via.getAPI();
    }

    public Version getPlayerVersion(MinimapPlayer p) {
        int protoVersion = api.getPlayerVersion(((SpigotPlayer)p).getNativePlayer());
        ProtocolVersion version = ProtocolVersion.getProtocol(protoVersion);

        if (version.getVersionType() == VersionType.SPECIAL) {
            return new SpigotServer().getMinecraftVersion();
        }

        String[] ver = version.getName().replaceAll("x","0").split("-")[0].split("\\.");
        if (ver.length < 3) {
            return new Version(parseInt(ver[0]), parseInt(ver[1]), 0);
        } else if (ver.length == 3) {
            return new Version(parseInt(ver[0]), parseInt(ver[1]), parseInt(ver[2]));
        } else {
            throw new RuntimeException("Cannot parse version "+ version.getName());
        }
    }
}
