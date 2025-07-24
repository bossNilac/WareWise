package com.warewise.client.controller;

import com.warewise.client.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;

public class MainController {
    @FXML private VBox sideMenu;
    @FXML private StackPane contentPane;

    private boolean isManager;

    /**
     * Call once _after_ loading Main.fxml (in your Main.java) and before showing the stage.
     */
    public void setManagerFlag(boolean isManager) {
        this.isManager = isManager;
        loadSideMenu();
        DashboardController dash = loadView("DashboardView.fxml");
        if (dash != null) dash.setMainController(this);
    }


    /** Loads the correct sidebar menu using App.resourceDir as the classpath root. */
    private void loadSideMenu() {
        String menuFile = isManager ? "ManagerMenu.fxml" : "WorkersMenu.fxml";
        String menuPath = App.resourceDir + "/fxml/" + menuFile;
        URL menuUrl = getClass().getResource(menuPath);
        if (menuUrl == null) {
            System.err.println("Cannot find menu FXML at: " + menuPath);
            sideMenu.getChildren().clear();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(menuUrl);
            loader.setController(this);               // reuse this MainController
            VBox menuRoot = loader.load();            // load the <VBox> from the menu FXML
            sideMenu.getChildren().setAll(            // copy its children into your injected sideMenu
                    menuRoot.getChildren()
            );
        } catch (IOException ex) {
            ex.printStackTrace();
            sideMenu.getChildren().clear();
        }
    }

    /** Loads the given FXML into the contentPane and returns its controller. */
    private <T> T loadView(String fxmlName) {
        String path = App.resourceDir + "/fxml/" + fxmlName;
        URL url = getClass().getResource(path);
        if (url == null) {
            System.err.println("Cannot find FXML: " + path);
            return null;
        }
        try {
            FXMLLoader loader = new FXMLLoader(url);
            Parent view = loader.load();
            contentPane.getChildren().setAll(view);
            return loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Allow child controllers to load new content into the main pane */
    public void loadContentPane(String fxmlName) {
        loadView(fxmlName);
    }


    /**
     * Bound to every sidebar Button via onAction="#handleMenuAction",
     * with userData set to the view FXML name (e.g. "OrdersView.fxml").
     */
    @FXML
    private void handleMenuAction(ActionEvent event) {
        String view = (String)((Node)event.getSource()).getUserData();

        if ("DashboardView.fxml".equals(view)) {
            // whenever you click the Dashboard button, re-load it and re-inject
            DashboardController dash = loadView(view);
            if (dash != null) {
                dash.setMainController(this);
            }
        } else {
            // for any other view (OrdersView.fxml, etc.) just load it
            loadView(view);
        }
    }

    /** Loads the given view (just the filename, e.g. "ItemsView.fxml") into the center pane. */
    private void loadContent(String viewF) {
        String viewPath = App.resourceDir + "/fxml/" + viewF;
        URL viewUrl = getClass().getResource(viewPath);
        if (viewUrl == null) {
            System.err.println("Cannot find view FXML at: " + viewPath);
            return;
        }
        try {
            Node view = FXMLLoader.load(viewUrl);
            contentPane.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
