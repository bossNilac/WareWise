package com.warewise.client.controller;

import com.warewise.client.util.ConfigManager;
import com.warewise.client.apps.LoginApp;
import com.warewise.client.networking.ApiHandler;
import com.warewise.client.util.AdminUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

import static com.warewise.client.apps.Main.scene;

public class SettingsController implements Initializable {

    @FXML private ToggleButton themeToggle;
    @FXML private ComboBox<String> refreshRateDropdown;
    @FXML private ComboBox<String> logoutTimerDropdown;
    @FXML private CheckBox rememberMeCheckbox;
    @FXML private Button logoutButton;

    private boolean isDarkMode;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load settings from the properties file
        loadSettings();
        //  Theme Toggle
        themeToggle.setOnAction(event -> toggleTheme());

        //  Refresh Rate Options
        refreshRateDropdown.getItems().addAll("10s", "30s", "1min", "5min", "Manual");
        refreshRateDropdown.setOnAction(event -> updateRefreshRate());

        //  Auto Logout Timer Options
        logoutTimerDropdown.getItems().addAll("5 min", "10 min", "30 min", "Never");
        logoutTimerDropdown.setOnAction(event -> updateLogoutTimer());

        //  Remember Me Toggle
        rememberMeCheckbox.setOnAction(event -> updateRememberMe());
    }

    private void loadSettings() {
        //  Load Theme
        isDarkMode = Boolean.parseBoolean(ConfigManager.getProperty("darkMode", "true"));
        themeToggle.setSelected(isDarkMode);
        themeToggle.setText(isDarkMode ? "🌙 Dark Mode" : "☀ Light Mode");

        //  Load Refresh Rate
        String refreshRate = ConfigManager.getProperty("refreshRate", "30s");
        refreshRateDropdown.setValue(refreshRate);

        //  Load Auto Logout Timer
        String logoutTime = ConfigManager.getProperty("logoutTime", "Never");
        logoutTimerDropdown.setValue(logoutTime);

        //  Load Remember Me
        boolean rememberMe = Boolean.parseBoolean(ConfigManager.getProperty("rememberMe", "false"));
        rememberMeCheckbox.setSelected(rememberMe);
    }

    private void toggleTheme() {
        isDarkMode = !isDarkMode;
        themeToggle.setText(isDarkMode ? "🌙 Dark Mode" : "☀ Light Mode");
        applyTheme();
        ConfigManager.setProperty("darkMode", String.valueOf(isDarkMode));
    }

    private void applyTheme() {
        scene.getStylesheets().clear();
        String themeFile = isDarkMode ? "com.warewise.client/stylesheets/stylesheet-dark.css" : "com.warewise.client/stylesheets/stylesheet-light.css";
        scene.getStylesheets().add(getClass().getClassLoader().getResource(themeFile).toExternalForm());
    }

    private void updateRefreshRate() {
        String selectedRate = refreshRateDropdown.getValue();
        ConfigManager.setProperty("refreshRate", selectedRate);
        System.out.println(" Refresh Rate Set To: " + selectedRate);
    }

    private void updateLogoutTimer() {
        String selectedTime = logoutTimerDropdown.getValue();
        ConfigManager.setProperty("logoutTime", selectedTime);
        System.out.println(" Auto Logout Set To: " + selectedTime);
    }

    private void updateRememberMe() {
        boolean rememberMe = rememberMeCheckbox.isSelected();
        ConfigManager.setProperty("rememberMe", String.valueOf(rememberMe));
        System.out.println("Remember Me Set To: " + rememberMe);
    }

    @FXML
    private void logoutAction(ActionEvent event) {
        AdminUtil.logOut();
        ApiHandler.TOKEN = null;
        AdminUtil.sessionUsername = null;
        AdminUtil.sessionRole = null;
        AdminUtil.userId = -1;
        ConfigManager.setProperty("rememberMe", "false");
        ConfigManager.setProperty("username", "null");
        ConfigManager.setProperty("password", "null");

        try {
            Stage currentStage = (Stage) logoutButton.getScene().getWindow();
            currentStage.close();
            new LoginApp().start(new Stage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
