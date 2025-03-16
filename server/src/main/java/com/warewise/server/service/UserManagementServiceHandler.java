package com.warewise.server.service;

import com.warewise.common.encryption.Encrypt;
import com.warewise.common.logs.AppLogger;
import com.warewise.common.logs.LogLevel;
import com.warewise.common.model.Supplier;
import com.warewise.common.model.User;
import com.warewise.common.util.enums.UserRole;
import com.warewise.common.util.protocol.Protocol;
import com.warewise.server.server.Server;
import com.warewise.server.server.ServerConnection;
import com.warewise.server.server.util.ServerUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * A concrete ServiceHandler for user management commands.
 */
 public class UserManagementServiceHandler extends ServiceHandler {

    public UserManagementServiceHandler(Server server, ServerConnection connection) {
        super(server, connection);
    }

    @Override
    public void handleDisconnect() {
        System.out.println("UserManagementServiceHandler disconnecting...");
        server.removeClient(this);
    }

    @Override
    public void handleCommand(String command, String[] params) {
        String connectionUser = connection.getAuthHandler().getRegisteredUser();
        UserRole connectionRole = connection.getAuthHandler().getRole();
        String response = null;
        switch (command) {
            case Protocol.ADD_USER:
                if (connectionUser == null) {
                    response=sendCommand(Protocol.ERRORTAG, "Not logged in");
                } else {
                        if (params.length == 4) {
                            String username = params[0];
                            if (!connectionUser.equals(username) ) {
                                String role = params[2];
                                String email = params[3];
                                String password = Encrypt.hashPassword(params[1]);
                                String formattedDate = LocalDateTime.now().format(
                                        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
                                User user = new User(formattedDate, email,
                                        UserRole.valueOf(role), password, username);
                                if(serverUtil.userExists(username) == null){
                                    System.out.println("Adding user: " + username);
                                    server.getDbLoader().addUser(user);
                                    response=sendCommand(Protocol.ADD_USER, "User " + username + " added successfully");
                                }else {
                                    response=sendCommand(Protocol.ERRORTAG, "Use  UPDATE_USER");
                                }
                            } else {
                                response=sendCommand(Protocol.ERRORTAG, "Cannot add own user");
                            }
                    } else{
                        response=sendCommand(Protocol.ERRORTAG, "Invalid parameters for ADD_USER");
                    }
                }
                break;
            case Protocol.UPDATE_USER:
                if (connectionUser == null) {
                    response=sendCommand(Protocol.ERRORTAG, "Not logged in");
                } else {
                    if (connectionRole != UserRole.ADMIN){
                        response=sendCommand(Protocol.ERRORTAG, "Not admin rights");
                        break;
                    }
                    if (params.length == 4) {
                        String username = params[0];
                        if (!connectionUser.equals(username)  && !server.getUserList().contains(username)) {
                            String role = params[2];
                            String email = params[3];
                            String password = Encrypt.hashPassword(params[1]);

                            User user = serverUtil.userExists(username);
                            if(serverUtil.userExists(username)!= null){
                                System.out.println("Updating user: " + username);
                                if (server.getDbLoader().updateUser(user,role,email,password)){
                                    response=sendCommand(Protocol.UPDATE_USER, "User " + username + "was added successfully");
                                }else {
                                    response=sendCommand(Protocol.ERRORTAG, "Params for " + username + "were identical to the DB");
                                }
                            }else {
                                response=sendCommand(Protocol.ERRORTAG, "Use  ADD_USER");
                            }
                        } else {
                            response=sendCommand(Protocol.ERRORTAG, "Cannot modify logged in user");
                        }
                    } else{
                        response=sendCommand(Protocol.ERRORTAG, "Invalid parameters for UPDATE_USER");
                    }
                }
                break;
            case Protocol.DELETE_USER:
                if (connectionUser == null) {
                    response=sendCommand(Protocol.ERRORTAG, "Not logged in");
                } else {
                    if (connectionRole != UserRole.ADMIN){
                        response=sendCommand(Protocol.ERRORTAG, "Not admin rights");
                        break;
                    }
                    if (params.length == 1) {
                        String username = params[0];
                        if (!connectionUser.equals(username) && !server.getUserList().contains(username)) {
                            User user = serverUtil.userExists(username);
                            if(serverUtil.userExists(username)!= null){
                                System.out.println("Deleting user: " + username);
                                server.getDbLoader().deleteUser(user);
                                response=sendCommand(Protocol.DELETE_USER, "User " + username + "was deleted successfully");
                            }else {
                                response=sendCommand(Protocol.ERRORTAG, "User does not exist");
                            }
                        } else {
                            response=sendCommand(Protocol.ERRORTAG, "Cannot delete logged in user");
                        }
                    } else{
                        response=sendCommand(Protocol.ERRORTAG, "Invalid parameters for DELETE_USER");
                    }
                }
                break;
            case Protocol.KICK_USER:
                if (connectionUser == null) {
                    response=sendCommand(Protocol.ERRORTAG, "Not logged in");
                } else {
                    if (connectionRole != UserRole.ADMIN){
                        response=sendCommand(Protocol.ERRORTAG, "Not admin rights");
                        break;
                    }
                    if (params.length == 1) {
                        String username = params[0];
                        if (server.getUserList().contains(username)) {
                            serverUtil.kickUser(username);
                            response=sendCommand(Protocol.KICK_USER, "User kicked out ");
                        } else {
                            response=sendCommand(Protocol.ERRORTAG, "Cannot kick logged out user");
                        }
                    } else{
                        response=sendCommand(Protocol.ERRORTAG, "Invalid parameters for DELETE_USER");
                    }
                }
                break;
            case Protocol.LIST_USERS:
                String users = server.getUsers().stream()
                        .map(User::toString)
                        .collect(Collectors.joining(Protocol.SEPARATOR));
                response=sendCommand(Protocol.LIST_USERS, users);
                break;

            case Protocol.LIST_ONLINE_USERS:
                response=sendCommand(Protocol.LIST_ONLINE_USERS, server.getUserList().toString());
                break;
            default:
                response=sendCommand(Protocol.ERRORTAG, "Invalid command in UserManagementServiceHandler");
                break;
        }
    AppLogger.log(LogLevel.INFO, ServerUtil.SENT_COMMAND+response);
    }
}
