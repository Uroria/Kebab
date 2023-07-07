package org.kebab.protocol.current.out.login;

import org.kebab.protocol.io.KebabOutputStream;
import org.kebab.protocol.packet.OutgoingPacket;
import org.kebab.protocol.packet.Packet;
import org.kebab.protocol.packet.State;
import lombok.Getter;
import net.kyori.adventure.text.Component;

import java.io.IOException;

@Packet(id = 0x00, state = State.LOGIN)
public final class LoginOutDisconnect extends OutgoingPacket {
    private @Getter final Component reason;

    public LoginOutDisconnect(Component reason) {
        this.reason = reason;
    }

    @Override
    public void write(KebabOutputStream output) throws IOException {
        output.writeComponent(reason);
    }
}
