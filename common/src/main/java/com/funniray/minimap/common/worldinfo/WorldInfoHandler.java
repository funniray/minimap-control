package com.funniray.minimap.common.worldinfo;

import com.funniray.minimap.common.JavaMinimapPlugin;
import com.funniray.minimap.common.api.MessageHandler;
import com.funniray.minimap.common.api.MinimapPlayer;
import com.funniray.minimap.common.network.NetworkUtils;
import com.funniray.minimap.common.version.Version;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.util.UUID;

public class WorldInfoHandler implements MessageHandler {
    private final JavaMinimapPlugin plugin;

    public static String WORLDINFO_CHANNEL = "worldinfo:world_id";

    public WorldInfoHandler(JavaMinimapPlugin plugin) {
        this.plugin = plugin;
    }

    public void sendPacket(MinimapPlayer player) {
        UUID worldId;
        // The entire server gets it's own worldId on 1.13
        // worldId on 1.12 and before is per-dimension, as bukkit reuses dimension IDs
        if (player.getVersion().greaterThanEqual(new Version(1,13,0))) {
            worldId = plugin.getConfig().worldId;
        } else {
            worldId = plugin.getConfig().getWorldConfig(player.getLocation().getWorld().getName()).worldId;
        }
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeByte(0);
        out.writeByte(42);
        NetworkUtils.writeUtf(worldId.toString(), out);
        player.sendPluginMessage(out.toByteArray(), WORLDINFO_CHANNEL);
    }

    @Override
    public void onPluginMessage(String channel, MinimapPlayer player, byte[] message) {
        sendPacket(player);
    }
}
