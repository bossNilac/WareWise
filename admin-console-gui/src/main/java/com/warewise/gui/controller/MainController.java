package com.warewise.gui.controller;

import com.warewise.gui.util.AdminUtil;
import com.warewise.gui.util.UtilityCommands;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;

public class MainController {

    @FXML
    private Button recoverMenuButton;
    @FXML
    private VBox dashboard;
    @FXML
    private BorderPane root;
    @FXML
    private Button menuButton;
    @FXML
    private Button menuDashBoardButton;
    @FXML
    private Button menuDBButton;
    @FXML
    private Button menuConnBoardButton;
    @FXML
    private Button menuSettingsButton;
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

    private boolean isDashboardVisible = true; // Track dashboard state

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

}