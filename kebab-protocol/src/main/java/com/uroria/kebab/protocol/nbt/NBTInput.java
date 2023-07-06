package com.uroria.kebab.protocol.nbt;

import com.uroria.kebab.protocol.tag.NamedTag;
import com.uroria.kebab.protocol.tag.Tag;

import java.io.IOException;

public interface NBTInput {

    NamedTag readTag(int maxDepth) throws IOException;

    Tag<?> readRawTag(int maxDepth) throws IOException;
}
