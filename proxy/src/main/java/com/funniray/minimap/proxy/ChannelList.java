package com.funniray.minimap.proxy;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class ChannelList {
    protected static final List<String> listenChannels = Arrays.asList(
            "journeymap:version",
            "journeymap:perm_req",
            "journeymap:admin_req",
            "journeymap:admin_save",
            "journeymap:teleport_req",
            "journeymap:common",
            "worldinfo:world_id",
            "xaerominimap:main",
            "xaeroworldmap:main",
            "voxelmap:settings"
    );

    protected static byte[] asBytes() {
        return String.join("\0", ChannelList.listenChannels).getBytes(StandardCharsets.UTF_8);
    }
}
