package com.warewise.server.service;

import com.warewise.common.model.User;
import com.warewise.common.util.protocol.Protocol;
import com.warewise.server.logs.AppLogger;
import com.warewise.server.server.ServerConnection;
import com.warewise.server.server.ThreadPoolSocketServer;

import java.util.Objects;

public class AuthHandler extends ServiceHandler{

    private boolean isLogged = false;

    public AuthHandler(ServerConnection connection) {
        super(connection);
    }

    @Override
    public void handleMessage(String message) {
        String[] receivedMsg = message.split(Protocol.SEPARATOR);
        if(message.startsWith(Protocol.LOGIN)){
            loginCheck(receivedMsg[1],receivedMsg[2] );
        } else if (message.startsWith(Protocol.LOGOUT)) {
            if (isLogged){
                connection.sendMessage(Protocol.LOGOUT);
            }else {
                connection.sendMessage(Protocol.ERRORTAG+" not logged in");
                connection.close();
            }
        }
    }

    private void loginCheck(String username,String passwd) {
        for (User user : connection.getServer().getUserList()){
            if (Objects.equals(user.getPasswordHash(), passwd)
                    && Objects.equals(user.getUsername(), username)){
                connection.sendMessage(Protocol.LOGIN_SUCCESS);
                isLogged = true;
                break;
            }
        }
        connection.sendMessage(Protocol.LOGIN_FAILURE);
    }

    @Override
    public void handleDisconnect() {
        AppLogger.getLogger().warn("User Logged off");
    }
}
