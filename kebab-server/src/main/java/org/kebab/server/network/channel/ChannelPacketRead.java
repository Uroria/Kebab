package org.kebab.server.network.channel;

import org.kebab.protocol.packet.IngoingPacket;

import java.io.DataInput;

public final class ChannelPacketRead {
    private int size;
    private byte packetId;
    private final DataInput input;
    private IngoingPacket packet;
    public ChannelPacketRead(int size, byte packetId, DataInput input) {
        this.size = size;
        this.packetId = packetId;
        this.input = input;
        this.packet = null;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public byte getPacketId() {
        return packetId;
    }

    public void setPacketId(byte packetId) {
        this.packetId = packetId;
    }

    public boolean hasReadPacket() {
        return this.packet != null;
    }

    public IngoingPacket getReadPacket() {
        return this.packet;
    }

    public void setPacket(IngoingPacket packet) {
        this.packet = packet;
    }

    public DataInput getDataInput() {
        return this.input;
    }
}
