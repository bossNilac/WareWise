package com.warewise.server.service;

import com.warewise.common.util.protocol.Protocol;
import com.warewise.server.server.Server;
import com.warewise.server.server.ServerConnection;
import com.warewise.common.model.Supplier;

import java.util.stream.Collectors;

/**
 * A concrete ServiceHandler for supplier management commands.
 */
public class SupplierManagementServiceHandler extends ServiceHandler {

    public SupplierManagementServiceHandler(Server server, ServerConnection connection) {
        super(server, connection);
    }

    @Override
    public void sendCommand(String command, String... params) {
        // Implementation not provided as per request.
    }

    @Override
    public void handleDisconnect() {
        System.out.println("SupplierManagementServiceHandler disconnecting...");
        server.removeClient(this);
    }

    @Override
    public void handleCommand(String command, String[] params) {
        String connectionUser = connection.getAuthHandler().getRegisteredUser();

        switch (command) {
            case Protocol.ADD_SUPPLIER:
                if (connectionUser == null) {
                    sendCommand(Protocol.ERRORTAG, "Not logged in");
                } else {
                    if (params.length == 4) {
                        String name = params[0];
                        String contactEmail = params[1];
                        String contactPhoneNo = params[2];
                        String address = params[3];

                        Supplier supplier = new Supplier(name, contactEmail, contactPhoneNo, address);
                        server.getDbLoader().addSupplier(supplier);
                        sendCommand(Protocol.ADD_SUPPLIER, "Supplier added successfully: " + supplier.getID());
                    } else {
                        sendCommand(Protocol.ERRORTAG, "Invalid parameters for ADD_SUPPLIER");
                    }
                }
                break;

            case Protocol.UPDATE_SUPPLIER:
                if (connectionUser == null) {
                    sendCommand(Protocol.ERRORTAG, "Not logged in");
                } else {
                    if (params.length == 5) {
                        int supplierID = Integer.parseInt(params[0]);
                        String name = params[1];
                        String contactEmail = params[2];
                        String contactPhoneNo = params[3];
                        String address = params[4];

                        Supplier supplier = serverUtil.supplierExists(supplierID);
                        if (supplier != null) {
                            server.getDbLoader().updateSupplier(supplier, name, contactEmail, contactPhoneNo, address);
                            sendCommand(Protocol.UPDATE_SUPPLIER, "Supplier " + supplierID + " updated successfully");
                        } else {
                            sendCommand(Protocol.ERRORTAG, "Supplier does not exist");
                        }
                    } else {
                        sendCommand(Protocol.ERRORTAG, "Invalid parameters for UPDATE_SUPPLIER");
                    }
                }
                break;

            case Protocol.DELETE_SUPPLIER:
                if (connectionUser == null) {
                    sendCommand(Protocol.ERRORTAG, "Not logged in");
                } else {
                    if (params.length == 1) {
                        int supplierID = Integer.parseInt(params[0]);

                        Supplier supplier = serverUtil.supplierExists(supplierID);
                        if (supplier != null) {
                            server.getDbLoader().deleteSupplier(supplier);
                            sendCommand(Protocol.DELETE_SUPPLIER, "Supplier " + supplierID + " deleted successfully");
                        } else {
                            sendCommand(Protocol.ERRORTAG, "Supplier does not exist");
                        }
                    } else {
                        sendCommand(Protocol.ERRORTAG, "Invalid parameters for DELETE_SUPPLIER");
                    }
                }
                break;

            case Protocol.LIST_SUPPLIERS:
                System.out.println("Listing suppliers.");
                String suppliers = server.getSuppliers().stream()
                        .map(Supplier::toString)
                        .collect(Collectors.joining(";"));
                sendCommand(Protocol.LIST_SUPPLIERS, suppliers);
                break;

            default:
                sendCommand(Protocol.ERRORTAG, "Invalid command in SupplierManagementServiceHandler");
                break;
        }
    }
}
