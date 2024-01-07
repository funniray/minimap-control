package com.funniray.minimap.common.voxel;

import com.funniray.minimap.common.JavaMinimapPlugin;
import com.funniray.minimap.common.api.MessageHandler;
import com.funniray.minimap.common.api.MinimapPlayer;
import com.funniray.minimap.common.network.NetworkUtils;
import com.funniray.minimap.common.voxel.data.VoxelConfig;
import com.funniray.minimap.common.voxel.data.VoxelWorldConfig;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;

public class VoxelHandler {
    private final JavaMinimapPlugin plugin;

    public String VOXEL_SETTINGS_CHANNEL = "voxelmap:settings";

    public VoxelHandler(JavaMinimapPlugin plugin) {
        this.plugin = plugin;
    }

    public void sendSettings(MinimapPlayer player) {
        VoxelWorldConfig worldConfig = plugin.getConfig().getWorldConfig(player.getLocation().getWorld().getName()).voxelConfig;
        VoxelConfig config = plugin.getConfig().globalVoxelConfig;
        if (worldConfig != null && worldConfig.enabled) {
            config = worldConfig;
        }

        String configJson = new Gson().toJson(config);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeByte(42);
        NetworkUtils.writeUtf(configJson, out);
        player.sendPluginMessage(out.toByteArray(), VOXEL_SETTINGS_CHANNEL);
        System.out.println("sent: "+configJson);
    }

}
