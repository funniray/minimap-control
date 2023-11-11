package com.funniray.minimap.common.xaeros;

import com.funniray.minimap.common.JavaMinimapPlugin;
import com.funniray.minimap.common.api.MessageHandler;
import com.funniray.minimap.common.api.MinimapPlayer;
import com.funniray.minimap.common.network.NetworkUtils;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.dewy.nbt.Nbt;
import dev.dewy.nbt.tags.collection.CompoundTag;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.io.IOException;

public class XaerosHandler implements MessageHandler {
    private JavaMinimapPlugin plugin;

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
        CompoundTag tag = new CompoundTag();
        tag.putByte("cm", NetworkUtils.booleanToByte(config.caveMode));
        tag.putByte("ncm", NetworkUtils.booleanToByte(config.netherCaveMode));
        tag.putByte("r", NetworkUtils.booleanToByte(config.radar));
        try {
            new Nbt().toStream(tag, out);
            byte[] arr = out.toByteArray();
            player.sendPluginMessage(arr, XAEROS_CHANNEL);
            player.sendPluginMessage(out.toByteArray(), XAEROS_MAP_CHANNEL);
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
