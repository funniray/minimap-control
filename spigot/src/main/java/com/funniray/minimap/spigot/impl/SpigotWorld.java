package com.funniray.minimap.spigot.impl;

import com.funniray.minimap.common.api.MinimapLocation;
import com.funniray.minimap.common.api.MinimapWorld;
import org.bukkit.Location;
import org.bukkit.World;

public class SpigotWorld implements MinimapWorld {
    private World nativeWorld;

    public SpigotWorld(World nativeWorld) {
        this.nativeWorld = nativeWorld;
    }

    @Override
    public String getName() {
        return nativeWorld.getName();
    }

    @Override
    public MinimapLocation getLocation(double x, double y, double z) {
        Location location = new Location(nativeWorld, x, y, z);

        return new SpigotLocation(location);
    }
}
