package com.warewise.gui.util;

import java.io.*;
import java.util.Properties;

public class PropertiesReader {

    private static final String CONFIG_FILE = "config.properties";

    public static void main(String[] args) {
        loadSettings();
    }

    public static void loadSettings() {
        Properties properties = new Properties();

        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            properties.load(fis);

            boolean autoStartLogin = Boolean.parseBoolean(properties.getProperty("auto-start-login", "false"));
            boolean autoStartWithLoginPrompt = Boolean.parseBoolean(properties.getProperty("auto-start-with-login-prompt", "false"));
            boolean autoStartWithNoLogin = Boolean.parseBoolean(properties.getProperty("auto-start-with-no-login", "false"));

            // Count how many are true
            int trueCount = (autoStartLogin ? 1 : 0) +
                    (autoStartWithLoginPrompt ? 1 : 0) +
                    (autoStartWithNoLogin ? 1 : 0);

            if (trueCount > 1) {
                // Reset to: auto-start-with-no-login = true, others = false
                autoStartLogin = false;
                autoStartWithLoginPrompt = false;
                autoStartWithNoLogin = true;

                // Update properties
                properties.setProperty("auto-start-login", "false");
                properties.setProperty("auto-start-with-login-prompt", "false");
                properties.setProperty("auto-start-with-no-login", "true");

                // Save corrected properties back to file
                try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
                    properties.store(fos, "Auto-corrected invalid settings: Only auto-start-with-no-login is true.");
                    System.out.println("Multiple true values found. Reset to auto-start-with-no-login = true.");
                }
            }

            System.out.println("Auto-Start Login: " + autoStartLogin);
            System.out.println("Auto-Start with Login Prompt: " + autoStartWithLoginPrompt);
            System.out.println("Auto-Start with No Login: " + autoStartWithNoLogin);

        } catch (IOException e) {
            System.err.println("Error reading or writing properties file: " + e.getMessage());
        }
    }
}
