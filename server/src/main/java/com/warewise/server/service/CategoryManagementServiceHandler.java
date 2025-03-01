package com.warewise.server.service;

import com.warewise.common.util.protocol.Protocol;
import com.warewise.server.server.Server;
import com.warewise.server.server.ServerConnection;

/**
 * A concrete ServiceHandler for category management commands.
 */
public class CategoryManagementServiceHandler extends ServiceHandler {

    public CategoryManagementServiceHandler(Server server, ServerConnection connection) {
        super(server, connection);
    }

    @Override
    public void sendCommand(String command, String... params) {
        StringBuilder sb = new StringBuilder(command);
        for (String param : params) {
            sb.append(Protocol.SEPARATOR).append(param);
        }
        connection.sendMessage(sb.toString());
    }

    @Override
    public void handleDisconnect() {
        System.out.println("CategoryManagementServiceHandler disconnecting...");
        // Cleanup if needed.
        server.removeClient(this);
    }

    @Override
    public void handleCommand(String command, String[] params) {
        switch (command) {
            case Protocol.ADD_CATEGORY:
                if (params.length >= 2) {
                    String categoryName = params[0];
                    String description = params[1];
                    System.out.println("Adding category: " + categoryName);
                    sendCommand(Protocol.ADD_CATEGORY, "Category " + categoryName + " added");
                } else {
                    sendCommand(Protocol.ERRORTAG, "Invalid parameters for ADD_CATEGORY");
                }
                break;
            case Protocol.UPDATE_CATEGORY:
                if (params.length >= 3) {
                    String categoryID = params[0];
                    String field = params[1];
                    String newValue = params[2];
                    System.out.println("Updating category " + categoryID + ": " + field + " -> " + newValue);
                    sendCommand(Protocol.UPDATE_CATEGORY, "Category " + categoryID + " updated");
                } else {
                    sendCommand(Protocol.ERRORTAG, "Invalid parameters for UPDATE_CATEGORY");
                }
                break;
            case Protocol.DELETE_CATEGORY:
                if (params.length >= 1) {
                    String categoryID = params[0];
                    System.out.println("Deleting category: " + categoryID);
                    sendCommand(Protocol.DELETE_CATEGORY, "Category " + categoryID + " deleted");
                } else {
                    sendCommand(Protocol.ERRORTAG, "Invalid parameters for DELETE_CATEGORY");
                }
                break;
            case Protocol.LIST_CATEGORIES:
                System.out.println("Listing categories.");
                String categories = "cat1,cat2,cat3"; // Example
                sendCommand(Protocol.LIST_CATEGORIES, categories);
                break;
            default:
                sendCommand(Protocol.ERRORTAG, "Invalid command in CategoryManagementServiceHandler");
                break;
        }
    }
}
