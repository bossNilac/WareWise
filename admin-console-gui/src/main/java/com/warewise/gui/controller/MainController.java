package com.warewise.gui.controller;

import com.warewise.gui.util.AdminUtil;
import com.warewise.gui.util.UtilityCommands;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.File;

public class MainController {
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
    private Label usernameLabel;
    @FXML
    private Label connNumberLabel;
    @FXML
    private Label cpuNumberLabel;
    @FXML
    private Label memNumberLabel;
    @FXML
    private Button startServerButton;
    @FXML
    private Button closeServerButton;
    @FXML
    private AnchorPane dashboardPane;


    public void stopServerAction(ActionEvent actionEvent) {
        AdminUtil.closeServer(ServerApplication.getNetworkingObject());
    }

    public void startServerAction(ActionEvent actionEvent) {
        File file = new File(AdminUtil.CREDENTIALS_FILE);
        AdminUtil.startServer();
        ServerApplication.initNetworkingObject();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if(UtilityCommands.isFileEmpty(file) || !file.exists()){
            String[] loginPrompt = LoginPrompt.promptLogin();
            AdminUtil.saveLoginCred(loginPrompt[0],loginPrompt[1]);
            AdminUtil.logIn(ServerApplication.getNetworkingObject(),loginPrompt[0],loginPrompt[1]);
        }else {
            String[] params = AdminUtil.getLoginCred();
            AdminUtil.logIn(ServerApplication.getNetworkingObject(),params[0],params[1]);
        }
    }
}