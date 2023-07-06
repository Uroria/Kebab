package com.uroria.kebab.api;

import com.uroria.kebab.protocol.packet.Version;

public interface Server {
    Version getVersion();

    void shutdown();

    void reload();
}
