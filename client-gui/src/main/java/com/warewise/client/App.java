package com.warewise.client;

import com.warewise.client.apps.LoginApp;
import com.warewise.client.apps.Main;
import com.warewise.client.network.DataHandler;
import com.warewise.client.network.ServerConnection;
import com.warewise.client.util.ConfigManager;

public class App {

    public static final String resourceDir="/com.warewise.client";
    public static String username;
    public static String password;
    public static boolean darkMode;

    public static void main(String[] args) {
        ConfigManager.loadProperties();
        username = ConfigManager.getProperty("username","null");
        password = ConfigManager.getProperty("password","null");
        boolean brokenLogin = username.equals("null") && password.equals("null");
        System.out.println(brokenLogin);
        darkMode = Boolean.parseBoolean(ConfigManager.
                getProperty("darkMode","true"));
        System.out.println(Boolean.parseBoolean(ConfigManager.
                getProperty("rememberMe","false")));
        if (brokenLogin || !Boolean.parseBoolean(ConfigManager.
                getProperty("rememberMe","false"))){
        LoginApp.main(args);
        }else {
            ServerConnection.getConnection();
            ServerConnection.login(username,password);
            Main.main(args);
        }
    }
}