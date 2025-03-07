package com.warewise.server.service;

import com.warewise.common.logs.AppLogger;
import com.warewise.common.logs.LogLevel;
import com.warewise.common.util.protocol.Protocol;
import com.warewise.server.server.Server;
import com.warewise.server.server.ServerConnection;
import com.warewise.common.model.Item;
import com.warewise.common.model.Category;
import com.warewise.common.util.enums.UserRole;
import com.warewise.server.server.util.ServerUtil;

import java.util.stream.Collectors;

/**
 * A concrete ServiceHandler for item management commands.
 */
public class ItemManagementServiceHandler extends ServiceHandler {

    public ItemManagementServiceHandler(Server server, ServerConnection connection) {
        super(server, connection);
    }

    @Override
    public void handleDisconnect() {
        System.out.println("ItemManagementServiceHandler disconnecting...");
        server.removeClient(this);
    }

    @Override
    public void handleCommand(String command, String[] params) {
        String connectionUser = connection.getAuthHandler().getRegisteredUser();
        String response = null;
//        UserRole connectionRole = connection.getAuthHandler().getRole();

        switch (command) {
            case Protocol.ADD_ITEM:
                if (connectionUser == null) {
                    response=sendCommand(Protocol.ERRORTAG, "Not logged in");
                } else {
                    if (params.length == 6) {
                        int itemID = Integer.parseInt(params[0]);
                        int orderID = Integer.parseInt(params[1]);
                        int quantity = Integer.parseInt(params[2]);
                        double price = Double.parseDouble(params[3]);
                        int inventoryId =  Integer.parseInt(params[4]);
                        int categoryName =  Integer.parseInt(params[5]);

                        Category category = serverUtil.categoryExists(categoryName);
//                        if (category == null) {
//                            response=sendCommand(Protocol.ERRORTAG, "Invalid category");
//                            break;
//                        }

                        Item item = new Item(itemID,orderID, inventoryId, quantity, price, category);
                        server.getDbLoader().addItem(item);
                        response=sendCommand(Protocol.ADD_ITEM, "Item added successfully: " + item.getID());
                    } else {
                        response=sendCommand(Protocol.ERRORTAG, "Invalid parameters for ADD_ITEM");
                    }
                }
                break;

            case Protocol.UPDATE_ITEM:
                if (connectionUser == null) {
                    response=sendCommand(Protocol.ERRORTAG, "Not logged in");
                } else {
                    if (params.length == 6) {
                        int itemID = Integer.parseInt(params[0]);
                        int orderID = Integer.parseInt(params[1]);
                        int quantity = Integer.parseInt(params[2]);
                        double price = Double.parseDouble(params[3]);
                        int inventoryId =  Integer.parseInt(params[4]);
                        int categoryID =  Integer.parseInt(params[5]);
                        Category category = serverUtil.categoryExists(categoryID);
                        Item item = serverUtil.itemExists(itemID);
                        if (item != null) {
                            System.out.println("Updating item " + itemID );
                            if (server.getDbLoader().updateItem(item, orderID, inventoryId,quantity,price,category)) {
                                response=sendCommand(Protocol.UPDATE_ITEM, "Item " + itemID + " updated successfully");
                            } else {
                                response=sendCommand(Protocol.ERRORTAG, "No changes detected for item " + itemID);
                            }
                        } else {
                            response=sendCommand(Protocol.ERRORTAG, "Item does not exist, use ADD_ITEM");
                        }
                    } else {
                        response=sendCommand(Protocol.ERRORTAG, "Invalid parameters for UPDATE_ITEM");
                    }
                }
                break;

            case Protocol.DELETE_ITEM:
                if (connectionUser == null) {
                    response=sendCommand(Protocol.ERRORTAG, "Not logged in");
                } else {
                    if (params.length == 1) {
                        int itemID = Integer.parseInt(params[0]);

                        Item item = serverUtil.itemExists(itemID);
                        if (item != null) {
                            System.out.println("Deleting item: " + itemID);
                            server.getDbLoader().deleteItem(item);
                            response=sendCommand(Protocol.DELETE_ITEM, "Item " + itemID + " deleted successfully");
                        } else {
                            response=sendCommand(Protocol.ERRORTAG, "Item does not exist");
                        }
                    } else {
                        response=sendCommand(Protocol.ERRORTAG, "Invalid parameters for DELETE_ITEM");
                    }
                }
                break;

            case Protocol.LIST_ITEMS:
                System.out.println("Listing items.");
                String items = server.getItems().stream()
                        .map(Item::toString)
                        .collect(Collectors.joining(";"));
                response=sendCommand(Protocol.LIST_ITEMS, items);
                break;

            default:
                response=sendCommand(Protocol.ERRORTAG, "Invalid command in ItemManagementServiceHandler");
                break;
        }
    AppLogger.log(LogLevel.INFO, ServerUtil.SENT_COMMAND+response);
    }
}
