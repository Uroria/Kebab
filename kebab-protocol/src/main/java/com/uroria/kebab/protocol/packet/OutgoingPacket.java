package com.uroria.kebab.protocol.packet;

import com.uroria.kebab.protocol.io.KebabOutputStream;

import java.io.IOException;

public abstract class OutgoingPacket {

    public abstract void write(KebabOutputStream output) throws IOException;
}
