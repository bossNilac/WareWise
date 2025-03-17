package com.warewise.client.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class MainController {

    @FXML
    private VBox sideMenu;
    @FXML
    private StackPane contentPane;
    @FXML
    private Button btnDashboard;
    @FXML
    private Button btnSettings;
    @FXML
    private Button btnOrders;
    @FXML
    private Button btnCategories;
    @FXML
    private Button btnUpdateInventory;
    @FXML
    private Button btnItems;
    @FXML
    private Button btnAlerts;
    @FXML
    private Button btnSuppliers;
    @FXML
    private ImageView logoImage;


    List<Button> menuButtons;

    @FXML
    private void initialize() {
        Image bannerImage = new Image(System.getProperty("user.home") + "/WareWiseFiles/images/logo.png");
        logoImage.setImage(bannerImage);
        slideInSidebar(sideMenu);

        menuButtons = Arrays.asList(btnDashboard, btnSettings, btnOrders, btnCategories,
                btnUpdateInventory, btnItems, btnAlerts, btnSuppliers);
        // Load default view (Dashboard)
        loadView(btnDashboard,"DashboardView.fxml");

        btnDashboard.setOnAction(e -> loadView(btnDashboard,"DashboardView.fxml"));
        btnOrders.setOnAction(e -> loadView(btnOrders,"OrdersView.fxml"));
        btnCategories.setOnAction(e -> loadView(btnCategories,"CategoriesView.fxml"));
        btnUpdateInventory.setOnAction(e -> loadView(btnUpdateInventory,"UpdateInventoryView.fxml"));
        btnItems.setOnAction(e -> loadView(btnItems,"ItemsView.fxml"));
        btnAlerts.setOnAction(e -> loadView(btnAlerts,"AlertsView.fxml"));
        btnSuppliers.setOnAction(e -> loadView(btnSuppliers,"SuppliersView.fxml"));
        btnSettings.setOnAction(e -> loadView(btnSettings,"SettingsView.fxml"));

    }

    private void slideInSidebar(Node sidebar) {
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), sidebar);
        slideIn.setFromX(-250);  // Start from off-screen
        slideIn.setToX(0);       // Move to original position
        slideIn.setInterpolator(Interpolator.EASE_IN);
        slideIn.play();
    }


    private void loadView(Button activeButton,String fxmlFile) {
        try {
            Node view = FXMLLoader.load(getClass().getResource("/com.warewise.client/fxml/"+fxmlFile));
            contentPane.getChildren().setAll(view);

            for (Button btn : menuButtons) {
                btn.getStyleClass().remove("selected");
            }

            // Add "selected" class to the active button
            activeButton.getStyleClass().add("selected");
        } catch (IOException e) {
            System.out.println(fxmlFile);
            e.printStackTrace();
        }
    }
}
