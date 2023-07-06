package com.uroria.kebab.server;

import com.uroria.kebab.api.Server;
import com.uroria.kebab.protocol.Protocol;
import com.uroria.kebab.protocol.packet.Version;
import com.uroria.kebab.server.network.ServerConnection;
import io.sentry.Sentry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class KebabServer implements Server {
    private static boolean running;
    private static final Logger LOGGER = LoggerFactory.getLogger("Kebab");

    private final ServerConnection serverConnection;
    public KebabServer() {
        if (running) throw new IllegalStateException("Server already instanced");
        Protocol.reload(KebabConfiguration.getVersion());
        this.serverConnection = new ServerConnection();
    }

    public void start() {
        if (running) throw new IllegalStateException("Server is already running");
    }

    public void terminate() {
        if (!running) throw new IllegalStateException("Server already terminated");
        running = false;

    }

    public static void captureException(Throwable exception) {
        if (!KebabConfiguration.isSentryEnabled()) return;
        Sentry.captureException(exception);
    }

    public static Logger logger() {
        return LOGGER;
    }

    public static boolean isRunning() {
        return running;
    }

    @Override
    public Version getVersion() {
        return KebabConfiguration.getVersion();
    }

    @Override
    public void shutdown() {
        terminate();
    }

    @Override
    public synchronized void reload() {
        KebabConfiguration.reload();

    }
}
