package com.funniray.minimap.common;

import com.funniray.minimap.common.api.MinimapPlayer;
import com.funniray.minimap.common.api.MinimapWorld;
import com.funniray.minimap.common.jm.JMHandler;
import com.funniray.minimap.common.jm.data.JMConfig;
import com.funniray.minimap.common.jm.data.JMVersion;
import com.funniray.minimap.common.jm.data.JMWorldConfig;
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
            final boolean firstSave = node.empty() || node.virtual();

            config = node.get(MinimapConfig.class);
            if (config == null) {
                config = new MinimapConfig();
            }

            final String version = new JMVersion().journeymap_version.full;
            config.globalJourneymapConfig.configVersion = version;
            config.defaultWorldConfig.configVersion = version;
            // Populate missing worlds before version stamping so new entries get the version too.
            config.ensureWorldConfigs(getServer().getWorlds());
            config.getWorldConfigs().forEach((world) -> world.journeymapConfig.configVersion = version);

            // Serialize after all mutations so the written file matches the live object
            // (especially important on first save when defaults + worlds must all land together).
            node.set(MinimapConfig.class, config);
            getConfigLoader().save(node);

            if (firstSave) {
                System.out.println("[MinimapControl] Created default config with fair-play enabled.");
            }
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
        worldInfoHandler.sendPacket(player);
        if (jmHandler.isLegacy(player)) {
            jmHandler.handlePerm(player, new byte[0], "journeymap:common", 2);
        } else {
            jmHandler.handlePerm(player, new byte[0], "journeymap:perm_req", 0);
        }
    }

    @Override
    public void handlePlayerLeft(MinimapPlayer player) {
        xaerosHandler.forgetPlayer(player);
    }

    public void saveConfig() {
        if (config == null) {
            return;
        }
        try {
            final CommentedConfigurationNode node = getConfigLoader().load();
            node.set(MinimapConfig.class, config);
            getConfigLoader().save(node);
        } catch (ConfigurateException e) {
            e.printStackTrace();
        }
    }

    public JMConfig getEffectiveJMConfig(MinimapPlayer player) {
        JMWorldConfig worldConfig = this.getConfig().getWorldConfig(player.getLocation().getWorld().getName()).journeymapConfig;
        JMConfig config = this.getConfig().globalJourneymapConfig;
        if (worldConfig != null) {
            return worldConfig.applyToConfig(config);
        }
        return config;
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
