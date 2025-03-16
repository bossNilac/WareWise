package com.warewise.gui.util;

import com.warewise.gui.controller.ServerApplication;
import com.warewise.gui.networking.Protocol;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.controlsfx.control.Notifications;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;


public class UtilityCommands {
    public static boolean isFileEmpty(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return reader.readLine() == null;  // Returns true if the file is empty
        } catch (IOException e) {
            return true; // Treat it as empty if there's an error reading
        }

    }

    public static void displayNotificationPanel(int i, String text) {
            Platform.runLater(() -> {
                switch (i) {
                    case 1:
                        Notifications.create().title("Info").text(text).showInformation();
                        break;
                    case 2:
                        Notifications.create().title("Warning").text(text).showWarning();
                        break;
                    case 3:
                        Notifications.create().title("Error").text(text).showError();
                        break;
                    default:
                        Notifications.create().title("Notification").text(text).show();
                }
            });
        }
    public static boolean displayWarning(String text,boolean output) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(text);
        if(output) {
            // Add Yes and No buttons
            alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

            // Show dialog and wait for response
            Optional<ButtonType> result = alert.showAndWait();
            return result.isPresent() && result.get() == ButtonType.YES;
        }else{
            alert.getButtonTypes().setAll(ButtonType.OK);
            alert.showAndWait();
            return false;
        }
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
            ServerApplication.initNetworkingObject();
            ServerApplication.getNetworkingObject().sendMessage(Protocol.HELLO);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}


