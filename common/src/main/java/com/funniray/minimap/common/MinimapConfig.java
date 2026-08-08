package com.funniray.minimap.common;

import com.funniray.minimap.common.api.MinimapWorld;
import com.funniray.minimap.common.jm.data.JMConfig;
import com.funniray.minimap.common.jm.data.JMWorldConfig;
import com.funniray.minimap.common.voxel.data.VoxelConfig;
import com.funniray.minimap.common.voxel.data.VoxelWorldConfig;
import com.funniray.minimap.common.xaeros.XaerosConfig;
import com.funniray.minimap.common.xaeros.XaerosWorldConfig;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@ConfigSerializable
public class MinimapConfig {
    public String worldId = UUID.randomUUID().toString();
    public JMConfig globalJourneymapConfig = new JMConfig();
    public XaerosConfig globalXaerosConfig = new XaerosConfig();
    @Comment("Xaero's Minimap 25.3.0 and above ignore the config packet this plugin sends.\n" +
            "With this enabled, fair-play is additionally enforced through the system message markers\n" +
            "documented by Xaero, which work on every mod version.\n" +
            "Those markers cannot separate cave mode from the entity radar, so disabling either one\n" +
            "turns off both. Players also receive a blank chat line whenever the markers are applied.")
    public boolean xaerosSystemMessages = true;
    @Comment("Only supports VoxelMap-Updated. See: https://github.com/funniray/minimap-control/issues/1")
    public VoxelConfig globalVoxelConfig = new VoxelConfig();
    public JMWorldConfig defaultWorldConfig = new JMWorldConfig();
    private Map<String, WorldConfig> worlds = new LinkedHashMap<>();

    /**
     * Adds any server worlds that are missing from the map. Does not save.
     *
     * @return true if at least one world entry was created
     */
    public boolean ensureWorldConfigs(Collection<? extends MinimapWorld> serverWorlds) {
        boolean added = false;
        for (MinimapWorld world : serverWorlds) {
            if (worlds.putIfAbsent(world.getName(), new WorldConfig()) == null) {
                added = true;
            }
        }
        return added;
    }

    public WorldConfig getWorldConfig(String world) {
        WorldConfig conf = worlds.get(world);

        if (conf == null) {
            conf = new WorldConfig();
            worlds.put(world, conf);
            JavaMinimapPlugin plugin = JavaMinimapPlugin.getInstance();
            // Avoid saving while loadConfig is still building the initial config object.
            if (plugin != null && plugin.getConfig() == this) {
                plugin.saveConfig();
            }
        }

        return conf;
    }

    public Collection<WorldConfig> getWorldConfigs() {
        return worlds.values();
    }

    @ConfigSerializable
    public static class WorldConfig {
        public String worldId = UUID.randomUUID().toString();
        public JMWorldConfig journeymapConfig = new JMWorldConfig();
        public XaerosWorldConfig xaerosConfig = new XaerosWorldConfig();
        public VoxelWorldConfig voxelConfig = new VoxelWorldConfig();
    }
}
