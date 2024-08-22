package com.funniray.minimap.common;

import com.funniray.minimap.common.api.MinimapPlayer;
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
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@ConfigSerializable
public class MinimapConfig {
    public UUID worldId = UUID.randomUUID();
    public JMConfig globalJourneymapConfig = new JMConfig();
    public XaerosConfig globalXaerosConfig = new XaerosConfig();
    @Comment("Only supports VoxelMap-Updated. See: https://github.com/funniray/minimap-control/issues/1")
    public VoxelConfig globalVoxelConfig = new VoxelConfig();
    public JMWorldConfig defaultWorldConfig = new JMWorldConfig();
    private Map<String, WorldConfig> worlds = JavaMinimapPlugin.getInstance().getServer().getWorlds().stream()
            .map(MinimapWorld::getName)
            .collect(Collectors.toMap(s->s, s->new WorldConfig()));

    public WorldConfig getWorldConfig(String world) {
        WorldConfig conf = worlds.get(world);

        if (conf == null) {
            conf = new WorldConfig();
            worlds.put(world, conf);
            JavaMinimapPlugin.getInstance().saveConfig();
        }

        return conf;
    }

    public Collection<WorldConfig> getWorldConfigs() {
        return worlds.values();
    }

    @ConfigSerializable
    public static class WorldConfig {
        public UUID worldId = UUID.randomUUID();
        public JMWorldConfig journeymapConfig = new JMWorldConfig();
        public XaerosWorldConfig xaerosConfig = new XaerosWorldConfig();
        public VoxelWorldConfig voxelConfig = new VoxelWorldConfig();
    }
}
