package com.warewise.server.server;

import com.warewise.server.server.networking.SocketServer;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolSocketServer extends SocketServer {
    private final ExecutorService executor;

    /**
     * Constructs a new ThreadPoolSocketServer that listens on the given port using a fixed thread pool.
     * @param port the port on which to listen for connections.
     * @param poolSize the number of threads in the pool.
     * @throws IOException if an I/O error occurs when opening the socket.
     */
    public ThreadPoolSocketServer(int port, int poolSize) throws IOException {
        super(port);
        this.executor = Executors.newFixedThreadPool(poolSize);
    }

    /**
     * For each accepted connection, create a new ClientHandler and ServerConnection,
     * and submit the connection to the thread pool.
     */
    @Override
    protected void handleConnection(Socket socket) {
        try {
            ClientHandler handler = new ClientHandler();
            ServerConnection connection = new ServerConnection(socket, handler);
            handler.setConnection(connection);
            executor.submit(connection);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shut down the server socket and the thread pool.
     */
    public void shutdown() {
        close();
        executor.shutdown();
    }
}
