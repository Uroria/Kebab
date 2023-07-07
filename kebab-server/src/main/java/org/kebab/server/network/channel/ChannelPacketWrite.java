package org.kebab.server.network.channel;

import org.kebab.protocol.packet.OutgoingPacket;

public final class ChannelPacketWrite {
    private OutgoingPacket packet;
    public ChannelPacketWrite(OutgoingPacket packet) {
        this.packet = packet;
    }

    public OutgoingPacket getPacket() {
        return packet;
    }

    public void setPacket(OutgoingPacket packet) {
        this.packet = packet;
    }
}
