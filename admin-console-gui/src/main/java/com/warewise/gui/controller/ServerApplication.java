package com.warewise.gui.controller;

import com.warewise.gui.util.DashboardHandler;
import com.warewise.gui.util.PropertiesReader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

public class ServerApplication extends javafx.application.Application {



    private static DashboardHandler dashboardHandler ;
    private static FXMLLoader fxmlLoader;
    public static boolean[] settings ;

    @Override
    public void start(Stage stage) throws IOException {
        JMetro jMetro = new JMetro(Style.LIGHT);
        settings = PropertiesReader.loadSettings();
        dashboardHandler = new DashboardHandler();
        fxmlLoader = new FXMLLoader(ServerApplication.class.getResource("main-view.fxml"));
        stage.setTitle("WareWise App");
        URL bundledIcon = ServerApplication.class.getResource("/images/logo.png");
        if (bundledIcon != null) {
            stage.getIcons().add(new Image(bundledIcon.toExternalForm()));
        } else {
            Path iconPath = Path.of(System.getProperty("user.home"), "WareWise", "images", "logo.png");
            if (Files.exists(iconPath)) {
                stage.getIcons().add(new Image(iconPath.toUri().toString()));
            }
        }
        // Prevent fullscreen and always on top behavior
        stage.setFullScreen(false);
        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(null);
        stage.setAlwaysOnTop(false);
        stage.setResizable(false); // Prevent resizing
        stage.fullScreenProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) stage.setFullScreen(false); // Prevent entering fullscreen
        });
        Scene scene = new Scene(fxmlLoader.load());
        jMetro.setScene(scene);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static DashboardHandler getDashboardHandler(){
        return dashboardHandler;
    }

}
