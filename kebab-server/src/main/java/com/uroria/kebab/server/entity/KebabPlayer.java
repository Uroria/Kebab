package com.uroria.kebab.server.entity;

import com.uroria.kebab.api.player.Player;
import com.uroria.kebab.common.ValidUtils;
import com.uroria.kebab.protocol.packet.OutgoingPacket;
import com.uroria.kebab.protocol.current.out.play.PlayOutPluginMessage;
import com.uroria.kebab.server.network.ClientConnection;
import lombok.Getter;

import java.io.IOException;
import java.util.UUID;

public final class KebabPlayer implements Player {
    private final ClientConnection connection;
    private final UUID uuid;
    private @Getter String name;

    public KebabPlayer(ClientConnection connection, UUID uuid, String name) {
        this.connection = connection;
        this.uuid = uuid;
        this.name = name;
    }

    public void sendPluginMessage(String channel, byte[] data) {
        PlayOutPluginMessage pluginMessage = new PlayOutPluginMessage(channel, data);
        try {
            sendPacket(pluginMessage);
        } catch (Exception exception) {
            throw new RuntimeException("Cannot send plugin-message to player " + name, exception);
        }
    }

    public void sendPacket(OutgoingPacket packet) throws IOException {
        ValidUtils.notNull(packet, "Packet cannot be null");
        connection.sendPacket(packet);
    }

    public UUID getUUID() {
        return this.uuid;
    }
}
