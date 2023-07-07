package org.kebab.protocol.current.out.login;

import org.kebab.protocol.io.KebabOutputStream;
import org.kebab.protocol.packet.OutgoingPacket;
import org.kebab.protocol.packet.Packet;
import org.kebab.protocol.packet.State;
import lombok.Getter;

import java.io.IOException;
import java.util.UUID;

@Packet(id = 0x02, state = State.LOGIN)
public final class LoginOutSuccess extends OutgoingPacket {
    private final UUID uuid;
    private @Getter final String name;

    public LoginOutSuccess(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    @Override
    public void write(KebabOutputStream output) throws IOException {
        output.writeUUID(uuid);
        output.writeString(name);
        output.writeVarInt(0);
    }

    public UUID getUUID() {
        return this.uuid;
    }
}
