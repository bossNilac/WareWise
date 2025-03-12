package com.warewise.gui.controllers;

import com.warewise.gui.util.UtilityCommands;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
        UtilityCommands.displayNotificationPanel(1,"it works");
        UtilityCommands.displayWarning("it works");
    }
}