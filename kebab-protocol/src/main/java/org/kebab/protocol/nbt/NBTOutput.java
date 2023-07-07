package org.kebab.protocol.nbt;

import org.kebab.protocol.tag.NamedTag;
import org.kebab.protocol.tag.Tag;

import java.io.IOException;

public interface NBTOutput {

    void writeTag(NamedTag tag, int maxDepth) throws IOException;

    void writeTag(Tag<?> tag, int maxDepth) throws IOException;

    void flush() throws IOException;
}
