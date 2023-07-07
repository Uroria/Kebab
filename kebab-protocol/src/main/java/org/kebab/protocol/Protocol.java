package org.kebab.protocol;

import org.kebab.protocol.packet.IngoingPacket;
import org.kebab.protocol.packet.OutgoingPacket;
import org.kebab.protocol.packet.Packet;
import org.kebab.protocol.packet.State;
import org.kebab.protocol.packet.Version;
import lombok.Getter;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class Protocol {
    private @Getter final Map<Byte, Class<? extends IngoingPacket>> ingoingHandshake = new HashMap<>();
    private @Getter final Map<Byte, Class<? extends OutgoingPacket>> outgoingHandshake = new HashMap<>();
    private @Getter final Map<Byte, Class<? extends IngoingPacket>> ingoingLogin = new HashMap<>();
    private @Getter final Map<Byte, Class<? extends OutgoingPacket>> outgoingLogin = new HashMap<>();
    private @Getter final Map<Byte, Class<? extends IngoingPacket>> ingoingPlay = new HashMap<>();
    private @Getter final Map<Byte, Class<? extends OutgoingPacket>> outgoingPlay = new HashMap<>();

    public void reload(Version version) {
        ingoingHandshake.clear();
        outgoingHandshake.clear();
        ingoingLogin.clear();
        outgoingLogin.clear();
        ingoingPlay.clear();
        outgoingPlay.clear();
        for (Class<?> clazz : version.getPackets()) {
            if (!clazz.isAnnotationPresent(Packet.class)) return;
            Packet packet = clazz.getAnnotation(Packet.class);

            byte packetId = packet.id();
            State state = packet.state();
            if (clazz.isAssignableFrom(IngoingPacket.class)) {
                Class<? extends IngoingPacket> packetClass = (Class<? extends IngoingPacket>) clazz;
                switch (state) {
                    case HANDSHAKE -> ingoingHandshake.put(packetId, packetClass);
                    case LOGIN -> ingoingLogin.put(packetId, packetClass);
                    case PLAY -> ingoingPlay.put(packetId, packetClass);
                }
                return;
            }
            if (clazz.isAssignableFrom(OutgoingPacket.class)) {
                Class<? extends OutgoingPacket> packetClass = (Class<? extends OutgoingPacket>) clazz;
                switch (state) {
                    case HANDSHAKE -> outgoingHandshake.put(packetId, packetClass);
                    case LOGIN -> outgoingLogin.put(packetId, packetClass);
                    case PLAY -> outgoingPlay.put(packetId, packetClass);
                }
            }
        }
    }
}
