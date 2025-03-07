package com.warewise.server.service;

import com.warewise.common.logs.AppLogger;
import com.warewise.common.logs.LogLevel;
import com.warewise.common.util.protocol.Protocol;
import com.warewise.server.server.Server;
import com.warewise.server.server.ServerConnection;
import com.warewise.common.model.Category;
import com.warewise.common.util.enums.UserRole;
import com.warewise.server.server.util.ServerUtil;

import java.util.stream.Collectors;

/**
 * A concrete ServiceHandler for category management commands.
 */
public class CategoryManagementServiceHandler extends ServiceHandler {

    public CategoryManagementServiceHandler(Server server, ServerConnection connection) {
        super(server, connection);
    }

    @Override
    public void handleDisconnect() {
        System.out.println("CategoryManagementServiceHandler disconnecting...");
        // Cleanup if needed.
        server.removeClient(this);
    }

    @Override
    public void handleCommand(String command, String[] params) {
        String connectionUser = connection.getAuthHandler().getRegisteredUser();
        UserRole connectionRole = connection.getAuthHandler().getRole();
        String response=null;

        switch (command) {
            case Protocol.ADD_CATEGORY:
                if (connectionUser == null) {
                    response=sendCommand(Protocol.ERRORTAG, "Not logged in");
                } else {
                    if (params.length == 2) {
                        String categoryName = params[0];
                        String description = params[1];

                        if (serverUtil.categoryExists(categoryName) == null) {
                            Category category = new Category(categoryName, description);
                            server.getDbLoader().addCategory(category);
                            response=sendCommand(Protocol.ADD_CATEGORY, "Category " + categoryName + " added successfully");
                        } else {
                            response=sendCommand(Protocol.ERRORTAG, "Category already exists, use UPDATE_CATEGORY");
                        }
                    } else {
                        response=sendCommand(Protocol.ERRORTAG, "Invalid parameters for ADD_CATEGORY");
                    }
                }
                break;

            case Protocol.UPDATE_CATEGORY:
                if (connectionUser == null) {
                    response=sendCommand(Protocol.ERRORTAG, "Not logged in");
                } else {
                    if (connectionRole != UserRole.ADMIN) {
                        response=sendCommand(Protocol.ERRORTAG, "Not admin rights");
                        break;
                    }
                    if (params.length == 3) {
                        int categoryID = Integer.parseInt(params[0]);
                        String name = params[1];
                        String description = params[2];

                        Category category = serverUtil.categoryExists(categoryID);
                        if (category != null) {
                            if (server.getDbLoader().updateCategory(category, name, description)) {
                                response=sendCommand(Protocol.UPDATE_CATEGORY, "Category " + categoryID + " updated successfully");
                            } else {
                                response=sendCommand(Protocol.ERRORTAG, "No changes detected for category " + categoryID);
                            }
                        } else {
                            response=sendCommand(Protocol.ERRORTAG, "Category does not exist, use ADD_CATEGORY");
                        }
                    } else {
                        response=sendCommand(Protocol.ERRORTAG, "Invalid parameters for UPDATE_CATEGORY");
                    }
                }
                break;

            case Protocol.DELETE_CATEGORY:
                if (connectionUser == null) {
                    response=sendCommand(Protocol.ERRORTAG, "Not logged in");
                } else {
                    if (connectionRole != UserRole.ADMIN) {
                        response=sendCommand(Protocol.ERRORTAG, "Not admin rights");
                        break;
                    }
                    if (params.length == 1) {
                        String categoryID = params[0];
                        Category category = serverUtil.categoryExists(Integer.parseInt(categoryID));
                        if (category != null) {
                            server.getDbLoader().deleteCategory(category);
                            response=sendCommand(Protocol.DELETE_CATEGORY, "Category " + categoryID + " deleted successfully");
                        } else {
                            response=sendCommand(Protocol.ERRORTAG, "Category does not exist");
                        }
                    } else {
                        response=sendCommand(Protocol.ERRORTAG, "Invalid parameters for DELETE_CATEGORY");
                    }
                }
                break;

            case Protocol.LIST_CATEGORIES:
                String categories = server.getCategories().stream()
                        .map(Category::getName)
                        .collect(Collectors.joining(","));
                response=sendCommand(Protocol.LIST_CATEGORIES, categories);
                break;

            default:
                response=sendCommand(Protocol.ERRORTAG, "Invalid command in CategoryManagementServiceHandler");
                break;
        }
        AppLogger.log(LogLevel.INFO, ServerUtil.SENT_COMMAND+response);

    }

}
