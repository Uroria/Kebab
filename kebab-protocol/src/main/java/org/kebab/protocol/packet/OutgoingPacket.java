package org.kebab.protocol.packet;

import org.kebab.protocol.io.KebabOutputStream;

import java.io.IOException;

public abstract class OutgoingPacket extends AbstractPacket {

    public abstract void write(KebabOutputStream output) throws IOException;
}
