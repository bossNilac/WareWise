package com.warewise.client.util;

import com.warewise.client.networking.ApiHandler;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import org.controlsfx.control.Notifications;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;


public class UtilityCommands {

    public static void displayNotificationPanel(int i, String text) {
            Platform.runLater(() -> {
                switch (i) {
                    case 2:
                        AlertUtil.showWarningAlert(text);
                        break;
                    case 3:
                        AlertUtil.showErrorAlert(text);
                        break;
                    default:
                        AlertUtil.showOkAlert(text);
                }
            });
        }

    public static void openLink(String url) {
        try {
            // Check if Desktop is supported
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(new URI(url));
                    return;
                }
            }
            // Fallback for Linux environments
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("nix") || os.contains("nux")) {
                Runtime.getRuntime().exec("xdg-open " + url);
            } else if (os.contains("win")) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            } else if (os.contains("mac")) {
                Runtime.getRuntime().exec("open " + url);
            } else {
                throw new UnsupportedOperationException("Cannot open URL on this OS");
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static boolean pingServer() {
        try {
            String loginResponse = ApiHandler.sendApiCall("GET", "status", "", null);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}


