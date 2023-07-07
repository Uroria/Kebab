package org.kebab.server;

import de.leonhard.storage.Toml;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class KebabConfiguration {
    private @Getter final Toml config = new Toml("kebab.toml", ".", KebabServer.class.getClassLoader().getResourceAsStream("config.toml"));

    private @Getter @Setter String host;
    private @Getter @Setter int port;

    public void reload() {
        host = config.getString("socket.host");
        port = config.getInt("socket.port");
    }
}
