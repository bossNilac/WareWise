package com.warewise.admin.tui.commands;

import com.warewise.admin.tui.TuiClass;

import java.io.*;

public class AdminUtil {

    public static boolean loggedIn = false;
    private static  String sessionUsername = null;

    public static void notLoggedInError(){
        UtilityCommands.displayNotificationPanel(3,"User not logged in");
    }

    public static void saveLoginCred(String username, String password) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TuiClass.CREDENTIALS_FILE))) {
            writer.write("{\n");
            writer.write("  \"username\": \"" + username + "\",\n");
            writer.write("  \"password\": \"" + password + "\"\n");
            writer.write("}");
            UtilityCommands.displayNotificationPanel(1,"Credentials saved successfully ");
        } catch (IOException e) {
            UtilityCommands.displayNotificationPanel(3,"Credentials were not saved successfully ");
        }
    }

    public static String getLoginCred(){
        String username = null;
        String password = null;

        StringBuffer sb = new StringBuffer();

        // Read and extract credentials manually
        try (BufferedReader reader = new BufferedReader(new FileReader(TuiClass.CREDENTIALS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim(); // Remove leading/trailing spaces
                sb.append(line);

                if (line.startsWith("\"username\"")) {
                    username = extractValue(line);
                } else if (line.startsWith("\"password\"")) {
                    password = extractValue(line);
                }
            }
        } catch (IOException e) {
            UtilityCommands.displayNotificationPanel(3,"File not found ");
            UtilityCommands.displayNotificationPanel(3,"Credentials were not read successfully ");
            return null;
        }

        if (username == null || password == null) {
            UtilityCommands.displayNotificationPanel(3,"Credentials were not read successfully ");
            return null;
        }
        sessionUsername = username;
        return sb.toString(); // Return extracted credentials
    }

    private static String extractValue(String jsonLine) {
        return jsonLine.split(":")[1].trim().replace("\"", "").replace(",", "");
    }

    public static String getSessionUsername() {
        return sessionUsername;
    }
}


