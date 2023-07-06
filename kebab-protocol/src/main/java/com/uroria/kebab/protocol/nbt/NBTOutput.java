package com.uroria.kebab.protocol.nbt;

import com.uroria.kebab.protocol.tag.NamedTag;
import com.uroria.kebab.protocol.tag.Tag;

import java.io.IOException;

public interface NBTOutput {

    void writeTag(NamedTag tag, int maxDepth) throws IOException;

    void writeTag(Tag<?> tag, int maxDepth) throws IOException;

    void flush() throws IOException;
}
