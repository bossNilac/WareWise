package com.warewise.server.server.util;

import com.warewise.common.model.User;
import com.warewise.server.server.Server;

public class ServerUtil {

    private Server server;

    public ServerUtil(Server server){
        this.server = server;
    }

    public User userExists(String username) {
        for (User user : server.getUsers()){
            if (user.getUsername().equals(username)){
                return user;
            }
        }
        return null;
    }



}
