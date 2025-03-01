package com.warewise.server.service;

import com.warewise.common.util.protocol.Protocol;
import com.warewise.server.server.Server;
import com.warewise.server.server.ServerConnection;

/**
 * A concrete ServiceHandler for order management commands.
 */
public class OrderManagementServiceHandler extends ServiceHandler {

    public OrderManagementServiceHandler(Server server, ServerConnection connection) {
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
        System.out.println("OrderManagementServiceHandler disconnecting...");
        // Cleanup if needed.
        server.removeClient(this);
    }

    @Override
    public void handleCommand(String command, String[] params) {
        switch (command) {
            case Protocol.CREATE_ORDER:
                if (params.length >= 3) {
                    String customerName = params[0];
                    String itemID = params[1];
                    String quantity = params[2];
                    System.out.println("Creating order for: " + customerName);
                    sendCommand(Protocol.CREATE_ORDER, "Order created for " + customerName);
                } else {
                    sendCommand(Protocol.ERRORTAG, "Invalid parameters for CREATE_ORDER");
                }
                break;
            case Protocol.UPDATE_ORDER:
                if (params.length >= 2) {
                    String orderID = params[0];
                    String status = params[1];
                    System.out.println("Updating order " + orderID + " to status: " + status);
                    sendCommand(Protocol.UPDATE_ORDER, "Order " + orderID + " updated");
                } else {
                    sendCommand(Protocol.ERRORTAG, "Invalid parameters for UPDATE_ORDER");
                }
                break;
            case Protocol.DELETE_ORDER:
                if (params.length >= 1) {
                    String orderID = params[0];
                    System.out.println("Deleting order: " + orderID);
                    sendCommand(Protocol.DELETE_ORDER, "Order " + orderID + " deleted");
                } else {
                    sendCommand(Protocol.ERRORTAG, "Invalid parameters for DELETE_ORDER");
                }
                break;
            case Protocol.LIST_ORDERS:
                System.out.println("Listing orders.");
                String orders = "order1,order2,order3"; // Example
                sendCommand(Protocol.LIST_ORDERS, orders);
                break;
            default:
                sendCommand(Protocol.ERRORTAG, "Invalid command in OrderManagementServiceHandler");
                break;
        }
    }
}
