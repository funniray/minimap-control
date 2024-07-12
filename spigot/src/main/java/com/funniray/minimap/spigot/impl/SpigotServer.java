package com.funniray.minimap.spigot.impl;

import com.funniray.minimap.common.api.MinimapPlayer;
import com.funniray.minimap.common.api.MinimapServer;
import com.funniray.minimap.common.api.MinimapWorld;
import com.funniray.minimap.common.version.Version;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

public class SpigotServer implements MinimapServer {
    @Override
    public Version getMinecraftVersion() {
        String[] ver = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
        if (ver.length < 3) {
            return new Version(parseInt(ver[0]), parseInt(ver[1]), 0);
        } else if (ver.length == 3) {
            return new Version(parseInt(ver[0]), parseInt(ver[1]), parseInt(ver[2]));
        } else {
            throw new RuntimeException("Cannot parse version "+Bukkit.getBukkitVersion());
        }
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
