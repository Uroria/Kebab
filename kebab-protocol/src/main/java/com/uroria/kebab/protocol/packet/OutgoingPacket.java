package com.uroria.kebab.protocol.packet;

import com.uroria.kebab.protocol.io.KebabOutputStream;

import java.io.IOException;

public abstract class OutgoingPacket extends AbstractPacket {

    public abstract void write(KebabOutputStream output) throws IOException;
}
