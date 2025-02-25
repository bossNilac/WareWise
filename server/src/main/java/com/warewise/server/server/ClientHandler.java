package com.warewise.server.server;

public class ClientHandler {
    private ServerConnection connection;
    private String registeredUser = null;

    /**
     * Associates this handler with its ServerConnection.
     */
    public void setConnection(ServerConnection connection) {
        this.connection = connection;
    }

    /**
     * Registers the client if not already registered.
     */
    public void handleRegistration(String user) {
        if (registeredUser != null) {
            System.out.println("User already registered. Ignoring registration for: " + user);
            connection.sendMessage("You are already registered as " + registeredUser + ".");
        } else {
            registeredUser = user;
            System.out.println("User registered: " + registeredUser);
            connection.sendMessage("Welcome, " + registeredUser + "!");
        }
    }

    /**
     * Handles a generic message. If the client is registered, it echoes the message.
     * Otherwise, prompts the client to register.
     */
    public void handleMessage(String message) {
        if (registeredUser != null) {
            System.out.println("Received from " + registeredUser + ": " + message);
            connection.sendMessage("Echo: " + message);
        } else {
            System.out.println("Received message from unregistered client: " + message);
            connection.sendMessage("Please register first using 'USER <username>'.");
        }
    }

    /**
     * Handles a client disconnect.
     */
    public void handleDisconnect() {
        System.out.println("Client disconnected: " + (registeredUser != null ? registeredUser : "Unknown user"));
    }
}
