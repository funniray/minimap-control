package com.funniray.minimap.common.xaeros;

import com.funniray.minimap.common.api.MinimapPlayer;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class XaerosConfig {
    public boolean caveMode = false;
    public boolean netherCaveMode = false;
    public boolean radar = false;

    private boolean getOverride(String nodeSuffix, boolean def, MinimapPlayer player) {
        // Require an explicitly set node so OP / '*' does not force cave/radar back on.
        String enabled = "minimap.override." + nodeSuffix + ".enabled";
        String disabled = "minimap.override." + nodeSuffix + ".disabled";
        if (player.isPermissionSet(enabled) && player.hasPermission(enabled)) {
            return true;
        } else if (player.isPermissionSet(disabled) && player.hasPermission(disabled)) {
            return false;
        }

        return def;
    }

    public XaerosConfig applyOverrides(MinimapPlayer player) {
        XaerosConfig newConfig = new XaerosConfig();
        newConfig.radar = getOverride("radar", this.radar, player);
        newConfig.caveMode = getOverride("cave-mode", this.caveMode, player);
        newConfig.netherCaveMode = getOverride("nether-cave-mode", this.netherCaveMode, player);

        return newConfig;
    }
}
