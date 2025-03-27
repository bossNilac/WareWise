package com.warewise.client.controller;

import com.warewise.client.apps.Main;
import com.warewise.client.network.ServerConnection;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    @FXML
    PasswordField passwordField;
    @FXML
    TextField usernameField;
    @FXML
    Label errorLabel;

    @FXML
    private void initialize() {

    }

    private void displayError(String param){
        errorLabel.setText(param);
        errorLabel.getStyleClass().add("notification");
    }

    public void doLoginAction(ActionEvent actionEvent) {
        String username=usernameField.getText();
        String password=passwordField.getText();

        boolean brokenLogin = username == null && password == null;

        if (ServerConnection.getConnection() == null){
            displayError("Server offline!");
        }else if (!brokenLogin && ServerConnection.login(username, password)) {
            Platform.runLater(() -> {
                try {
                    Stage currentStage = (Stage) usernameField.getScene().getWindow();
                    currentStage.close();
                    new Main().start(new Stage());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }else {
            displayError("Wrong username or password");
        }
    }
}
