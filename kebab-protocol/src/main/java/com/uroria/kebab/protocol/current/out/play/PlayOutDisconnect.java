package com.uroria.kebab.protocol.current.out.play;

import com.uroria.kebab.protocol.io.KebabOutputStream;
import com.uroria.kebab.protocol.packet.OutgoingPacket;
import com.uroria.kebab.protocol.packet.Packet;
import com.uroria.kebab.protocol.packet.State;
import lombok.Getter;
import net.kyori.adventure.text.Component;

import java.io.IOException;

@Packet(id = 0x1A, state = State.PLAY)
public final class PlayOutDisconnect extends OutgoingPacket {

    private @Getter final Component reason;

    public PlayOutDisconnect(Component reason) {
        this.reason = reason;
    }

    @Override
    public void write(KebabOutputStream output) throws IOException {
        output.writeComponent(this.reason);
    }
}
