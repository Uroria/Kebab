package org.kebab.server.network.channel;

import org.kebab.common.Pair;
import org.kebab.protocol.io.KebabInputStream;
import org.kebab.protocol.io.KebabOutputStream;
import org.kebab.protocol.packet.IngoingPacket;
import org.kebab.protocol.packet.OutgoingPacket;
import org.kebab.protocol.packet.Packet;
import net.kyori.adventure.key.Key;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Channel implements AutoCloseable {
    private final List<Pair<Key, ChannelPacketHandler>> handlers;
    private final AtomicBoolean valid;
    protected final KebabInputStream input;
    protected final KebabOutputStream output;

    public Channel(KebabInputStream input, KebabOutputStream output) {
        this.input = input;
        this.output = output;
        this.handlers = new CopyOnWriteArrayList<>();
        this.valid = new AtomicBoolean(true);
    }

    private void ensureOpen() {
        if (!valid.get()) close();
    }

    public void addHandlerBefore(Key key, ChannelPacketHandler handler) {
        handlers.add(0, new Pair<>(key, handler));
    }

    public void addHandlerAfter(Key key, ChannelPacketHandler handler) {
        handlers.add(new Pair<>(key, handler));
    }

    public void removeHandler(Key key) {
        handlers.removeIf(handler -> handler.getFirst().equals(key));
    }

    public IngoingPacket readPacket() throws IOException {
        return readPacket(-1);
    }

    public IngoingPacket readPacket(int size) throws IOException {
        IngoingPacket packet = null;
        do {
            ensureOpen();
            size = size < 0 ? input.readVarInt() : size;
            int packetId = input.readVarInt();
            if (packetId > 127 || packetId < 0) {
                break;
            }
            ChannelPacketRead read = new ChannelPacketRead(size, (byte) packetId, input);
            for (Pair<Key, ChannelPacketHandler> pair : handlers) {
                read = pair.getSecond().read(read);
                if (read == null) {
                    packet = null;
                    break;
                }
                packet = read.getReadPacket();
            }
            size = -1;
        } while (packet == null);
        return packet;
    }

    public boolean writePacket(OutgoingPacket packet) throws IOException {
        ensureOpen();
        ChannelPacketWrite write = new ChannelPacketWrite(packet);
        for (Pair<Key, ChannelPacketHandler> pair : handlers) {
            write = pair.getSecond().write(write);
            if (write == null) return false;
        }
        packet = write.getPacket();
        if (!packet.getClass().isAnnotationPresent(Packet.class)) return false;
        KebabOutputStream outputStream = new KebabOutputStream(new ByteArrayOutputStream());
        outputStream.writeByte(packet.getClass().getAnnotation(Packet.class).id());
        packet.write(outputStream);
        byte[] packetByte = outputStream.toByteArray();
        output.writeVarInt(packetByte.length);
        output.write(packetByte);
        output.flush();
        return true;
    }

    @Override
    public void close() {
        if (valid.compareAndSet(true, false)) {
            try {
                input.close();
                output.close();
            } catch (Exception ignored) {}
        }
    }

    public KebabInputStream getInput() {
        return input;
    }

    public KebabOutputStream getOutput() {
        return output;
    }
}
