package com.warewise.server.service;

import com.warewise.common.util.protocol.Protocol;
import com.warewise.server.server.Server;
import com.warewise.server.server.ServerConnection;

/**
 * A concrete ServiceHandler for user management commands.
 */
public class UserManagementServiceHandler extends ServiceHandler {

    public UserManagementServiceHandler(Server server, ServerConnection connection) {
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
        System.out.println("UserManagementServiceHandler disconnecting...");
        // Add any cleanup for user management if necessary.
        server.removeClient(this);
    }

    @Override
    public void handleCommand(String command, String[] params) {
        switch (command) {
            case Protocol.ADD_USER:
                if (params.length >= 4) {
                    String username = params[0];
                    String role = params[1];
                    String email = params[2];
                    String password = params[3];
                    // Process the addition of a new user.
                    System.out.println("Adding user: " + username);
                    sendCommand(Protocol.ADD_USER, "User " + username + " added successfully");
                } else {
                    sendCommand(Protocol.ERRORTAG, "Invalid parameters for ADD_USER");
                }
                break;
            case Protocol.UPDATE_USER:
                if (params.length >= 3) {
                    String userID = params[0];
                    String field = params[1];
                    String newValue = params[2];
                    // Process update of user info.
                    System.out.println("Updating user " + userID + ": " + field + " -> " + newValue);
                    sendCommand(Protocol.UPDATE_USER, "User " + userID + " updated successfully");
                } else {
                    sendCommand(Protocol.ERRORTAG, "Invalid parameters for UPDATE_USER");
                }
                break;
            case Protocol.DELETE_USER:
                if (params.length >= 1) {
                    String userID = params[0];
                    // Process deletion of user.
                    System.out.println("Deleting user: " + userID);
                    sendCommand(Protocol.DELETE_USER, "User " + userID + " deleted successfully");
                } else {
                    sendCommand(Protocol.ERRORTAG, "Invalid parameters for DELETE_USER");
                }
                break;
            case Protocol.LIST_USERS:
                // For example, we return a comma-separated list of users.
                System.out.println("Listing users.");
                String users = String.join(",", server.getList());
                sendCommand(Protocol.LIST_USERS, users);
                break;
            default:
                sendCommand(Protocol.ERRORTAG, "Invalid command in UserManagementServiceHandler");
                break;
        }
    }
}
