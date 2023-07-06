package com.uroria.kebab.protocol.current.in.login;

import com.uroria.kebab.protocol.io.KebabInputStream;
import com.uroria.kebab.protocol.packet.IngoingPacket;
import com.uroria.kebab.protocol.packet.Packet;
import com.uroria.kebab.protocol.packet.State;
import lombok.Getter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Packet(id = 0x00, state = State.LOGIN)
public final class LoginInStart extends IngoingPacket {

    private @Getter String name;
    private UUID uuid;

    @Override
    public void read(KebabInputStream input) throws IOException {
        this.name = input.readString();
        if (input.readBoolean()) this.uuid = input.readUUID();
    }

    public Optional<UUID> getUUID() {
        return Optional.ofNullable(this.uuid);
    }
}
