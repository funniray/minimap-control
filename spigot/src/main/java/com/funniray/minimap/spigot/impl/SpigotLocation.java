package com.funniray.minimap.spigot.impl;

import com.funniray.minimap.common.api.MinimapLocation;
import com.funniray.minimap.common.api.MinimapWorld;
import org.bukkit.Location;

public class SpigotLocation implements MinimapLocation {
    private Location nativeLocation;

    public SpigotLocation(Location nativeLocation) {
        this.nativeLocation = nativeLocation;
    }

    @Override
    public double getX() {
        return nativeLocation.getX();
    }

    @Override
    public double getY() {
        return nativeLocation.getY();
    }

    @Override
    public double getZ() {
        return nativeLocation.getZ();
    }

    @Override
    public MinimapWorld getWorld() {
        return new SpigotWorld(nativeLocation.getWorld());
    }

    public Location getNativeLocation() {
        return nativeLocation;
    }
}
