package com.funniray.minimap.common.voxel.data;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class VoxelConfig {
    public boolean radarAllowed = true;
    public boolean radarMobsAllowed = true;
    public boolean radarPlayersAllowed = true;
    public boolean cavesAllowed = true;
    public String teleportCommand = "tp %p %x %y %z";
}
