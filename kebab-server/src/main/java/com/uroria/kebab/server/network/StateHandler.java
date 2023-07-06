package com.uroria.kebab.server.network;

import java.io.IOException;

public abstract class StateHandler {
    protected final ClientConnection connection;

    StateHandler(ClientConnection connection) {
        this.connection = connection;
    }

    public abstract void handle() throws IOException;
}
