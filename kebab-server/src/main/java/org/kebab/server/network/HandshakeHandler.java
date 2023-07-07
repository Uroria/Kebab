package org.kebab.server.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.kebab.api.player.PlayerLoginEvent;
import org.kebab.api.player.PlayerSkin;
import org.kebab.protocol.packet.IngoingPacket;
import org.kebab.protocol.current.in.handshake.HandshakingIn;
import org.kebab.protocol.current.in.login.LoginInStart;
import org.kebab.protocol.current.out.login.LoginOutSuccess;
import org.kebab.server.KebabServer;
import org.kebab.server.entity.KebabPlayer;
import net.kyori.adventure.text.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.UUID;

public final class HandshakeHandler extends StateHandler {

    HandshakeHandler(ClientConnection connection) {
        super(connection);
    }

    @Override
    public void handle() throws IOException {
        int handshakeSize = connection.channel.getInput().readVarInt();
        if (handshakeSize == 0xFE) {
            connection.close();
            return;
        }

        if (!(connection.channel.readPacket(handshakeSize) instanceof HandshakingIn handshake)) {
            connection.close();
            return;
        }

        String bungeeForwarding = handshake.getServerAddress();
        UUID clientUUID = null;
        PlayerSkin skin = null;

        switch (handshake.getType()) {
            case STATUS -> {
                KebabServer.logger().info("Server got pinged. This is not supported. Ignoring it");
                connection.close();
                return;
            }
            case LOGIN -> {
                connection.state = ClientState.LOGIN;
                String[] data = bungeeForwarding.split("\\x00");
                String host = "";
                String clientHost = "";
                String bungee = "";
                String skinData = "";
                int state = 0;
                for (String datum : data) {
                    switch (state) {
                        case 0 -> {
                            host = datum;
                            state = 1;
                        }
                        case 1 -> {
                            state = 2;
                            if (datum.startsWith("^^Floodgate^")) {
                                KebabServer.logger().info("Floodgate user is trying to connect. This is unsupported behaviour");
                                connection.disconnectDuringLogin(Component.text("§cUnsupported client"));
                                return;
                            }
                        }
                        case 2 -> {
                            clientHost = datum;
                            state = 3;
                        }
                        case 3 -> {
                            bungee = datum;
                            state = 4;
                        }
                        case 4 -> {
                            skinData = datum;
                            state = 6;
                        }
                    }
                }
                if (state != 6) throw new IllegalStateException("Illegal bungee state: " + state);

                clientUUID = UUID.fromString(bungee.replaceFirst("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5"));
                connection.address = InetAddress.getByName(clientHost);

                if (!skinData.equals("")) {
                    JsonArray skinJson = (JsonArray) new JsonParser().parse(skinData);

                    for (JsonElement object : skinJson) {
                        JsonObject property = object.getAsJsonObject();
                        if (property.get("name").getAsString().equals("textures")) {
                            String texture = property.get("value").getAsString();
                            String signature = property.get("signature").getAsString();
                            skin = new PlayerSkin(texture, signature);
                            break;
                        }
                    }
                }

                int messageId = new Random().nextInt();
                while (connection.socket.isConnected()) {
                    IngoingPacket packet = connection.channel.readPacket();

                    if (packet instanceof LoginInStart login) {
                        String name = login.getName();
                        UUID uuid = login.getUUID().orElse(UUID.nameUUIDFromBytes(("OFFLINE:" + name).getBytes(StandardCharsets.UTF_8)));

                        LoginOutSuccess success = new LoginOutSuccess(uuid, name);
                        connection.sendPacket(success);

                        connection.state = ClientState.PLAY;

                        connection.player = new KebabPlayer(connection, uuid, name);
                        //TODO Set skin layers (byte) (0x01 | 0x02 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40)
                        break;
                    }
                }

                PlayerLoginEvent playerLoginEvent = new PlayerLoginEvent(connection.player);
                //TODO Call event
            }
        }
    }
}
