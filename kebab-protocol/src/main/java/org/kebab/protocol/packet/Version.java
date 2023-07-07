package org.kebab.protocol.packet;

import lombok.Getter;

import java.util.Arrays;
import java.util.Comparator;

public enum Version {
    MINECRAFT_1_20("1.20", 763),
    MINECRAFT_1_20_1("1.20.1", 763);

    private @Getter final String name;
    private @Getter final int protocolVersion;
    private @Getter final Class<?>[] packets;

    Version(String name, int protocolVersion, Class<?>... packets) {
        this.name = name;
        this.protocolVersion = protocolVersion;
        this.packets = packets;
    }

    public static Version getByName(String name) {
        for (Version version : values()) {
            if (version.name.equalsIgnoreCase(name)) return version;
        }
        return getNewest();
    }

    public static Version getNewest() {
        return Arrays.stream(values()).max(Comparator.comparing(y -> y.protocolVersion)).orElse(Version.MINECRAFT_1_20);
    }
}
