package com.warewise.server.server;

import com.warewise.server.server.networking.SocketConnection;
import com.warewise.common.util.protocol.Protocol;
import com.warewise.server.service.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;

/**
 * Represents the server's connection to a single client.
 * It now instantiates all the service handlers and delegates each incoming
 * command to the correct handler based on the protocol.
 */
public class ServerConnection extends SocketConnection implements Runnable {
    private final Socket socket;

    // Instantiate all service handlers for this connection.
    private final AuthenticationServiceHandler authHandler;
    private final UserManagementServiceHandler userHandler;
    private final InventoryManagementServiceHandler inventoryHandler;
    private final ItemManagementServiceHandler itemHandler;
    private final CategoryManagementServiceHandler categoryHandler;
    private final OrderManagementServiceHandler orderHandler;
    private final SupplierManagementServiceHandler supplierHandler;
    private final StockAlertServiceHandler stockAlertHandler;
    private final ReportingServiceHandler reportingHandler;
    private final SystemServiceHandler systemHandler;

    public ServerConnection(Socket socket, Server server) throws IOException {
        super(socket);
        this.socket = socket;
        // Initialize all handlers, passing this connection and the server.
        authHandler = new AuthenticationServiceHandler(server, this);
        userHandler = new UserManagementServiceHandler(server, this);
        itemHandler = new ItemManagementServiceHandler(server, this);
        inventoryHandler = new InventoryManagementServiceHandler(server, this);
        categoryHandler = new CategoryManagementServiceHandler(server, this);
        orderHandler = new OrderManagementServiceHandler(server, this);
        supplierHandler = new SupplierManagementServiceHandler(server, this);
        stockAlertHandler = new StockAlertServiceHandler(server, this);
        reportingHandler = new ReportingServiceHandler(server, this);
        systemHandler = new SystemServiceHandler(server, this);

        new Thread(this).start();
    }

    /**
     * Determines which handler should process a given command.
     */
    private ServiceHandler getHandlerForCommand(String command) {
        if (command.equals(Protocol.HELLO) || command.equals(Protocol.LOGIN) || command.equals(Protocol.LOGOUT)) {
            return authHandler;
        } else if (command.equals(Protocol.ADD_USER) || command.equals(Protocol.UPDATE_USER) ||
                command.equals(Protocol.DELETE_USER) || command.equals(Protocol.LIST_USERS)) {
            return userHandler;
        } else if (command.equals(Protocol.ADD_ITEM) || command.equals(Protocol.UPDATE_ITEM) ||
                command.equals(Protocol.DELETE_ITEM) || command.equals(Protocol.LIST_ITEMS)) {
            return itemHandler;
        } else if (command.equals(Protocol.ADD_INVENTORY) || command.equals(Protocol.UPDATE_INVENTORY) ||
                command.equals(Protocol.DELETE_INVENTORY) || command.equals(Protocol.LIST_INVENTORY)) {
            return inventoryHandler;
        } else if (command.equals(Protocol.ADD_CATEGORY) || command.equals(Protocol.UPDATE_CATEGORY) ||
                command.equals(Protocol.DELETE_CATEGORY) || command.equals(Protocol.LIST_CATEGORIES)) {
            return categoryHandler;
        } else if (command.equals(Protocol.CREATE_ORDER) || command.equals(Protocol.UPDATE_ORDER) ||
                command.equals(Protocol.DELETE_ORDER) || command.equals(Protocol.LIST_ORDERS)) {
            return orderHandler;
        } else if (command.equals(Protocol.ADD_SUPPLIER) || command.equals(Protocol.UPDATE_SUPPLIER) ||
                command.equals(Protocol.DELETE_SUPPLIER) || command.equals(Protocol.LIST_SUPPLIERS)) {
            return supplierHandler;
        } else if (command.equals(Protocol.STOCK_ALERT) || command.equals(Protocol.RESOLVE_ALERT)) {
            return stockAlertHandler;
        } else if (command.equals(Protocol.GENERATE_REPORT) || command.equals(Protocol.REPORT_RESULT)) {
            return reportingHandler;
        } else if (command.equals(Protocol.SERVER_ERROR) || command.equals(Protocol.INVALID_COMMAND) ||
                command.equals(Protocol.HEARTBEAT)) {
            return systemHandler;
        }
        // Fallback for unknown commands.
        return systemHandler;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()))) {
            String message;
            while ((message = in.readLine()) != null) {
                handleMessage(message);
            }
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        } finally {
            handleDisconnect();
        }
    }

    @Override
    protected void handleMessage(String message) {
        try {
            // Split the message based on the protocol separator.
            String[] parts = message.split(Protocol.SEPARATOR);
            if (parts.length > 0) {
                String command = parts[0];
                String[] params = Arrays.copyOfRange(parts, 1, parts.length);
                // Delegate to the correct handler.
                ServiceHandler handler = getHandlerForCommand(command);
                handler.handleCommand(command, params);
            }
        } catch (RuntimeException e) {
            System.err.println("Error processing command: " + e.getMessage());
            systemHandler.sendCommand(Protocol.ERRORTAG, "Invalid command format");
        }
    }

    @Override
    protected void handleDisconnect() {
        authHandler.handleDisconnect();
        userHandler.handleDisconnect();
        inventoryHandler.handleDisconnect();
        categoryHandler.handleDisconnect();
        orderHandler.handleDisconnect();
        supplierHandler.handleDisconnect();
        stockAlertHandler.handleDisconnect();
        reportingHandler.handleDisconnect();
        systemHandler.handleDisconnect();
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Error closing socket: " + e.getMessage());
        }
    }

    // Expose a method to send a message.
    public boolean sendMessage(String message) {
        super.sendMessage(message);
        return true;
    }

    public AuthenticationServiceHandler getAuthHandler() {
        return authHandler;
    }

}
