package com.funniray.minimap.common.jm.data;

import com.funniray.minimap.common.JavaMinimapPlugin;

public class JMVersion {
    public VersionDetails journeymap_version = new VersionDetails();
    public String loader_version = JavaMinimapPlugin.getInstance().getServer().getLoaderVersion();
    public String loader = JavaMinimapPlugin.getInstance().getServer().getLoaderName();
    public String minecraft_version = JavaMinimapPlugin.getInstance().getServer().getMinecraftVersion().toString();

    public static class VersionDetails {
        public final String full;
        public final int major;
        public final int minor;
        public final int micro;
        public final String patch;

        public VersionDetails() {
            this(6,1,0,"-beta99");
        }

        public VersionDetails(int major, int minor, int micro, String patch) {
            this.major = major;
            this.minor = minor;
            this.micro = micro;
            this.patch = patch;
            this.full = String.format("%d.%d.%d%s",major,minor,micro,patch);
        }
    }
}
