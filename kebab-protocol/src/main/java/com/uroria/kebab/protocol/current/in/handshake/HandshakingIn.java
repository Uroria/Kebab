package com.uroria.kebab.protocol.current.in.handshake;

import com.uroria.kebab.protocol.io.KebabInputStream;
import com.uroria.kebab.protocol.packet.IngoingPacket;
import com.uroria.kebab.protocol.packet.Packet;
import com.uroria.kebab.protocol.packet.State;
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
