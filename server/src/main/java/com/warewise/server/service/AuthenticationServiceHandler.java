package com.warewise.server.service;

import com.warewise.common.model.User;
import com.warewise.common.util.protocol.Protocol;
import com.warewise.common.encryption.Encrypt;
import com.warewise.server.server.*;

/**
 * A concrete ServiceHandler for authentication-related commands.
 * It implements HELLO and LOGIN using the protocol.
 */
public class AuthenticationServiceHandler extends ServiceHandler {

    private boolean receivedHello = false;
    private String registeredUser = null;

    public AuthenticationServiceHandler(Server server, ServerConnection connection) {
        super(server, connection);
    }

    @Override
    public void sendCommand(String command, String... params) {
        // Build the protocol message (command + separator + each parameter)
        StringBuilder sb = new StringBuilder(command);
        for (String param : params) {
            sb.append(Protocol.SEPARATOR).append(param);
        }
        connection.sendMessage(sb.toString());
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
        switch (command) {
            case Protocol.HELLO:
                if (!receivedHello) {
                    sendCommand(Protocol.HELLO, server.getHello());
                    receivedHello = true;
                } else {
                    sendCommand(Protocol.ERRORTAG, "HELLO command already received");
                }
                break;

            case Protocol.LOGIN:
                if (!receivedHello) {
                    sendCommand(Protocol.ERRORTAG, "HELLO must be sent before LOGIN");
                    break;
                }
                if (params.length == 2) {
                    String username = params[0];
                    String password_hash = params[1];
                    if (server.isLoggedIn(username)) {
                        sendCommand(Protocol.LOGIN_FAILURE, "User already logged in on another device");
                    } else {
                        User user = serverUtil.userExists(username);
                        if (user != null) {
                            String storedHash = user.getPasswordHash() ;
                            if (Encrypt.verifyPassword(storedHash, password_hash)) {
                                registeredUser = username;
                                sendCommand(Protocol.LOGIN_SUCCESS);
                                server.addToList(username);
                                System.out.println("User connected: " + username);
                            } else {
                                sendCommand(Protocol.LOGIN_FAILURE, "Wrong password");
                            }
                        }else {
                            sendCommand(Protocol.LOGIN_FAILURE, "Wrong username");
                        }

                    }
                } else {
                    sendCommand(Protocol.ERRORTAG, "Missing username for LOGIN or password");
                }
                break;

            case Protocol.LOGOUT:
                if (!receivedHello) {
                    sendCommand(Protocol.ERRORTAG, "HELLO must be sent before LOGIN");
                    break;
                }
                if (params.length == 1) {
                    String username = params[0];
                    if (!server.isLoggedIn(username)) {
                        sendCommand(Protocol.LOGIN_FAILURE, "User already logged out.");
                    } else {
                        System.out.println("Logged out user : " + username);
                        server.removeFromList(username);
                        System.out.println(server.getUserList());
                    }
                } else {
                    sendCommand(Protocol.ERRORTAG, "Missing username for LOGIN or password");
                }
                break;

            // Add additional cases here for other commands as needed.
            default:
                sendCommand(Protocol.ERRORTAG, "Invalid command");
                break;
        }
    }
}
