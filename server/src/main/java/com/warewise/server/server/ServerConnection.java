package com.warewise.server.server;

import com.warewise.server.server.ClientHandler;
import com.warewise.server.server.networking.SocketConnection;

import java.io.IOException;
import java.net.Socket;

public class ServerConnection extends SocketConnection implements Runnable {
    private final ClientHandler clientHandler;

    /**
     * Constructs a new ServerConnection with the given socket and client handler.
     */
    public ServerConnection(Socket socket, ClientHandler clientHandler) throws IOException {
        super(socket);
        this.clientHandler = clientHandler;
    }

    /**
     * Runs the connection's receive loop in a thread-pool thread.
     */
    @Override
    public void run() {
        receiveMessages();
    }

    /**
     * If the message starts with "USER ", treat it as a registration command.
     * Otherwise, pass it to the client handler for generic processing.
     */
    @Override
    public void handleMessage(String message) {
        if (message.startsWith("USER ")) {
            String user = message.substring(5).trim();
            clientHandler.handleRegistration(user);
        } else {
            clientHandler.handleMessage(message);
        }
    }

    /**
     * When the connection is closed, let the client handler know.
     */
    @Override
    public void handleDisconnect() {
        clientHandler.handleDisconnect();
    }
}
