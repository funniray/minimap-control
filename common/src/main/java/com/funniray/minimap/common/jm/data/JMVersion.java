package com.funniray.minimap.common.jm.data;

import com.funniray.minimap.common.JavaMinimapPlugin;

public class JMVersion {
    public VersionDetails journeymap_version = new VersionDetails();
    public String loader_version = JavaMinimapPlugin.getInstance().getServer().getLoaderVersion();
    public String loader = JavaMinimapPlugin.getInstance().getServer().getLoaderName();
    public String minecraft_version = JavaMinimapPlugin.getInstance().getServer().getMinecraftVersion().toString();

    public static class VersionDetails {
        public String full;
        public int major = 6;
        public int minor = 1;
        public int micro = 0;
        public String patch = "-beta";

        public VersionDetails() {
            this.full = String.format("%d.%d.%d%s",major,minor,micro,patch);
        }
    }
}
