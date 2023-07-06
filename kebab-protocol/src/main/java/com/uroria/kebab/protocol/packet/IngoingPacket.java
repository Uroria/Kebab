package com.uroria.kebab.protocol.packet;

import com.uroria.kebab.protocol.io.KebabInputStream;

import java.io.IOException;

public abstract class IngoingPacket {

    public abstract void read(KebabInputStream input) throws IOException;
}
