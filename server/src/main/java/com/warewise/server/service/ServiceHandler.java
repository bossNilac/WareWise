package com.warewise.server.service;

import com.warewise.common.util.protocol.Protocol;
import com.warewise.server.server.Server;
import com.warewise.server.server.ServerConnection;

/**
 * Abstract base class defining a generic service handler.
 * Every service-specific handler must implement these three methods.
 */
public abstract class ServiceHandler {
    protected final Server server;

    public ServerConnection getConnection() {
        return connection;
    }

    protected final ServerConnection connection;

    public ServiceHandler(Server server, ServerConnection connection) {
        this.server = server;
        this.connection = connection;
    }

    // Sends a command with its parameters to the client.
    public abstract void sendCommand(String command, String... params);

    // Handles the disconnection of the client.
    public abstract void handleDisconnect();

    // Handles an incoming command with its parameters.
    public abstract void handleCommand(String command, String[] params);
}
