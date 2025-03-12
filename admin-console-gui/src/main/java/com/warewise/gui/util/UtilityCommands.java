package com.warewise.gui.util;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.controlsfx.control.Notifications;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
    public static boolean displayWarning(String text) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(text);

        // Add Yes and No buttons
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        // Show dialog and wait for response
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.YES;
    }

}
