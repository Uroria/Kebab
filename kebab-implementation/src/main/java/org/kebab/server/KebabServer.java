package org.kebab.server;

import org.kebab.api.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class KebabServer implements Server {
    private static final Logger LOGGER = LoggerFactory.getLogger("Kebab");

    public void start() {
        startApplication();
    }

    public void terminate() {
        terminateApplication();
    }

    public native void startApplication();

    public native void terminateApplication();

    @Override
    public void shutdown() {
        terminate();
    }

    public static void info(String input) {
        logger().info(input);
    }

    public static Logger logger() {
        return LOGGER;
    }
}
