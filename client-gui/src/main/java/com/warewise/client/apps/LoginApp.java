package com.warewise.client.apps;

import com.warewise.client.App;
import com.warewise.client.util.ConfigManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class LoginApp extends Application{

    public static Scene scene;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginApp.class.getResource(App.resourceDir+"/fxml/Login.fxml"));
        stage.setTitle("WareWise App");
        scene = new Scene(fxmlLoader.load());

        String themeFile = App.darkMode ? App.resourceDir+"/stylesheets/stylesheet-dark.css" : App.resourceDir+"/stylesheets/stylesheet-light.css";
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

