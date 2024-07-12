package com.funniray.minimap.common.xaeros;

import com.funniray.minimap.common.JavaMinimapPlugin;
import com.funniray.minimap.common.api.MessageHandler;
import com.funniray.minimap.common.api.MinimapPlayer;
import com.funniray.minimap.common.version.Version;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.io.IOException;

public class XaerosHandler implements MessageHandler {
    private final JavaMinimapPlugin plugin;

    public static String XAEROS_CHANNEL = "xaerominimap:main";
    public static String XAEROS_MAP_CHANNEL = "xaeroworldmap:main";


    public XaerosHandler(JavaMinimapPlugin plugin) {
        this.plugin = plugin;
    }

    public void sendXaerosHandshake(MinimapPlayer player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeByte(1);
        out.writeInt(2);
        player.sendPluginMessage(out.toByteArray(), XAEROS_CHANNEL);
        player.sendPluginMessage(out.toByteArray(), XAEROS_MAP_CHANNEL);
    }

    public void sendXaerosConfig(MinimapPlayer player) {
        XaerosWorldConfig worldConfig = plugin.getConfig().getWorldConfig(player.getLocation().getWorld().getName()).xaerosConfig;
        XaerosConfig config = plugin.getConfig().globalXaerosConfig;
        if (worldConfig != null && worldConfig.enabled) {
            config = worldConfig;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeByte(4);
        CompoundBinaryTag tag = CompoundBinaryTag.builder()
                .putBoolean("cm", config.caveMode)
                .putBoolean("ncm", config.netherCaveMode)
                .putBoolean("r", config.radar)
                .build();
        try {
            if (player.getVersion().greaterThanEqual(new Version(1,20,3))) {
                BinaryTagIO.writer().writeNameless(tag, out);
            } else {
                BinaryTagIO.writer().write(tag, out);
            }
            byte[] arr = out.toByteArray();
            player.sendPluginMessage(arr, XAEROS_CHANNEL);
            player.sendPluginMessage(arr, XAEROS_MAP_CHANNEL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onPluginMessage(String channel, MinimapPlayer player, byte[] message) {
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        if (in.readByte() == 1) {
            int version = in.readInt();
            if (version < 2) {
                player.disconnect(MiniMessage.miniMessage().deserialize("<red>Xaero's Minimap is outdated.\n Please update to 23.7.0 or later."));
                return;
            }

            sendXaerosConfig(player);
        }
    }
}
