package com.funniray.minimap.spigot.impl;

import com.funniray.minimap.common.api.MinimapPlayer;
import com.funniray.minimap.common.api.MinimapServer;
import com.funniray.minimap.common.api.MinimapWorld;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.stream.Collectors;

public class SpigotServer implements MinimapServer {
    @Override
    public String getMinecraftVersion() {
        return Bukkit.getBukkitVersion();
    }

    @Override
    public String getLoaderVersion() {
        return Bukkit.getVersion();
    }

    @Override
    public String getLoaderName() {
        return Bukkit.getName();
    }

    @Override
    public List<MinimapPlayer> getPlayers() {
        return Bukkit.getServer().getOnlinePlayers().stream()
                .map(SpigotPlayer::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<MinimapWorld> getWorlds() {
        return Bukkit.getWorlds().stream()
                .map(SpigotWorld::new)
                .collect(Collectors.toList());
    }
}
