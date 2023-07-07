package org.kebab.protocol.current.out.play;

import org.kebab.protocol.io.KebabOutputStream;
import org.kebab.protocol.packet.OutgoingPacket;
import org.kebab.protocol.packet.Packet;
import org.kebab.protocol.packet.State;
import lombok.Getter;

import java.io.IOException;

@Packet(id = 0x28, state = State.PLAY)
public final class PlayOutLogin extends OutgoingPacket {
    private @Getter int entityId;
    private @Getter boolean hardcore;
    private @Getter int gameMode;


    @Override
    public void write(KebabOutputStream output) throws IOException {

    }
}
