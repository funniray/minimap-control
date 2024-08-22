package com.funniray.minimap.sponge.impl;

import com.funniray.minimap.common.api.MinimapLocation;
import com.funniray.minimap.common.api.MinimapWorld;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.math.vector.Vector3d;

public class SpongeWorld implements MinimapWorld {
    private ServerWorld nativeWorld;

    public static SpongeWorld of(ServerWorld world) {
        return new SpongeWorld(world);
    }

    public SpongeWorld(ServerWorld world) {
        this.nativeWorld = world;
    }

    @Override
    public String getName() {
        return nativeWorld.key().asString();
    }

    @Override
    public String getKeyedName() {
        return getName(); // Sponge worlds are already keyed
    }

    @Override
    public MinimapLocation getLocation(double x, double y, double z) {
        return SpongeLocation.of(nativeWorld.location(new Vector3d(x,y,z)));
    }

    public ServerWorld getNativeWorld() {
        return nativeWorld;
    }
}
