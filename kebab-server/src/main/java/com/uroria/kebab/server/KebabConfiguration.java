package com.uroria.kebab.server;

import com.uroria.kebab.protocol.packet.Version;
import de.leonhard.storage.Toml;
import de.leonhard.storage.internal.settings.ReloadSettings;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;

import java.net.InetSocketAddress;

@UtilityClass
public class KebabConfiguration {
    private final Toml config = new Toml("kebab.toml", ".", KebabServer.class.getClassLoader().getResourceAsStream("config.toml"), ReloadSettings.MANUALLY, null);

    private @Getter Version version;
    private @Getter @Setter String host;
    private @Getter @Setter int port;
    private @Getter @Setter boolean sentryEnabled;
    private @Getter @Setter String sentryDSN;

    public void reload() {
        version = Version.getByName(config().getOrSetDefault("minecraft.version", "1.20"));
        host = config().getString("socket.host");
        port = config().getInt("socket.port");
        sentryEnabled = config().getBoolean("sentry.enabled");
        sentryDSN = config().getString("sentry.dsn");
    }

    public InetSocketAddress getAddress() {
        return new InetSocketAddress(host, port);
    }

    public void setVersion(String name) {
        version = Version.getByName(name);
    }

    Toml config() {
        return config;
    }
}
