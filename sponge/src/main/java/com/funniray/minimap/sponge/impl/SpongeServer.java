package com.funniray.minimap.sponge.impl;

import com.funniray.minimap.common.api.MinimapPlayer;
import com.funniray.minimap.common.api.MinimapServer;
import com.funniray.minimap.common.api.MinimapWorld;
import com.funniray.minimap.common.version.Version;
import org.spongepowered.api.Sponge;

import java.util.List;
import java.util.stream.Collectors;

public class SpongeServer implements MinimapServer {

    @Override
    public Version getMinecraftVersion() {
//        return Sponge.platform().minecraftVersion().name();
        return new Version(1,16,5);
    }

    @Override
    public String getLoaderVersion() {
        return "8.0.0"; // Unsure how to do this programmatically
    }

    @Override
    public String getLoaderName() {
        return "Sponge";
    }

    @Override
    public List<MinimapPlayer> getPlayers() {
        return Sponge.server().onlinePlayers().stream()
                .map(SpongePlayer::new)
                .map(MinimapPlayer.class::cast)
                .collect(Collectors.toList());
    }

    @Override
    public List<MinimapWorld> getWorlds() {
        return Sponge.server().worldManager().worlds().stream()
                .map(SpongeWorld::new)
                .map(MinimapWorld.class::cast)
                .collect(Collectors.toList());
    }
}
