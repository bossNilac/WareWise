package com.warewise.gui.controller;

import com.warewise.gui.networking.NetworkingClass;
import com.warewise.gui.util.DashboardHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;

public class ServerApplication extends javafx.application.Application {

    private static final String host = "localhost";
    private static final int port    =  12345;

    private static DashboardHandler dashboardHandler ;
    private static NetworkingClass networkingObject ;

    @Override
    public void start(Stage stage) throws IOException {

        dashboardHandler = new DashboardHandler();

        FXMLLoader fxmlLoader = new FXMLLoader(ServerApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Hello!");
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
            networkingObject = new NetworkingClass(new Socket(host,port),dashboardHandler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}