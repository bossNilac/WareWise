package com.warewise.client;

import com.warewise.client.apps.LoginApp;
import com.warewise.client.apps.Main;
import javafx.embed.swing.JFXPanel;
import com.warewise.client.util.AdminUtil;
import com.warewise.client.util.ConfigManager;
import com.warewise.client.util.UtilityCommands;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.warewise.client.util.AlertUtil.serverError;
import static com.warewise.client.util.UtilityCommands.pingServer;

public class App {

    public static final String resourceDir="/com.warewise.client";
    public static String username;
    public static String password;
    public static boolean darkMode;

    public static Image loadAppIcon() {
        URL bundledIcon = App.class.getResource("/images/logo.png");
        if (bundledIcon != null) {
            return new Image(bundledIcon.toExternalForm());
        }

        Path iconPath = Path.of(System.getProperty("user.home"), "WareWise", "images", "logo.png");
        if (Files.exists(iconPath)) {
            return new Image(iconPath.toUri().toString());
        }
        return null;
    }

    public static void main(String[] args) {
        new JFXPanel();
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
            if(pingServer()) {
                if(AdminUtil.doLogin()) {
                    Main.main(args);
                }else  {
                    LoginApp.main(args);
                }
            }else {
                serverError();
            }
        }
    }
}
