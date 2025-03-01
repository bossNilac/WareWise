package com.warewise.server.service;

import com.warewise.server.server.ServerConnection;
import com.warewise.server.server.ThreadPoolSocketServer;

public abstract class ServiceHandler {
    protected ServerConnection connection;

    /**
     * Associates this handler with its ServerConnection.
     */

    public ServiceHandler(ServerConnection connection){
        this.connection = connection;
    }

    /**
     * Handles a generic message. If the client is registered, it echoes the message.
     * Otherwise, prompts the client to register.
     */
    public abstract void handleMessage(String message);

    /**
     * Handles a client disconnect.
     */
    public abstract void handleDisconnect();
}