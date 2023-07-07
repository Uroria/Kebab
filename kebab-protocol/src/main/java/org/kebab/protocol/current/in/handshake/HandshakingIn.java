package org.kebab.protocol.current.in.handshake;

import org.kebab.protocol.io.KebabInputStream;
import org.kebab.protocol.packet.IngoingPacket;
import org.kebab.protocol.packet.Packet;
import org.kebab.protocol.packet.State;
import lombok.Getter;

import java.io.IOException;

@Packet(id = 0x00, state = State.HANDSHAKE)
public final class HandshakingIn extends IngoingPacket {
    private @Getter int protocolVersion;
    private @Getter String serverAddress;
    private @Getter int serverPort;
    private @Getter HandshakeType type;

    @Override
    public void read(KebabInputStream input) throws IOException {
        this.protocolVersion = input.readVarInt();
        this.serverAddress = input.readString();
        this.serverPort = input.readShort() & 0xFFFF;
        this.type = HandshakeType.fromId(input.readVarInt());
    }

    public enum HandshakeType {
        STATUS(1),
        LOGIN(2);

        @Getter final int id;

        HandshakeType(int id) {
            this.id = id;
        }

        static HandshakeType fromId(int id) {
            for (HandshakeType type : values()) {
                if (type.id == id) return type;
            }
            return null;
        }
    }
}
