package org.kebab.api;

import org.kebab.protocol.packet.Version;

public interface Server {
    Version getVersion();

    void shutdown();

    void reload();
}
