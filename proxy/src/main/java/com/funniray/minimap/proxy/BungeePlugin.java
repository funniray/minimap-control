package com.funniray.minimap.proxy;

import net.md_5.bungee.api.plugin.Plugin;

public class BungeePlugin extends Plugin {

    @Override
    public void onEnable() {
        ChannelList.listenChannels.forEach(channel -> getProxy().registerChannel(channel));
    }

}
