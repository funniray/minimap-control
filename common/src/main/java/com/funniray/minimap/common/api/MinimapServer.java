package com.funniray.minimap.common.api;

import java.util.List;

public interface MinimapServer {
    String getMinecraftVersion();
    String getLoaderVersion();
    String getLoaderName();

    List<MinimapPlayer> getPlayers();
    List<MinimapWorld> getWorlds();
}
