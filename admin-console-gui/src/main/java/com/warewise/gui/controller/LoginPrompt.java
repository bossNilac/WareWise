package com.warewise.gui.controller;

import com.warewise.gui.util.AdminUtil;
import com.warewise.gui.util.UtilityCommands;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Scanner;

public class LoginPrompt extends Application {
    private static String username;
    private static String password;
    private static boolean submitted = false;

    /**
     * JavaFX Login Prompt
     */
    @Override
    public void start(Stage primaryStage) {
        Stage loginStage = new Stage();
        loginStage.initModality(Modality.APPLICATION_MODAL);
        loginStage.setTitle("Login");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        Label userLabel = new Label("Username:");
        TextField userInput = new TextField();

        Label passLabel = new Label("Password:");
        PasswordField passInput = new PasswordField();

        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> {
            username = userInput.getText();
            password = passInput.getText();
            submitted = true;
            loginStage.close();
        });

        grid.add(userLabel, 0, 0);
        grid.add(userInput, 1, 0);
        grid.add(passLabel, 0, 1);
        grid.add(passInput, 1, 1);
        grid.add(loginButton, 1, 2);

        Scene scene = new Scene(grid, 300, 150);
        loginStage.setScene(scene);
        loginStage.showAndWait();
    }

    /**
     * Utility method to retrieve login credentials
     */
    public static String[] promptLogin() {
        try {
            // Check if JavaFX is available
            new Thread(() -> Application.launch(LoginPrompt.class)).start();
            while (!submitted) {
                Thread.sleep(100);
            }
        } catch (Exception e) {
            // If JavaFX fails, use CLI login
            UtilityCommands.displayNotificationPanel(3,"Login failed , closing server + \n Restart app!");
            AdminUtil.closeServer(ServerApplication.getNetworkingObject());
        }
        return new String[]{username, password};
    }

}
