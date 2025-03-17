package com.warewise.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(App.resourceDir+"/fxml/Main.fxml"));
        stage.setTitle("WareWise App");
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(getClass().getResource(App.resourceDir+"/stylesheets/stylesheet-light.css").toExternalForm());
        stage.getIcons().add(new Image(System.getProperty("user.home") + "/WareWiseFiles/images/logo.png"));
        // Prevent fullscreen and always on top behavior
        stage.setScene(scene);
        stage.show();


    }

    public static void main(String[] args) {
        launch(args);
    }
}
