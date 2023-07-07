package org.kebab.protocol.current.out.play;

import org.kebab.protocol.io.KebabOutputStream;
import org.kebab.protocol.packet.OutgoingPacket;
import org.kebab.protocol.packet.Packet;
import org.kebab.protocol.packet.State;
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
