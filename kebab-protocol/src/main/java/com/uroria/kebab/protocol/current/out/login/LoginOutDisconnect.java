package com.uroria.kebab.protocol.current.out.login;

import com.uroria.kebab.protocol.io.KebabOutputStream;
import com.uroria.kebab.protocol.packet.OutgoingPacket;
import com.uroria.kebab.protocol.packet.Packet;
import com.uroria.kebab.protocol.packet.State;
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
