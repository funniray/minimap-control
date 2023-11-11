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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JMHandler implements MessageHandler {
    private JavaMinimapPlugin plugin;

    public JMHandler(JavaMinimapPlugin plugin) {
        this.plugin = plugin;
    }

    public void handleMPOptions(MinimapPlayer player, byte[] message, String replyChannel, int replyByte) {
        player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Multiplayer options are not implemented."));
    }

    public void handleTeleport(MinimapPlayer player, byte[] message, String replyChannel, int replyByte) {
        if (!player.hasPermission("minimap.jm.teleport")) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>You don't have permission to teleport."));
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        in.readByte();
        double x = in.readDouble();
        double y = in.readDouble();
        double z = in.readDouble();
        String dim = NetworkUtils.readUtf(in);

        Optional<MinimapWorld> world = plugin.getServer().getWorlds().stream().filter(w->w.getName().equals(dim)).findFirst();
        if (!world.isPresent()){
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>That world doesn't exist"));
            return;
        }

        player.teleport(world.get().getLocation(x,y,z));
    }

    public void handleAdminReq(MinimapPlayer player, byte[] message, String replyChannel, int replyByte) {
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        in.readByte();
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
            out.writeByte(replyByte);
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
        in.readByte();
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
        Gson gson = new Gson();
        String payload = gson.toJson(new JMVersion());
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeByte(0);
        NetworkUtils.writeUtf(payload, out);
        player.sendPluginMessage(out.toByteArray(), replyChannel);
    }

    public void handlePerm(MinimapPlayer player, byte[] message, String replyChannel, int replyByte) {
        JMWorldConfig worldConfig = plugin.getConfig().getWorldConfig(player.getLocation().getWorld().getName()).journeymapConfig;
        JMConfig config = plugin.getConfig().globalJourneymapConfig;
        if (worldConfig != null) {
            config = worldConfig.applyToConfig(config);
        }

        Gson gson = new Gson();
        String payload = gson.toJson(config);
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeByte(replyByte);
        out.writeByte(42);
        out.writeBoolean(player.hasPermission("minimap.jm.admin"));
        NetworkUtils.writeUtf(payload, out);
        out.writeBoolean(false);
        player.sendPluginMessage(out.toByteArray(), replyChannel);
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
