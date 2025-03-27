package com.warewise.client.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class AlertUtil {

    /**
     * Displays a Yes/No confirmation alert with customized text.
     *
     * @param type    The type of confirmation (CREATE, MODIFY, DELETE).
     * @param param   The entity or item name to include in the message.
     * @return True if the user selects "Yes", false otherwise.
     */
    public static boolean showYesNoAlert(AlertType type, String param) {
        String message = switch (type) {
            case CREATE -> "Are you sure you want to create this " + param + "?";
            case MODIFY -> "Are you sure you want to modify this " + param + "?";
            case DELETE -> "Are you sure you want to delete this " + param + "?";
        };

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.YES;
    }

    /**
     * Displays an OK alert with a custom message.
     *
     * @param message The message to display.
     */
    public static void showOkAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void serverError(){
        AlertUtil.showOkAlert("The served timeout out");
    }

    /**
     * Enum for Yes/No Alert Types.
     */
    public enum AlertType {
        CREATE, MODIFY, DELETE
    }
}
