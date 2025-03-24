package com.warewise.client;

import com.warewise.client.util.ConfigManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    public static Scene scene;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(App.resourceDir+"/fxml/Main.fxml"));
        stage.setTitle("WareWise App");
        scene = new Scene(fxmlLoader.load());
        boolean isDarkMode = Boolean.parseBoolean(ConfigManager.getProperty("darkMode", "true"));
        String themeFile = isDarkMode ? App.resourceDir+"/stylesheets/stylesheet-dark.css" : App.resourceDir+"/stylesheets/stylesheet-light.css";
        scene.getStylesheets().add(getClass().getResource(themeFile).toExternalForm());
        stage.getIcons().add(new Image(System.getProperty("user.home") + "/WareWiseFiles/images/logo.png"));
        // Prevent fullscreen and always on top behavior
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
