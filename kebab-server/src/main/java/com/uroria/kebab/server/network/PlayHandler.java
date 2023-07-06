package com.uroria.kebab.server.network;

import com.uroria.kebab.api.player.PlayerJoinEvent;

import java.io.IOException;

public final class PlayHandler extends StateHandler {

    PlayHandler(ClientConnection connection) {
        super(connection);
    }

    @Override
    public void handle() throws IOException {
        PlayerJoinEvent playerJoinEvent = new PlayerJoinEvent(connection.player);
        //TODO Handle event


    }
}
