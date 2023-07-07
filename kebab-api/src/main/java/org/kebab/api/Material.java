package org.kebab.api;

import org.kebab.protocol.packet.Version;
import lombok.Getter;
import net.kyori.adventure.key.Key;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

public enum Material {
    AIR(Key.key("minecraft:air"));
    private @Getter final Key type;
    private final Class<?> data;
    private @Getter final int maximumStackSize;
    private @Getter final short durability;
    private @Getter final Version[] versions;

    Material(Key type) {
        this(type, new Version[0]);
    }

    Material(Key type, Version... versions) {
        this(type, 64, versions);
    }

    Material(Key type, int maximumStackSize, Version... versions) {
        this(type, maximumStackSize, (short) 0, versions);
    }

    Material(Key type, int maximumStackSize, int durability, Version... versions) {
        this(type, maximumStackSize, durability, null, versions);
    }

    Material(Key type, int maximumStackSize, int durability, Class<?> data, Version... versions) {
        this.type = type;
        this.maximumStackSize = maximumStackSize;
        this.durability = (short) durability;
        this.data = data;
        this.versions = versions;
        if (isSupported()) return;
        throw new IllegalStateException("Material " + type.asString() + " not supported in this minecraft version");
    }

    public static Material byType(Key type) {
        for (Material material : values()) {
            if (material.type.asString().equalsIgnoreCase(type.asString())) return material;
        }
        return null;
    }

    public boolean isSupported() {
        if (versions.length == 0) return true;
        Version version = Arrays.stream(getVersions()).max(Comparator.comparing(Version::getProtocolVersion)).orElse(null);
        if (version == null) return true;
        return version.getProtocolVersion() > ServerAPI.getServer().getVersion().getProtocolVersion();
    }

    public Optional<Class<?>> getDataClass() {
        return Optional.ofNullable(data);
    }
}
