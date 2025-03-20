package com.funniray.minimap.common.jm;

import com.funniray.minimap.common.JavaMinimapPlugin;
import com.funniray.minimap.common.MinimapConfig;
import com.funniray.minimap.common.api.MessageHandler;
import com.funniray.minimap.common.api.MinimapPlayer;
import com.funniray.minimap.common.api.MinimapWorld;
import com.funniray.minimap.common.jm.data.JMConfig;
import com.funniray.minimap.common.jm.data.JMVersion;
import com.funniray.minimap.common.jm.data.JMWorldConfig;
import com.funniray.minimap.common.jm.data.ServerPropType;
import com.funniray.minimap.common.network.NetworkUtils;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.awt.print.Pageable;
import java.util.*;

public class JMHandler implements MessageHandler {
    private final JavaMinimapPlugin plugin;
    // JM for 1.21 and above no longer has a leading 0 byte at the start of packets.
    // We need to note who is on a modern version and not send them (or parse) leading 0 bytes
    private final Map<UUID, Boolean> modernList = new HashMap<>();

    public JMHandler(JavaMinimapPlugin plugin) {
        this.plugin = plugin;
    }

    private Optional<MinimapWorld> getWorldFromKeyedName(String keyedWorld) {
        return JavaMinimapPlugin.getInstance().getServer().getWorlds().stream().filter(w->w.getKeyedName().equals(keyedWorld)).findFirst();
    }

    public void handleMPOptions(MinimapPlayer player, byte[] message, String replyChannel, int replyByte) {
        player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Multiplayer options are not implemented."));
    }

    public void handleTeleport(MinimapPlayer player, byte[] message, String replyChannel, int replyByte) {
        if (!player.hasPermission("minimap.jm.teleport")) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>You don't have permission to teleport."));
            return;
        }
        String teleport = getEffectiveConfig(player).teleportEnabled;
        if (teleport.equalsIgnoreCase("none") || (teleport.equalsIgnoreCase("ops") && !player.hasPermission("minimap.jm.admin"))) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Teleport packet was sent, but teleporting isn't enabled."));
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        if (!modern(player)) in.readByte();
        double x = in.readDouble();
        double y = in.readDouble();
        double z = in.readDouble();
        String dim = NetworkUtils.readUtf(in);

        Optional<MinimapWorld> world = getWorldFromKeyedName(dim);

        if (!world.isPresent()){
            player.teleport(player.getLocation().getWorld().getLocation(x,y,z));
            return;
        }

