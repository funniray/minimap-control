package com.funniray.minimap.spigot.impl;

import com.funniray.minimap.common.api.MinimapLocation;
import com.funniray.minimap.common.api.MinimapWorld;
import org.bukkit.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SpigotWorld implements MinimapWorld {
    private World nativeWorld;

    public SpigotWorld(World nativeWorld) {
        this.nativeWorld = nativeWorld;
    }

    @Override
    public String getName() {
        return nativeWorld.getName();
    }

    public String getKeyedName() {
        try {
            Method getKey = nativeWorld.getClass().getMethod("getKey");
            NamespacedKey key = (NamespacedKey) getKey.invoke(nativeWorld);
            return key.toString();
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            return getName();
        }
    }

    @Override
    public MinimapLocation getLocation(double x, double y, double z) {
        Location location = new Location(nativeWorld, x, y, z);

        return new SpigotLocation(location);
    }
}
