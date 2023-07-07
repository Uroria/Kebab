package org.kebab.server.network;

import org.kebab.server.KebabConfiguration;
import org.kebab.server.KebabServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public final class ServerConnection extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger("ServerConnection");

    private final List<ClientConnection> clientConnections;

    public ServerConnection() {
        this.clientConnections = new ArrayList<>();
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(KebabConfiguration.getPort(), 50, KebabConfiguration.getAddress().getAddress())) {
            while (KebabServer.isRunning()) {
                Socket socket = serverSocket.accept();
                ClientConnection connection = new ClientConnection(socket);
                this.clientConnections.add(connection);
                connection.start();
            }
            LOGGER.info("Closing connections");
        } catch (Exception exception) {
            LOGGER.error("Unhandled exception in ServerSocket", exception);
            KebabServer.captureException(exception);
            System.exit(1);
        }
    }

    public List<ClientConnection> getClients() {
        return clientConnections;
    }
}
