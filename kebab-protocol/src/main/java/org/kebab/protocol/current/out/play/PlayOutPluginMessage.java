package org.kebab.protocol.current.out.play;

import org.kebab.protocol.io.KebabOutputStream;
import org.kebab.protocol.packet.OutgoingPacket;
import org.kebab.protocol.packet.Packet;
import org.kebab.protocol.packet.State;
import lombok.Getter;

import java.io.IOException;

@Packet(id = 0x17, state = State.PLAY)
public final class PlayOutPluginMessage extends OutgoingPacket {
    private @Getter final String channel;
    private @Getter final byte[] data;

    public PlayOutPluginMessage(String channel, byte[] data) {
        this.channel = channel;
        this.data = data;
    }

    @Override
    public void write(KebabOutputStream output) throws IOException {
        output.writeString(channel);
        output.write(data);
    }
}
