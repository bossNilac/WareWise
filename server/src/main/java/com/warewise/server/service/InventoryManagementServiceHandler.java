package com.warewise.server.service;

import com.warewise.common.logs.AppLogger;
import com.warewise.common.logs.LogLevel;
import com.warewise.common.util.protocol.Protocol;
import com.warewise.server.server.Server;
import com.warewise.server.server.ServerConnection;
import com.warewise.common.model.Inventory;
import com.warewise.common.util.enums.UserRole;
import com.warewise.server.server.util.ServerUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

/**
 * A concrete ServiceHandler for inventory management commands.
 */
public class InventoryManagementServiceHandler extends ServiceHandler {

    public InventoryManagementServiceHandler(Server server, ServerConnection connection) {
        super(server, connection);
    }

    @Override
    public void handleDisconnect() {
        System.out.println("InventoryManagementServiceHandler disconnecting...");
        server.removeClient(this);
    }

    @Override
    public void handleCommand(String command, String[] params) {
        String connectionUser = connection.getAuthHandler().getRegisteredUser();
        String response;
        switch (command) {
            case Protocol.ADD_INVENTORY:
                if (connectionUser == null) {
                    response=sendCommand(Protocol.ERRORTAG, "Not logged in");
                } else {
                    if (params.length == 4) {
                        String name = params[0];
                        String description = params[1];
                        int quantity = Integer.parseInt(params[2]);
                        String lastUpdated = params[3];

                        Inventory inventory = new Inventory(name, description, quantity, lastUpdated);
                        server.getDbLoader().addInventory(inventory);
                        response=sendCommand(Protocol.ADD_INVENTORY, "Inventory added successfully: " + inventory.getID());
                    } else {
                        response=sendCommand(Protocol.ERRORTAG, "Invalid parameters for ADD_INVENTORY");
                    }
                }
                break;

            case Protocol.UPDATE_INVENTORY:
                if (connectionUser == null) {
                    response=sendCommand(Protocol.ERRORTAG, "Not logged in");
                } else {
                    if (params.length == 5) {
                        int inventoryID = Integer.parseInt(params[0]);
                        String name = params[1];
                        String description = params[2];
                        int quantity = Integer.parseInt(params[3]);
                        String lastUpdated = params[4];
                        String formattedDate = LocalDateTime.now().format(
                                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
                        if(lastUpdated == null){
                            lastUpdated = formattedDate;
                        }
                        Inventory inventory = serverUtil.inventoryExists(inventoryID);
                        if (inventory != null) {
                            System.out.println("Updating inventory " + inventoryID);
                            if (server.getDbLoader().updateInventory(inventory, name, description, quantity, lastUpdated)) {
                                response=sendCommand(Protocol.UPDATE_INVENTORY, "Inventory " + inventoryID + " updated successfully");
                            } else {
                                response=sendCommand(Protocol.ERRORTAG, "No changes detected for inventory " + inventoryID);
                            }
                        } else {
                            response=sendCommand(Protocol.ERRORTAG, "Inventory does not exist, use ADD_INVENTORY");
                        }
                    } else {
                        response=sendCommand(Protocol.ERRORTAG, "Invalid parameters for UPDATE_INVENTORY");
                    }
                }
                break;

            case Protocol.DELETE_INVENTORY:
                if (connectionUser == null) {
                    response=sendCommand(Protocol.ERRORTAG, "Not logged in");
                } else {
                    if (params.length == 1) {
                        int inventoryID = Integer.parseInt(params[0]);

                        Inventory inventory = serverUtil.inventoryExists(inventoryID);
                        if (inventory != null) {
                            System.out.println("Deleting inventory: " + inventoryID);
                            server.getDbLoader().deleteInventory(inventory);
                            response=sendCommand(Protocol.DELETE_INVENTORY, "Inventory " + inventoryID + " deleted successfully");
                        } else {
                            response=sendCommand(Protocol.ERRORTAG, "Inventory does not exist");
                        }
                    } else {
                        response=sendCommand(Protocol.ERRORTAG, "Invalid parameters for DELETE_INVENTORY");
                    }
                }
                break;

            case Protocol.LIST_INVENTORY:
                System.out.println("Listing inventory.");
                String inventories = server.getInventories().stream()
                        .map(Inventory::toString)
                        .collect(Collectors.joining(Protocol.SEPARATOR));
                response=sendCommand(Protocol.LIST_INVENTORY, inventories);
                break;

            default:
                response=sendCommand(Protocol.ERRORTAG, "Invalid command in InventoryManagementServiceHandler");
                break;
        }
    AppLogger.log(LogLevel.INFO, ServerUtil.SENT_COMMAND+response);
    }
}
