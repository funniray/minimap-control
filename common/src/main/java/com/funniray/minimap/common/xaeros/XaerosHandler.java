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
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class XaerosHandler implements MessageHandler {
    private final JavaMinimapPlugin plugin;

    public static String XAEROS_CHANNEL = "xaerominimap:main";
    public static String XAEROS_MAP_CHANNEL = "xaeroworldmap:main";

    private static final String MARKER_FAIR = marker("fairxaero");
    private static final String MARKER_RESET = marker("resetxaero");
    private static final String MARKER_NETHER_IS_FAIR_MINIMAP = marker("xaerommnetherisfair");
    private static final String MARKER_NETHER_IS_FAIR_WORLDMAP = marker("xaerowmnetherisfair");

    private static final String STATE_UNRESTRICTED = "";
    private static final String STATE_FAIR = "fair";
    private static final String STATE_FAIR_EXCEPT_NETHER = "fair-except-nether";

    private final Map<UUID, String> appliedMarkers = new ConcurrentHashMap<>();

    public XaerosHandler(JavaMinimapPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Xaero looks for these keywords with a section sign in front of every character, so that the
     * whole marker is consumed as formatting codes and renders as an empty line.
     */
    private static String marker(String keyword) {
        StringBuilder builder = new StringBuilder(keyword.length() * 2);
        for (char c : keyword.toCharArray()) {
            builder.append('\u00a7').append(c);
        }
        return builder.toString();
    }

    public void sendXaerosHandshake(MinimapPlayer player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeByte(1);
        out.writeInt(2);
        player.sendPluginMessage(out.toByteArray(), XAEROS_CHANNEL);
        player.sendPluginMessage(out.toByteArray(), XAEROS_MAP_CHANNEL);
    }

    public XaerosConfig getEffectiveConfig(MinimapPlayer player) {
        XaerosWorldConfig worldConfig = plugin.getConfig().getWorldConfig(player.getLocation().getWorld().getName()).xaerosConfig;
        XaerosConfig config = plugin.getConfig().globalXaerosConfig;
        if (worldConfig != null && worldConfig.enabled) {
            config = worldConfig;
        }

        return config.applyOverrides(player);
    }

    public void sendXaerosConfig(MinimapPlayer player) {
        XaerosConfig config = getEffectiveConfig(player);

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

        sendXaerosSystemMessages(player, config);
    }

    /**
     * Fair-play enforcement for Xaero 25.3.0 and above, which no longer reads the config packet.
     * Markers must be delivered as raw system chat (tellraw-style), not Adventure components —
     * matching XaeroForceDisabler / Xaero's documented server-side control strings.
     * The markers are a client side latch rather than a full state, so a reset has to be sent
     * before the wanted markers are re-applied.
     */
    public void sendXaerosSystemMessages(MinimapPlayer player, XaerosConfig config) {
        if (!plugin.getConfig().xaerosSystemMessages) {
            return;
        }

        String state = STATE_UNRESTRICTED;
        boolean bypass = player.isPermissionSet("minimap.xaeros.bypass")
                && player.hasPermission("minimap.xaeros.bypass");
        if (!bypass && (!config.caveMode || !config.radar)) {
            state = config.netherCaveMode ? STATE_FAIR_EXCEPT_NETHER : STATE_FAIR;
        }

        String previous = appliedMarkers.put(player.getUniqueId(), state);
        if (state.equals(previous)) {
            return;
        }

        if (previous != null && !previous.equals(STATE_UNRESTRICTED)) {
            player.sendRawSystemMessage(MARKER_RESET);
        }
        if (state.equals(STATE_UNRESTRICTED)) {
            return;
        }

        player.sendRawSystemMessage(MARKER_FAIR);
        if (state.equals(STATE_FAIR_EXCEPT_NETHER)) {
            player.sendRawSystemMessage(MARKER_NETHER_IS_FAIR_MINIMAP);
            player.sendRawSystemMessage(MARKER_NETHER_IS_FAIR_WORLDMAP);
        }
    }

    public void forgetPlayer(MinimapPlayer player) {
        appliedMarkers.remove(player.getUniqueId());
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
