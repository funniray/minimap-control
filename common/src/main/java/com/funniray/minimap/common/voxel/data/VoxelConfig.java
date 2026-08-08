package com.funniray.minimap.common.voxel.data;

import com.funniray.minimap.common.api.MinimapPlayer;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class VoxelConfig {
    public boolean radarAllowed = false;
    public boolean radarMobsAllowed = false;
    public boolean radarPlayersAllowed = false;
    public boolean cavesAllowed = false;
    public String teleportCommand = "tp %p %x %y %z";

    private boolean getOverride(String nodeSuffix, boolean def, MinimapPlayer player) {
        // Require an explicitly set node so OP / '*' does not force features back on.
        String enabled = "minimap.override." + nodeSuffix + ".enabled";
        String disabled = "minimap.override." + nodeSuffix + ".disabled";
        if (player.isPermissionSet(enabled) && player.hasPermission(enabled)) {
            return true;
        } else if (player.isPermissionSet(disabled) && player.hasPermission(disabled)) {
            return false;
        }

        return def;
    }

    public VoxelConfig applyOverrides(MinimapPlayer player) {
        VoxelConfig newConfig = new VoxelConfig();
        newConfig.radarAllowed = getOverride("radar", this.radarAllowed, player);
        newConfig.radarMobsAllowed = getOverride("radar-mobs", this.radarMobsAllowed, player);
        newConfig.radarPlayersAllowed = getOverride("radar-players", this.radarPlayersAllowed, player);
        newConfig.cavesAllowed = getOverride("cave-mode", this.cavesAllowed, player);

        return newConfig;
    }
}
