package com.warewise.server.server;

import com.warewise.server.server.networking.SocketServer;
import com.warewise.server.service.ServiceHandler;

import java.io.IOException;
import java.net.Socket;
import java.util.Collections;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Server class accepts client connections and stores them.
 * Now it tracks ServerConnection objects (each of which internally manages its own set of ServiceHandlers).
 */
public class Server extends SocketServer {
    // Store active connections.
    private final Set<ServerConnection> connections = Collections.newSetFromMap(new ConcurrentHashMap<>());
    // A simple set of logged-in usernames.
    private final Set<String> list = Collections.synchronizedSet(new java.util.HashSet<>());

    protected Server(int port) throws IOException {
        super(port);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int port = 0;
        boolean validPort = false;

        while (!validPort) {
            System.out.println("Enter port number: ");
            if (sc.hasNextInt()) {
                port = sc.nextInt();

                if (port >= 0 && port <= 65536) {
                    validPort = true;
                } else {
                    System.out.println("Enter a valid port number: ");
                }
            } else {
                System.out.println("That's not a number. Please enter a number.");
                sc.next();
            }
        }
        try {
            Server server = new Server(port);
            System.out.println("Server started on port " + server.getPort() + ".");
            server.acceptConnections();
        } catch (IOException e) {
            System.out.println("Port " + port + " is already in use.");
        }
    }


    @Override
    public int getPort() {
        return super.getPort();
    }

    @Override
    protected void handleConnection(Socket socket) {
        try {
            ServerConnection connection = new ServerConnection(socket, this);
            addClient(connection);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void acceptConnections() throws IOException {
        super.acceptConnections();
    }

    @Override
    public synchronized void close() {
        super.close();
    }

    /**
     * Adds a new connection to the set of active connections.
     */
    public void addClient(ServerConnection connection) {
        connections.add(connection);
        System.out.println("Added client connection.");
    }

    /**
     * Removes a connection based on a service handler's associated connection.
     * Each ServiceHandler can call this method when it detects a disconnect.
     */
    public void removeClient(ServiceHandler handler) {
        connections.remove(handler.getConnection());
        System.out.println("Removed client connection.");
    }

    // Getters and utility methods.

    public Set<String> getList() {
        System.out.println("Sending list of logged in users.");
        return list;
    }

    public String getHello() {
        return "Welcome to WARE WISE server";
    }

    public void addToList(String username) {
        list.add(username);
    }

    public void removeFromList(String username) {
        list.remove(username);
    }

    public boolean isLoggedIn(String username) {
        return list.contains(username);
    }
}



