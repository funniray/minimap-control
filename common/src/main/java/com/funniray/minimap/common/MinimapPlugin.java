package com.funniray.minimap.common;

import com.funniray.minimap.common.api.MinimapPlayer;
import com.funniray.minimap.common.api.MinimapServer;
import com.funniray.minimap.common.api.MinimapWorld;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;

public interface MinimapPlugin {

    void enableSelf();
    void disableSelf();
    void onPluginMessage(String channel, MinimapPlayer player, byte[] message);

    void registerChannel(String channel);
    MinimapServer getServer();
    ConfigurationLoader<CommentedConfigurationNode> getConfigLoader();
    void handleSwitchWorld(MinimapWorld world, MinimapPlayer player);
    void handlePlayerJoined(MinimapPlayer player);
}
