package com.warewise.gui.controller;

import com.warewise.gui.networking.Protocol;
import com.warewise.gui.util.AdminUtil;
import com.warewise.gui.util.UtilityCommands;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainController {

    @FXML
    private Label serverActionsLabel;
    @FXML
    private Label memoryUsageLabel;
    @FXML
    private Button dashBoardTitleLabel;
    @FXML
    private Label connectedAsLabel;
    @FXML
    private Button menuSettingsButton;
    @FXML
    private Button menuDBButton;
    @FXML
    private Button menuConnBoardButton;
    @FXML
    private Button menuDashBoardButton;
    @FXML
    private Button menuButton;
    @FXML
    private Button recoverMenuButton;
    @FXML
    private VBox dashboard;
    @FXML
    private BorderPane root;
    @FXML
    private  Label usernameLabel;
    @FXML
    private  Label connNumberLabel;
    @FXML
    private  Label cpuNumberLabel;
    @FXML
    private  Label memNumberLabel;
    @FXML
    private Button startServerButton;
    @FXML
    private Button closeServerButton;
    @FXML
    private  AnchorPane dashboardPane;
    @FXML
    private TableView<Pair<String,String>> connectionTableView;
    TableColumn<Pair<String, String>, String> nameColumn = new TableColumn<>("Username");
    TableColumn<Pair<String, String>, String> ageColumn = new TableColumn<>("IP");
    private List<Pair<String, String>> tableData ;

    private List<Node> dashboardUiElements = new ArrayList<>();

    private boolean isDashboardVisible = true; // Track dashboard state


    @FXML
    public void initialize() {
        dashboardUiElements.addAll(List.of(
                serverActionsLabel, memoryUsageLabel, connectedAsLabel, usernameLabel,
                connNumberLabel, cpuNumberLabel, memNumberLabel,

                menuSettingsButton, menuDBButton, menuConnBoardButton, menuDashBoardButton,
                menuButton, recoverMenuButton, startServerButton, closeServerButton , dashBoardTitleLabel
        ));
        startServerAction(null);
    }


    public void stopServerAction(ActionEvent actionEvent) {
        AdminUtil.closeServer(ServerApplication.getNetworkingObject());
        try {
            ServerApplication.getNetworkingObject().close();
            usernameLabel.setText("N/A");
            cpuNumberLabel.setText("N/A");
            memNumberLabel.setText("N/A");
            connNumberLabel.setText("N/A");
            UtilityCommands.displayNotificationPanel(1,"Closed server!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void startServerAction(ActionEvent actionEvent) {
        File file = new File(AdminUtil.CREDENTIALS_FILE);
        AdminUtil.startServer();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        ServerApplication.initNetworkingObject();
        if(UtilityCommands.isFileEmpty(file) || !file.exists()){
            String[] loginPrompt = LoginPrompt.promptLogin();
            AdminUtil.saveLoginCred(loginPrompt[0],loginPrompt[1]);
            AdminUtil.logIn(ServerApplication.getNetworkingObject(),loginPrompt[0],loginPrompt[1]);
        }else {
            String[] params = AdminUtil.getLoginCred();
            AdminUtil.logIn(ServerApplication.getNetworkingObject(),params[0],params[1]);
            setUsernameLabel(params[0]);
        }
        refreshTableAction(null);
    }

    public void menuButtonAction(ActionEvent actionEvent){
        toggleDashboard();
    }

    private void toggleDashboard() {
        TranslateTransition slide = new TranslateTransition(Duration.millis(350), dashboard);
        FadeTransition fade = new FadeTransition(Duration.millis(250), dashboard);
        ScaleTransition scale = new ScaleTransition(Duration.millis(300), dashboard);

        if (isDashboardVisible) {
            fade.setFromValue(1.0);
            fade.setToValue(0.0); // Smooth fading out

            slide.setToX(250); // Move to the right
            slide.setInterpolator(Interpolator.EASE_IN); // Fast at start, slow at end

            scale.setToX(0.9);
            scale.setToY(0.9); // Slight shrink effect for pop-out

            slide.setOnFinished(event -> root.setLeft(null));

            // Play animations together
            new ParallelTransition(slide, fade, scale).play();


        } else {
            root.setLeft(dashboard); // Add before animation starts

            fade.setFromValue(0.0);
            fade.setToValue(1.0); // Smooth fading in

            slide.setFromX(250);
            slide.setToX(0); // Slide back in
            slide.setInterpolator(Interpolator.EASE_OUT); // Slow at start, fast at end

            scale.setFromX(0.9);
            scale.setFromY(0.9);
            scale.setToX(1);
            scale.setToY(1); // Pop-in effect

            new ParallelTransition(slide, fade, scale).play();
        }
        recoverMenuButton.setVisible(isDashboardVisible);
        isDashboardVisible = !isDashboardVisible;
    }

    public  void setCpuNumberLabel(String text) {
         cpuNumberLabel.setText(text);
    }

    public  void setConnNumberLabel(String text) {
         connNumberLabel.setText(text);
    }

    public  void setUsernameLabel(String text) {
         usernameLabel.setText(text);
    }

    public  void setMemNumberLabel(String text) {
        memNumberLabel.setText(text);
    }

    public  boolean isDashBoardVisible(){
        return dashboardPane.isVisible();
    }

    public void exitAction(){
        System.exit(0);
    }

    public void logOutAction(){
        AdminUtil.logOut(ServerApplication.getNetworkingObject());
        usernameLabel.setText("N/A");
        cpuNumberLabel.setText("N/A");
        memNumberLabel.setText("N/A");
        connNumberLabel.setText("N/A");
        try {
            ServerApplication.getNetworkingObject().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        UtilityCommands.displayNotificationPanel(1,"Logged out!");
    }

    public void refreshTableAction(ActionEvent actionEvent){
        ServerApplication.getNetworkingObject().sendMessage(Protocol.HEARTBEAT);
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(tableData);

        connectionTableView.getColumns().remove(nameColumn);
        connectionTableView.getColumns().remove(ageColumn);
        connectionTableView.getColumns().addAll(nameColumn, ageColumn);

        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        ageColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue()));

        ObservableList<Pair<String, String>> data = FXCollections.observableArrayList(tableData);
        connectionTableView.getItems().setAll(data);
        connectionTableView.refresh();
    }

    public void endConnectionAction(ActionEvent actionEvent){
        Pair<String, String> selectedRow = connectionTableView.getSelectionModel().getSelectedItem();
        if (selectedRow != null) {
            AdminUtil.kickUser(ServerApplication.getNetworkingObject(),selectedRow.getKey());
            refreshTableAction(null);
        }
    }

    private void toggleDashboardUI(){
        dashboardUiElements.forEach(node -> node.setVisible(isDashboardVisible));
        isDashboardVisible = !isDashboardVisible;
    }

    public void loadTableData(String output){
        List<Pair<String, String>> parsedConnections = new ArrayList<>();
        String[] parts = output.replaceAll("/","").split(Protocol.SEPARATOR);
        for (int i = 1; i < parts.length; i += 2) {
            if (i + 1 < parts.length) {
                parsedConnections.add(new Pair<>(parts[i+1], parts[i])); // IP -> Username
            }
        }
        setTableData(parsedConnections);
    }

    public void setTableData(List<Pair<String, String>> tableData) {
        this.tableData = tableData;
    }
}