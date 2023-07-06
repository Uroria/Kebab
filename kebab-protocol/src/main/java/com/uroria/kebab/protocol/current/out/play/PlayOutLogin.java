package com.uroria.kebab.protocol.current.out.play;

import com.uroria.kebab.protocol.io.KebabOutputStream;
import com.uroria.kebab.protocol.packet.OutgoingPacket;
import com.uroria.kebab.protocol.packet.Packet;
import com.uroria.kebab.protocol.packet.State;
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
