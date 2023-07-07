package org.kebab.server.network.channel;

public abstract class ChannelPacketHandler {
    public ChannelPacketRead read(ChannelPacketRead read) {
        return read;
    }

    public ChannelPacketWrite write(ChannelPacketWrite write) {
        return write;
    }
}
