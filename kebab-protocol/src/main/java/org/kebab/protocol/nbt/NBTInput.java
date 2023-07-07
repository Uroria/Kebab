package org.kebab.protocol.nbt;

import org.kebab.protocol.tag.NamedTag;
import org.kebab.protocol.tag.Tag;

import java.io.IOException;

public interface NBTInput {

    NamedTag readTag(int maxDepth) throws IOException;

    Tag<?> readRawTag(int maxDepth) throws IOException;
}
