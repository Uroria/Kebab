package com.uroria.kebab.server;

import com.uroria.kebab.protocol.packet.Version;
import de.leonhard.storage.Toml;
import de.leonhard.storage.internal.settings.ReloadSettings;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class KebabConfiguration {
    private final Toml config = new Toml("kebab.toml", ".", KebabServer.class.getClassLoader().getResourceAsStream("config.toml"), ReloadSettings.MANUALLY, null);

    private @Getter Version version;
    private @Getter @Setter String host;
    private @Getter @Setter int port;
    private @Getter @Setter boolean sentryEnabled;
    private @Getter @Setter String sentryDSN;

    public void reload() {
        version = setVersion(config().getOrSetDefault("minecraft.version", "1.20"));
        host = config().getString("socket.host");
        port = config().getInt("socket.port");
        sentryEnabled = config().getBoolean("sentry.enabled");
        sentryDSN = config().getString("sentry.dsn");
    }

    public Version setVersion(String name) {
        return setVersion(Version.getByName(name));
    }

    public Version setVersion(Version newVersion) {
        version = newVersion;
        return version;
    }

    Toml config() {
        return config;
    }
}
