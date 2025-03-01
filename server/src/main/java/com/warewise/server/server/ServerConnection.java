package com.warewise.server.server;

import com.warewise.server.server.networking.SocketConnection;
import com.warewise.server.service.AuthHandler;
import com.warewise.server.service.ServiceHandler;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class ServerConnection extends SocketConnection implements Runnable {

    /**
     * Constructs a new ServerConnection with the given socket and client handler.
     */

    private ThreadPoolSocketServer server;
    private AuthHandler authHandler;


    public ServerConnection(Socket socket,ThreadPoolSocketServer server) throws IOException {
        super(socket);
        this.server = server;
        authHandler=new AuthHandler(this);
    }

    /**
     * Runs the connection's receive loop in a thread-pool thread.
     */
    @Override
    public void run() {
        receiveMessages();
    }



    @Override
    public void handleMessage(String message) {

    }

    /**
     * When the connection is closed, let the client handler know.
     */
    @Override
    public void handleDisconnect() {

    }

    public ThreadPoolSocketServer getServer() {
        return server;
    }

}
