package com.warewise.server.service;

import com.warewise.common.util.protocol.Protocol;
import com.warewise.server.server.Server;
import com.warewise.server.server.ServerConnection;

/**
 * A concrete ServiceHandler for inventory management commands.
 */
public class InventoryManagementServiceHandler extends ServiceHandler {

    public InventoryManagementServiceHandler(Server server, ServerConnection connection) {
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
        System.out.println("InventoryManagementServiceHandler disconnecting...");
        // Cleanup if needed.
        server.removeClient(this);
    }

    @Override
    public void handleCommand(String command, String[] params) {
        switch (command) {
            case Protocol.ADD_ITEM:
                if (params.length >= 5) {
                    String productName = params[0];
                    String description = params[1];
                    String quantity = params[2];
                    String price = params[3];
                    String supplierID = params[4];
                    System.out.println("Adding item: " + productName);
                    sendCommand(Protocol.ADD_ITEM, "Item " + productName + " added successfully");
                } else {
                    sendCommand(Protocol.ERRORTAG, "Invalid parameters for ADD_ITEM");
                }
                break;
            case Protocol.UPDATE_ITEM:
                if (params.length >= 3) {
                    String productID = params[0];
                    String field = params[1];
                    String newValue = params[2];
                    System.out.println("Updating item " + productID + ": " + field + " -> " + newValue);
                    sendCommand(Protocol.UPDATE_ITEM, "Item " + productID + " updated successfully");
                } else {
                    sendCommand(Protocol.ERRORTAG, "Invalid parameters for UPDATE_ITEM");
                }
                break;
            case Protocol.DELETE_ITEM:
                if (params.length >= 1) {
                    String productID = params[0];
                    System.out.println("Deleting item: " + productID);
                    sendCommand(Protocol.DELETE_ITEM, "Item " + productID + " deleted successfully");
                } else {
                    sendCommand(Protocol.ERRORTAG, "Invalid parameters for DELETE_ITEM");
                }
                break;
            case Protocol.LIST_ITEMS:
                // Here you would fetch the item list from your inventory system.
                System.out.println("Listing items.");
                String items = "item1,item2,item3"; // Example
                sendCommand(Protocol.LIST_ITEMS, items);
                break;
            default:
                sendCommand(Protocol.ERRORTAG, "Invalid command in InventoryManagementServiceHandler");
                break;
        }
    }
}
