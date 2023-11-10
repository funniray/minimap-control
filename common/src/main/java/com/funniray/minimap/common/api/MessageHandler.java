package com.funniray.minimap.common.api;

public interface MessageHandler {
    void onPluginMessage(String channel, MinimapPlayer player, byte[] message);
}
