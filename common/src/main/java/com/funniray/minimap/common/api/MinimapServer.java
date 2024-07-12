package com.funniray.minimap.common.api;

import com.funniray.minimap.common.version.Version;

import java.util.List;

public interface MinimapServer {
    Version getMinecraftVersion();
    String getLoaderVersion();
    String getLoaderName();

    List<MinimapPlayer> getPlayers();
    List<MinimapWorld> getWorlds();
}
