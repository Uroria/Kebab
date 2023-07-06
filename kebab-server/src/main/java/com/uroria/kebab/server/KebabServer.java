package com.uroria.kebab.server;

import com.uroria.kebab.protocol.Protocol;
import io.sentry.Sentry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class KebabServer {
    private static final Logger LOGGER = LoggerFactory.getLogger("Kebab");

    public KebabServer() {
        Protocol.reload(KebabConfiguration.getVersion());
    }

    public void start() {

    }

    public void terminate() {

    }

    public static void captureException(Throwable exception) {
        if (!KebabConfiguration.isSentryEnabled()) return;
        Sentry.captureException(exception);
    }

    public static Logger logger() {
        return LOGGER;
    }
}
