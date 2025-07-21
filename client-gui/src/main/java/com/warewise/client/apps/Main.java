package com.warewise.client.apps;

import com.warewise.client.App;
import com.warewise.client.controller.MainController;
import com.warewise.client.networking.DataHandler;
import com.warewise.client.util.enums.UserRole;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    public static Scene scene;

    @Override
    public void start(Stage stage) throws Exception {
        // Initialize DB and session
        DataHandler.initTables("Users");

        // Load main layout
        FXMLLoader loader = new FXMLLoader(
                Main.class.getResource(App.resourceDir + "/fxml/Main.fxml")
        );
        Parent root = loader.load();

        // Determine role and inform controller
        UserRole role = DataHandler.getCurrentUser().getRole();  // e.g. "WORKER" or "MANAGER"
        if (role.equals(UserRole.ADMIN)) {
            Alert alert = new Alert(Alert.AlertType.WARNING,"Administrator role has its specific app do not use this one");
            alert.show();
        } else {
            boolean isManager = role == UserRole.MANAGER;
            MainController controller = loader.getController();
            controller.setManagerFlag(isManager);

            // Build scene
            scene = new Scene(root);
            String themeFile = App.darkMode
                    ? App.resourceDir + "/stylesheets/stylesheet-dark.css"
                    : App.resourceDir + "/stylesheets/stylesheet-light.css";
            scene.getStylesheets().add(
                    getClass().getResource(themeFile).toExternalForm()
            );

            // Stage setup
            stage.setTitle("WareWise App");
            stage.getIcons().add(
                    new Image(System.getProperty("user.home") + "/WareWise/images/logo.png")
            );
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
