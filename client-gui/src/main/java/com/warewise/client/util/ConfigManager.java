package com.warewise.client.util;

import java.io.*;
import java.util.Properties;

public class ConfigManager {
    private static final String CONFIG_FILE = "src/main/resources/com.warewise.client/config/config.properties";
    private static Properties properties = new Properties();

    static {
        loadProperties();
    }

    // Load settings from the config file
    public static void loadProperties() {
        File file = new File(CONFIG_FILE);

        // If file doesn't exist or is corrupted, create a new one
        if (!file.exists() || file.length() == 0) {
            System.out.println("config.properties not found or empty. Creating default settings...");
            setDefaultProperties();
            saveProperties();
            return;
        }

        // Try loading properties
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
        } catch (IOException e) {
            System.out.println("Error reading config.properties: " + e.getMessage());
            System.out.println("Resetting to default settings...");
            setDefaultProperties();
            saveProperties();
        }
    }

    // Apply default settings if the file is missing or corrupted
    private static void setDefaultProperties() {
        properties.setProperty("darkMode", "true");
        properties.setProperty("refreshRate", "30s");
        properties.setProperty("logoutTime", "Never");
        properties.setProperty("rememberMe", "false");
    }

    // Get a setting value
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    // Set a setting value and save
    public static void setProperty(String key, String value) {
        properties.setProperty(key, value);
        saveProperties();
    }

    // Save settings to the config file
    private static void saveProperties() {
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.store(output, "Warehouse Management App Settings");
        } catch (IOException e) {
            System.out.println("Could not save config.properties: " + e.getMessage());
        }
    }
}
