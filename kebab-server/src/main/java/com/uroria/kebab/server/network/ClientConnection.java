package com.uroria.kebab.server.network;

import com.uroria.kebab.protocol.Protocol;
import com.uroria.kebab.protocol.io.KebabInputStream;
import com.uroria.kebab.protocol.io.KebabOutputStream;
import com.uroria.kebab.protocol.packet.AbstractPacket;
import com.uroria.kebab.protocol.packet.IngoingPacket;
import com.uroria.kebab.protocol.packet.OutgoingPacket;
import com.uroria.kebab.protocol.current.out.login.LoginOutDisconnect;
import com.uroria.kebab.protocol.current.out.play.PlayOutDisconnect;
import com.uroria.kebab.server.KebabServer;
import com.uroria.kebab.server.entity.KebabPlayer;
import com.uroria.kebab.server.network.channel.Channel;
import com.uroria.kebab.server.network.channel.ChannelPacketHandler;
import com.uroria.kebab.server.network.channel.ChannelPacketRead;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;

import java.io.DataInput;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

public final class ClientConnection extends Thread {
    private static final Key DEFAULT_HANDLER_NAMESPACE = Key.key("default");

    private final HandshakeHandler handshakeHandler;
    private final PlayHandler playHandler;
    final Socket socket;
    InetAddress address;
    Channel channel;
    boolean running;
    ClientState state;

    KebabPlayer player;
    TimerTask keelAliveTask;
    final AtomicLong lastPacketTimeStamp;
    final AtomicLong lastKeepAlivePayload;
    boolean ready;

    ClientConnection(Socket socket) {
        this.socket = socket;
        this.address = socket.getInetAddress();
        this.handshakeHandler = new HandshakeHandler(this);
        this.playHandler = new PlayHandler(this);
        this.lastPacketTimeStamp = new AtomicLong(-1);
        this.lastKeepAlivePayload = new AtomicLong(-1);
    }

    @Override
    public void run() {
        this.running = true;
        state = ClientState.HANDSHAKE;
        try {
            try {
                socket.setKeepAlive(true);
                setChannel(new KebabInputStream(socket.getInputStream()), new KebabOutputStream(socket.getOutputStream()));
                this.handshakeHandler.handle();
            } catch (Exception exception) {
                close();
                state = ClientState.DISCONNECTED;
            }

            if (state == ClientState.PLAY) {
                this.playHandler.handle();
            }
        } catch (Exception exception) {
            KebabServer.logger().error("Unhandled exception", exception);
            KebabServer.captureException(exception);
        }

        close();
        this.running = false;
    }

    public void setChannel(KebabInputStream input, KebabOutputStream output) {
        this.channel = new Channel(input, output);

        this.channel.addHandlerBefore(DEFAULT_HANDLER_NAMESPACE, new ChannelPacketHandler() {
            @Override
            public ChannelPacketRead read(ChannelPacketRead read) {
                if (read.hasReadPacket()) {
                    return super.read(read);
                }
                try {
                    DataInput input = read.getDataInput();
                    int size = read.getSize();
                    byte packetId = read.getPacketId();
                    Class<? extends IngoingPacket> packetClass = switch (state) {
                        case HANDSHAKE -> Protocol.getIngoingHandshake().get(packetId);
                        case LOGIN -> Protocol.getIngoingLogin().get(packetId);
                        case PLAY ->  Protocol.getIngoingPlay().get(packetId);
                        case DISCONNECTED -> null;
                    };
                    if (packetClass == null) {
                        input.skipBytes(size - AbstractPacket.getVarIntLength(packetId));
                        return null;
                    }
                    Constructor<?>[] constructors = packetClass.getConstructors();
                    Constructor<?> constructor = Arrays.stream(constructors).filter(constructor1 -> constructor1.getParameterCount() == 0).findFirst().orElse(null);
                    if (constructor == null) throw new NoSuchMethodException(packetClass + " has no valid constructors");
                    return super.read(read);
                } catch (Exception exception) {
                    KebabServer.logger().error("Cannot read packet", exception);
                    KebabServer.captureException(exception);
                    return null;
                }
            }
        });
    }

    public void sendPacket(OutgoingPacket packet) throws IOException {
        if (channel.writePacket(packet)) this.lastPacketTimeStamp.set(System.currentTimeMillis());
    }

    void sendUnsafe(OutgoingPacket packet) {
        try {
            sendPacket(packet);
        } catch (Exception ignored) {}
    }

    public void disconnect(Component reason) {
        PlayOutDisconnect disconnect = new PlayOutDisconnect(reason);
        sendUnsafe(disconnect);
        close();
    }

    void disconnectDuringLogin(Component reason) {
        LoginOutDisconnect disconnect = new LoginOutDisconnect(reason);
        sendUnsafe(disconnect);
        close();
    }

    void close() {
        try {
            if (channel != null) {
                state = ClientState.DISCONNECTED;
                this.channel.close();
                this.channel = null;
            }
            if (socket.isClosed()) return;
            this.socket.close();
        } catch (Exception exception) {
            KebabServer.logger().error("Cannot close socket on port " + socket.getPort());
            KebabServer.captureException(exception);
        }
    }
}
