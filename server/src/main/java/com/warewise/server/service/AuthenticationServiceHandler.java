package com.warewise.server.service;

import com.warewise.common.model.User;
import com.warewise.common.util.enums.UserRole;
import com.warewise.common.util.protocol.Protocol;
import com.warewise.common.encryption.Encrypt;
import com.warewise.server.server.*;
import com.warewise.common.logs.*;
import com.warewise.server.server.util.ServerUtil;

/**
 * A concrete ServiceHandler for authentication-related commands.
 * It implements HELLO and LOGIN using the protocol.
 */
public class AuthenticationServiceHandler extends ServiceHandler {

    private boolean receivedHello = false;
    private String registeredUser = null;
    private UserRole role;

    public AuthenticationServiceHandler(Server server, ServerConnection connection) {
        super(server, connection);
    }

    @Override
    public void handleDisconnect() {
        // Cleanup logic upon disconnection.
        System.out.println("Handling disconnect in AuthenticationServiceHandler.");
        if (registeredUser != null) {
            server.removeFromList(registeredUser);
        }
        server.removeClient(this);
    }

    @Override
    public void handleCommand(String command, String[] params) {
        String response = null;
        AppLogger.log(LogLevel.INFO, ServerUtil.RECEIVED_COMMAND+command);
        switch (command) {
            case Protocol.HELLO:
                if (!receivedHello) {
                     response=sendCommand(Protocol.HELLO, server.getHello());
                     receivedHello = true;
                } else {
                    response=sendCommand(Protocol.ERRORTAG, "HELLO command already received");
                }
                break;

            case Protocol.LOGIN:
                if (!receivedHello) {
                    response=sendCommand(Protocol.ERRORTAG, "HELLO must be sent before LOGIN");
                    break;
                }
                if (params.length == 2) {
                    String username = params[0];
                    String password_hash = params[1];
                    if (server.isLoggedIn(username)) {
                        response=sendCommand(Protocol.LOGIN_FAILURE, "User already logged in on another device");
                    } else {
                        User user = serverUtil.userExists(username);
                        if (user != null) {
                            String storedHash = user.getPasswordHash() ;
                            if (Encrypt.verifyPassword(storedHash, password_hash)) {
                                registeredUser = username;
                                role = user.getRole();
                                System.out.println(role);
                                response=sendCommand(Protocol.LOGIN_SUCCESS);
                                server.addToList(username);
                            } else {
                                response=sendCommand(Protocol.LOGIN_FAILURE, "Wrong password");
                            }
                        }else {
                            response=sendCommand(Protocol.LOGIN_FAILURE, "Wrong username");
                        }

                    }
                } else {
                    response=sendCommand(Protocol.ERRORTAG, "Missing username for LOGIN or password");
                }
                break;

            case Protocol.LOGOUT:
                if (!receivedHello) {
                    response=sendCommand(Protocol.ERRORTAG, "HELLO must be sent before LOGIN");
                    break;
                }
                if (params.length == 1) {
                    String username = params[0];
                    if (!server.isLoggedIn(username)) {
                        response=sendCommand(Protocol.LOGIN_FAILURE, "User already logged out.");
                    } else {
                        System.out.println(server.getUserList());
                    }
                } else {
                    response=sendCommand(Protocol.ERRORTAG, "Missing username for LOGIN or password");
                }
                break;

            // Add additional cases here for other commands as needed.
            default:
                response=sendCommand(Protocol.ERRORTAG, "Invalid command");
                break;
        }
        AppLogger.log(LogLevel.INFO, ServerUtil.SENT_COMMAND+response);

    }


    public String getRegisteredUser() {
        return registeredUser;
    }

    public UserRole getRole() {
        return role;
    }
}
