package com.warewise.gui.util;

import java.io.*;
import java.util.Properties;

public class PropertiesReader {

    private static final String CONFIG_FILE = System.getProperty("user.home") + "/WareWiseFiles/settings.properties";

    public static boolean[] loadSettings() {
        Properties properties = new Properties();
        File configFile = new File(CONFIG_FILE);
        File configDir = configFile.getParentFile();
        boolean[] settings = new boolean[4]; // [0] = auto-start-login, [1] = auto-start-with-login-prompt,
        //       [2] = auto-start-with-no-login, [3] = no-interference-mode

        try {
            // Ensure parent directory exists
            if (!configDir.exists()) {
                configDir.mkdirs();
            }

            // Create file if it does not exist
            if (!configFile.exists()) {
                configFile.createNewFile();
                System.out.println("Settings file created: " + CONFIG_FILE);
            }

            // Load existing properties
            try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
                properties.load(fis);
            }

            // Parse existing values or default to false
            settings[0] = Boolean.parseBoolean(properties.getProperty("auto-start-login", "false"));
            settings[1] = Boolean.parseBoolean(properties.getProperty("auto-start-with-login-prompt", "false"));
            settings[2] = Boolean.parseBoolean(properties.getProperty("auto-start-with-no-login", "false"));
            settings[3] = Boolean.parseBoolean(properties.getProperty("no-interference-mode", "true"));

            // Count how many are true
            int trueCount = (settings[0] ? 1 : 0)
                    + (settings[1] ? 1 : 0)
                    + (settings[2] ? 1 : 0)
                    + (settings[3] ? 1 : 0);

            // If multiple are true OR all are false, enforce default (no-interference-mode = true)
            if (trueCount != 1) {
                settings[0] = false;
                settings[1] = false;
                settings[2] = false;
                settings[3] = true;
                System.out.println("Configuration reset to default: no-interference-mode = true.");
            }

            // **Always** update the Properties object to reflect final booleans
            updatePropertiesFile(settings,properties);

            // Finally, store updated properties
            try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE, false)) {
                properties.store(fos, "Updated settings");
                fos.flush();
            }

        } catch (IOException e) {
            System.err.println("Error reading or writing properties file: " + e.getMessage());
        }

        return settings;
    }

    /**
     * Determines which setting index is true.
     *
     * @param settings A boolean array of the settings.
     * @return The index of the active setting (0, 1, 2, or 3). Returns -1 if no setting is active.
     */
    public static int getActiveIndex(boolean[] settings) {
        for (int i = 0; i < settings.length; i++) {
            if (settings[i]) {
                return i;
            }
        }
        return -1; // Should never happen because we enforce at least one being true
    }

    /**
     * Sets new settings and updates the properties file.
     *
     * @param newSettings A boolean array where only one setting should be true.
     */
    public static void setSettings(boolean[] newSettings) {
        if (newSettings.length != 4) {
            throw new IllegalArgumentException("Invalid settings array size. Must be of length 4.");
        }

        // Ensure only one setting is true
        int trueCount = 0;
        for (boolean setting : newSettings) {
            if (setting) trueCount++;
        }

        if (trueCount != 1) {
            throw new IllegalArgumentException("Only one setting can be true at a time.");
        }

        Properties properties = new Properties();
        File configFile = new File(CONFIG_FILE);

        try {
            // Load existing properties
            if (configFile.exists()) {
                try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
                    properties.load(fis);
                }
            }

            // Update properties
            updatePropertiesFile(newSettings, properties);
            System.out.println("Settings updated successfully.");

        } catch (IOException e) {
            System.err.println("Error updating settings: " + e.getMessage());
        }
    }

    /**
     * Updates the properties file based on the provided settings.
     *
     * @param settings   Boolean array representing settings.
     * @param properties Properties object to be updated.
     * @throws IOException If an error occurs during file writing.
     */
    private static void updatePropertiesFile(boolean[] settings, Properties properties) throws IOException {
        properties.setProperty("auto-start-login", String.valueOf(settings[0]));
        properties.setProperty("auto-start-with-login-prompt", String.valueOf(settings[1]));
        properties.setProperty("auto-start-with-no-login", String.valueOf(settings[2]));
        properties.setProperty("no-interference-mode", String.valueOf(settings[3]));

        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE, false)) {
            properties.store(fos, "Updated settings");
            fos.flush();
        }
    }

}
