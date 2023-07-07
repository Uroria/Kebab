package org.kebab.protocol.packet;

import org.kebab.protocol.io.KebabInputStream;

import java.io.IOException;

public abstract class IngoingPacket extends AbstractPacket {

    public abstract void read(KebabInputStream input) throws IOException;
}
