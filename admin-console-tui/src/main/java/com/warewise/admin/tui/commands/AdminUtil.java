package com.warewise.admin.tui.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.warewise.admin.tui.ApiHandler;
import com.warewise.admin.tui.TuiClass;

import java.io.*;

import static com.warewise.admin.tui.commands.UtilityCommands.askForCred;

public class AdminUtil {

    public static boolean loggedIn = false;
    private static  String sessionUsername = null;
    private static int userId = -1;
    private static String sessionRole = null;

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
            askForCred();
            return getLoginCred();
        }

        if (username == null || password == null) {
            UtilityCommands.displayNotificationPanel(3,"Credentials were not read successfully ");
            askForCred();
            return getLoginCred();
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

    public static int getUserId() {
        return userId;
    }

    public static String getSessionRole() {
        return sessionRole;
    }

    public static boolean applyLoginSession(String data) {
        try {
            JsonArray session = JsonParser.parseString(data).getAsJsonArray();
            if (session.size() < 3) {
                return false;
            }
            ApiHandler.TOKEN = stringAt(session, 0);
            sessionRole = stringAt(session, 1);
            userId = session.get(2).getAsInt();
            return ApiHandler.TOKEN != null && !ApiHandler.TOKEN.isBlank();
        } catch (RuntimeException e) {
            return false;
        }
    }

    public static void logOut() {
        if (ApiHandler.TOKEN != null) {
            ApiHandler.sendApiCall("GET", "auth", "logout", null);
        }
        ApiHandler.TOKEN = null;
        loggedIn = false;
        sessionUsername = null;
        sessionRole = null;
        userId = -1;
    }

    private static String stringAt(JsonArray array, int index) {
        JsonElement value = array.get(index);
        return value == null || value.isJsonNull() ? null : value.getAsString();
    }
}


