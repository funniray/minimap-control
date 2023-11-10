package com.funniray.minimap.common.api;

public interface MinimapWorld {
    String getName();
    MinimapLocation getLocation(double x, double y, double z);
}
