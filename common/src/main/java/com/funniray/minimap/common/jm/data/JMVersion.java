package com.funniray.minimap.common.jm.data;

import com.funniray.minimap.common.JavaMinimapPlugin;

public class JMVersion {
    public static VersionDetails journeymap_version = new VersionDetails();
    public String loader_version = JavaMinimapPlugin.getInstance().getServer().getLoaderVersion();
    public String loader = JavaMinimapPlugin.getInstance().getServer().getLoaderName();
    public String minecraft_version = JavaMinimapPlugin.getInstance().getServer().getMinecraftVersion();

    public static class VersionDetails {
        public String full;
        public int major = 5;
        public int minor = 9;
        public int micro = 15;
        public String patch = null;

        public VersionDetails() {
            this.full = String.format("%d.%d.%d",major,minor,micro);
        }
    }
}