        player.teleport(world.get().getLocation(x,y,z));
    }

    public void handleAdminReq(MinimapPlayer player, byte[] message, String replyChannel, int replyByte) {
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        if (!modern(player)) in.readByte();
        in.readByte();
        ServerPropType type = ServerPropType.getFromType(in.readInt());
        Gson gson = new Gson();
        if (type != ServerPropType.GLOBAL) {
            return;
        }

        HashMap<String, String> payloads = new HashMap<>();

        payloads.put("GLOBAL", gson.toJson(plugin.getConfig().globalJourneymapConfig));
        payloads.put("DEFAULT", gson.toJson(plugin.getConfig().defaultWorldConfig));

        plugin.getServer().getWorlds().stream()
                .map(MinimapWorld::getName)
                .forEach(s->payloads.put(s,gson.toJson(plugin.getConfig().getWorldConfig(s))));

        for (Map.Entry<String, String> ent : payloads.entrySet()) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            if (!modern(player)) out.writeByte(replyByte);
            out.writeByte(42);
            if (ent.getKey().equals("GLOBAL")) {
                out.writeInt(ServerPropType.GLOBAL.getId());
                NetworkUtils.writeUtf("", out);
            } else if (ent.getKey().equals("DEFAULT")) {
                out.writeInt(ServerPropType.DEFAULT.getId());
                NetworkUtils.writeUtf("", out);
            } else {
                out.writeInt(ServerPropType.DIMENSION.getId());
                NetworkUtils.writeUtf(ent.getKey(), out);
            }
            NetworkUtils.writeUtf(ent.getValue(), out);
            player.sendPluginMessage(out.toByteArray(), replyChannel);
        }
    }

    public void handleAdminSave(MinimapPlayer player, byte[] message, String replyChannel, int replyByte) {
        if (!player.hasPermission("minimap.jm.admin")) return;

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        if (!modern(player)) in.readByte();
        in.readByte();
        int type = in.readInt();
        String dimension = NetworkUtils.readUtf(in);
        String payload = NetworkUtils.readUtf(in);

        Gson gson = new Gson();
        if (type == 1) {
            JMConfig newConfig = gson.fromJson(payload, JMConfig.class);
            plugin.getConfig().globalJourneymapConfig = newConfig;
        } else if (type == 2 || type == 3) {
            JMWorldConfig newConfig = gson.fromJson(payload, JMWorldConfig.class);
            if (type == 3) {
                MinimapConfig.WorldConfig worldConfig = plugin.getConfig().getWorldConfig(dimension);
                worldConfig.journeymapConfig = newConfig;
                System.out.println(dimension);
            } else {
                plugin.getConfig().defaultWorldConfig = newConfig;
            }
        }
        plugin.saveConfig();

        // Probably not correct, as a 1.16 admin could send an invalid packet
        // to a newer client that's also online
        String permChannel = "journeymap:perm_req";
        int replyInt = 0;
        if (replyChannel.equals("journeymap:common")) {
            permChannel = "journeymap:common";
            replyInt = 2;
        }

        String finalPermChannel = permChannel;
        int finalReplyInt = replyInt;
        plugin.getServer().getPlayers().forEach(p->handlePerm(p, new byte[0], finalPermChannel, finalReplyInt));
    }

    public void handleVersion(MinimapPlayer player, byte[] message, String replyChannel) {
        modernList.put(player.getUniqueId(), message.length > 0 && message[0] != 0);
        Gson gson = new Gson();
        JMVersion serverVersion = new JMVersion();
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        if (!modern(player)) in.readByte();

        String sent = NetworkUtils.readUtf(in);
        JMVersion clientVersion = gson.fromJson(sent, JMVersion.class);
        if (clientVersion.journeymap_version.major <= 5) {
            serverVersion.journeymap_version = new JMVersion.VersionDetails(6,0,0,null);
        }

        String payload = gson.toJson(serverVersion);
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        if (!modern(player)) out.writeByte(0);
        NetworkUtils.writeUtf(payload, out);
        player.sendPluginMessage(out.toByteArray(), replyChannel);
    }

    public JMConfig getEffectiveConfig(MinimapPlayer player) {
        JMWorldConfig worldConfig = plugin.getConfig().getWorldConfig(player.getLocation().getWorld().getName()).journeymapConfig;
        JMConfig config = plugin.getConfig().globalJourneymapConfig;
        if (worldConfig != null) {
            return worldConfig.applyToConfig(config);
        }
        return config;
    }

    public void handlePerm(MinimapPlayer player, byte[] message, String replyChannel, int replyByte) {
        modernList.putIfAbsent(player.getUniqueId(), message.length > 0 && message[0] == 42);
        JMConfig config = getEffectiveConfig(player);

        Gson gson = new Gson();
        String payload = gson.toJson(config);
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        if (!modern(player)) out.writeByte(replyByte);
        out.writeByte(42);
        out.writeBoolean(player.hasPermission("minimap.jm.admin"));
        NetworkUtils.writeUtf(payload, out);
        out.writeBoolean(true);
        player.sendPluginMessage(out.toByteArray(), replyChannel);
    }

    private boolean modern(MinimapPlayer player) {
        Boolean modern = modernList.get(player.getUniqueId());
        if (modern == null) return false;
        return modern;
    }

    public void playerLeft(MinimapPlayer player) {
        modernList.remove(player.getUniqueId());
    }

    @Override
    public void onPluginMessage(String channel, MinimapPlayer player, byte[] message) {
        switch (channel.split(":")[1]) {
            case "version":
                handleVersion(player, message, channel);
                break;
            case "perm_req":
                handlePerm(player, message, channel, 0);
                break;
            case "admin_req":
                handleAdminReq(player, message, channel, 0);
                break;
            case "admin_save":
                handleAdminSave(player, message, channel, 0);
                break;
            case "teleport_req":
                handleTeleport(player, message, channel, 0);
                break;
            case "common":
                ByteArrayDataInput in = ByteStreams.newDataInput(message);
                byte type = in.readByte();
                switch (type) {
                    case 0:
                        handleAdminReq(player, message, channel, type);
                        break;
                    case 1:
                        handleAdminSave(player, message, channel, type);
                        break;
                    case 2:
                        handlePerm(player, message, channel, type);
                        break;
                    case 4:
                        handleTeleport(player, message, channel, type);
                        break;
                    case 5:
                        handleMPOptions(player, message, channel, type);
                        break;
                }
                break;
        }
    }
}
