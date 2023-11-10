package com.funniray.minimap.sponge.impl;

import com.funniray.minimap.common.api.MinimapLocation;
import com.funniray.minimap.common.api.MinimapWorld;
import org.spongepowered.api.world.server.ServerLocation;

public class SpongeLocation implements MinimapLocation {

    private ServerLocation nativeLocation;

    public static SpongeLocation of(ServerLocation nativeLocation) {
        return new SpongeLocation(nativeLocation);
    }

    public SpongeLocation(ServerLocation nativeLocation) {
        this.nativeLocation = nativeLocation;
    }

    @Override
    public double getX() {
        return nativeLocation.x();
    }

    @Override
    public double getY() {
        return nativeLocation.y();
    }

    @Override
    public double getZ() {
        return nativeLocation.z();
    }

    @Override
    public MinimapWorld getWorld() {
        return SpongeWorld.of(nativeLocation.world());
    }

    public ServerLocation getNativeLocation() {
        return nativeLocation;
    }
}
