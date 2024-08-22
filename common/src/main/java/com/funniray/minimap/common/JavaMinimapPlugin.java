package com.funniray.minimap.common;

import com.funniray.minimap.common.api.MinimapPlayer;
import com.funniray.minimap.common.api.MinimapWorld;
import com.funniray.minimap.common.jm.JMHandler;
import com.funniray.minimap.common.jm.data.JMVersion;
import com.funniray.minimap.common.voxel.VoxelHandler;
import com.funniray.minimap.common.worldinfo.WorldInfoHandler;
import com.funniray.minimap.common.xaeros.XaerosHandler;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;

import java.util.Arrays;
import java.util.List;

public abstract class JavaMinimapPlugin implements MinimapPlugin {
    private static JavaMinimapPlugin instance;

    private MinimapConfig config;

    private static final List<String> listenChannels = Arrays.asList(
            "journeymap:version",
            "journeymap:perm_req",
            "journeymap:admin_req",
            "journeymap:admin_save",
            "journeymap:teleport_req",
            "journeymap:common",
            "worldinfo:world_id",
            "xaerominimap:main",
            "xaeroworldmap:main",
            "voxelmap:settings"
    );

    private final JMHandler jmHandler = new JMHandler(this);
    private final XaerosHandler xaerosHandler = new XaerosHandler(this);
    private final WorldInfoHandler worldInfoHandler = new WorldInfoHandler(this);
    private final VoxelHandler voxelHandler = new VoxelHandler(this);

    public static JavaMinimapPlugin getInstance() {
        return instance;
    }

    @Override
    public void enableSelf() {
        instance = this;
        listenChannels.forEach(this::registerChannel);
        loadConfig();
    }

    @Override
    public void disableSelf() {
        System.out.println("Disabled");
    }

    public void loadConfig() {
        try {
            final CommentedConfigurationNode node = getConfigLoader().load();
            config = node.get(MinimapConfig.class);
            node.set(MinimapConfig.class, config);
            final String version = new JMVersion().journeymap_version.full;
            config.globalJourneymapConfig.configVersion = version;
            config.defaultWorldConfig.configVersion = version;
            config.getWorldConfigs().forEach((world)->world.journeymapConfig.configVersion = version);
            getConfigLoader().save(node);
        } catch (ConfigurateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleSwitchWorld(MinimapWorld world, MinimapPlayer player) {
        xaerosHandler.sendXaerosConfig(player);
        voxelHandler.sendSettings(player);
    }

    @Override
    public void handlePlayerJoined(MinimapPlayer player) {
        xaerosHandler.sendXaerosHandshake(player);
        xaerosHandler.sendXaerosConfig(player);
        voxelHandler.sendSettings(player);
    }

    @Override
    public void handlePlayerLeft(MinimapPlayer player) {
        jmHandler.playerLeft(player);
    }

    public void saveConfig() {
        try {
            final CommentedConfigurationNode node = getConfigLoader().load();
            node.set(MinimapConfig.class, config);
            getConfigLoader().save(node);
        } catch (ConfigurateException e) {
            e.printStackTrace();
        }
    }

    public MinimapConfig getConfig() {
        return config;
    }

    public void onPluginMessage(String channel, MinimapPlayer player, byte[] message) {
        switch (channel.split(":")[0]) {
            case "journeymap":
                jmHandler.onPluginMessage(channel, player, message);
                break;
            case "xaerominimap":
            case "xaeroworldmap":
                xaerosHandler.onPluginMessage(channel, player, message);
                break;
            case "worldinfo":
                worldInfoHandler.onPluginMessage(channel, player, message);
                break;
        }
    }
}
