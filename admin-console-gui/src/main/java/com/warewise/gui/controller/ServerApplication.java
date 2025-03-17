package com.warewise.gui.controller;

import com.warewise.gui.networking.NetworkingClass;
import com.warewise.gui.util.DashboardHandler;
import com.warewise.gui.util.PropertiesReader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServerApplication extends javafx.application.Application {


    private static final int port    =  12345;

    private static DashboardHandler dashboardHandler ;
    private static NetworkingClass networkingObject ;
    private static FXMLLoader fxmlLoader;
    public static boolean[] settings ;

    @Override
    public void start(Stage stage) throws IOException {
        settings = PropertiesReader.loadSettings();
        dashboardHandler = new DashboardHandler();
        fxmlLoader = new FXMLLoader(ServerApplication.class.getResource("main-view.fxml"));
        stage.setTitle("WareWise App");
        stage.getIcons().add(new Image(System.getProperty("user.home") + "/WareWiseFiles/images/logo.png"));
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
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static DashboardHandler getDashboardHandler(){
        return dashboardHandler;
    }

    public static NetworkingClass getNetworkingObject() {
        return networkingObject;
    }

    public static void initNetworkingObject() {
        try {
            networkingObject = new NetworkingClass(
                    new Socket(InetAddress.getLocalHost(),port),dashboardHandler,fxmlLoader);
            networkingObject.listenToServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}