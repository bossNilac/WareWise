package com.warewise.server.service;

import com.warewise.common.util.protocol.Protocol;
import com.warewise.server.server.Server;
import com.warewise.server.server.ServerConnection;

/**
 * A concrete ServiceHandler for supplier management commands.
 */
public class SupplierManagementServiceHandler extends ServiceHandler {

    public SupplierManagementServiceHandler(Server server, ServerConnection connection) {
        super(server, connection);
    }

    @Override
    public void handleDisconnect() {
        System.out.println("SupplierManagementServiceHandler disconnecting...");
        server.removeClient(this);
    }

    @Override
    public void handleCommand(String command, String[] params) {
        switch (command) {
            case Protocol.ADD_SUPPLIER:
                if (params.length >= 4) {
                    String name = params[0];
                    String email = params[1];
                    String phone = params[2];
                    String address = params[3];
                    System.out.println("Adding supplier: " + name);
                    sendCommand(Protocol.ADD_SUPPLIER, "Supplier " + name + " added");
                } else {
                    sendCommand(Protocol.ERRORTAG, "Invalid parameters for ADD_SUPPLIER");
                }
                break;
            case Protocol.UPDATE_SUPPLIER:
                if (params.length >= 3) {
                    String supplierID = params[0];
                    String field = params[1];
                    String newValue = params[2];
                    System.out.println("Updating supplier " + supplierID + ": " + field + " -> " + newValue);
                    sendCommand(Protocol.UPDATE_SUPPLIER, "Supplier " + supplierID + " updated");
                } else {
                    sendCommand(Protocol.ERRORTAG, "Invalid parameters for UPDATE_SUPPLIER");
                }
                break;
            case Protocol.DELETE_SUPPLIER:
                if (params.length >= 1) {
                    String supplierID = params[0];
                    System.out.println("Deleting supplier: " + supplierID);
                    sendCommand(Protocol.DELETE_SUPPLIER, "Supplier " + supplierID + " deleted");
                } else {
                    sendCommand(Protocol.ERRORTAG, "Invalid parameters for DELETE_SUPPLIER");
                }
                break;
            case Protocol.LIST_SUPPLIERS:
                System.out.println("Listing suppliers.");
                String suppliers = "supplier1,supplier2,supplier3"; // Example
                sendCommand(Protocol.LIST_SUPPLIERS, suppliers);
                break;
            default:
                sendCommand(Protocol.ERRORTAG, "Invalid command in SupplierManagementServiceHandler");
                break;
        }
    }
}
