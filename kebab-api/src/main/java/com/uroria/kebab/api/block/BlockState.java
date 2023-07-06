package com.uroria.kebab.api.block;

import com.uroria.kebab.api.Material;
import com.uroria.kebab.common.ValidUtils;
import com.uroria.kebab.protocol.tag.CompoundTag;
import com.uroria.kebab.protocol.tag.Tag;
import com.uroria.kebab.protocol.tag.primitives.StringTag;
import lombok.Getter;
import net.kyori.adventure.key.Key;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class BlockState {
    private final CompoundTag tag;
    private @Getter Material material;

    public BlockState() {
        this(new CompoundTag());
    }

    public BlockState(CompoundTag tag) {
        if (tag == null) tag = new CompoundTag();
        this.tag = tag;
        this.material = Material.byType(getType());
    }

    public Key getType() {
        return Key.key(tag.getString("Name"));
    }

    public void setType(Key key) {
        ValidUtils.notNull(key, "Type cannot be null");
        tag.putString("Name", key.toString());
    }

    public void setMaterial(Material material) {
        ValidUtils.notNull(material, "Material cannot be null");
        setType(material.getType());
    }

    public Map<String, String> getProperties() {
        Map<String, String> mapping = new HashMap<>();
        for (Map.Entry<String, Tag<?>> entry : tag.getCompoundTag("Properties")) {
            String key = entry.getKey();
            String value = ((StringTag) entry.getValue()).getValue();
            mapping.put(key, value);
        }
        return mapping;
    }

    public Optional<String> getProperty(String key) {
        if (key == null) return Optional.empty();
        Tag<?> value = tag.getCompoundTag("Properties").get(key);
        return value == null ? Optional.empty() : Optional.of(((StringTag) value).getValue());
    }

    public void setProperties(Map<String, String> mapping) {
        ValidUtils.notNull(mapping, "Mapping cannot be null");
        CompoundTag properties = new CompoundTag();
        for (Map.Entry<String, String> entry : mapping.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            properties.putString(key, value);
        }
        tag.put("Properties", properties);
    }

    public <T> void setProperty(String key, T value) {
        ValidUtils.notNull(key, "Key cannot be null");
        CompoundTag properties = tag.getCompoundTag("Properties");
        properties.putString(key, ((T) value).toString());
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        BlockState that = (BlockState) object;
        return Objects.equals(tag, that.tag);
    }

    @Override
    public String toString() {
        return this.tag.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(tag);
    }

    public CompoundTag toCompoundTag() {
        return tag;
    }
}
