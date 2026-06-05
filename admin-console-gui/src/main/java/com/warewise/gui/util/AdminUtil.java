package com.warewise.gui.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.warewise.gui.networking.ApiHandler;
import com.warewise.gui.networking.ApiResponse;
import com.warewise.gui.util.enums.UserRole;

import java.io.*;

public class AdminUtil {

    public static boolean loggedIn = false;
    public static  String sessionUsername = null;
    public static int userId = -1;
    public static UserRole sessionRole = null;
    public static final String CREDENTIALS_FILE = System.getProperty("user.home") + "/WareWise/user_credentials.json";

    public static void notLoggedInError(){
        UtilityCommands.displayNotificationPanel(3,"User not logged in");
    }

    public static void saveLoginCred(String username, String password) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CREDENTIALS_FILE))) {
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

        StringBuilder sb = new StringBuilder();

        // Read and extract credentials manually
        try (BufferedReader reader = new BufferedReader(new FileReader(CREDENTIALS_FILE))) {
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

    public static void doLogin(){
        String credentials = AdminUtil.getLoginCred();
        if (credentials == null) {
            loggedIn = false;
            return;
        }
        String loginResponse = ApiHandler.sendApiCall("POST","auth","login",credentials);
        ApiResponse response = new ApiResponse(loginResponse);
        if(!response.getSuccess()){
            UtilityCommands.displayNotificationPanel(3,"Login Failed,wrong login credentials");
            loggedIn = false;
        }else {
            if (!applyLoginSession(response.getData())) {
                UtilityCommands.displayNotificationPanel(3,"Login response was invalid");
                loggedIn = false;
                return;
            }
            loggedIn = true;
        }
    }

    public static void logOut(){
        if (ApiHandler.TOKEN == null) {
            clearSession();
            return;
        }
        String loginResponse = ApiHandler.sendApiCall("GET","auth","logout",null);
        ApiResponse response = new ApiResponse(loginResponse);
        if(!response.getSuccess()){
            UtilityCommands.displayNotificationPanel(3,"Logout failed ");
            clearSession();
        }else {
            clearSession();
        }
    }

    private static void clearSession() {
        loggedIn = false;
        ApiHandler.TOKEN = null;
        sessionUsername = null;
        sessionRole = null;
        userId = -1;
    }

    public static boolean checkIfAdmin(){
        if (sessionRole != UserRole.ADMIN) {
            UtilityCommands.displayWarning("You are not logged in as an Administrator ",false);
            return false;
        }
        return true;
    }

    private static boolean applyLoginSession(String data) {
        try {
            JsonArray session = JsonParser.parseString(data).getAsJsonArray();
            if (session.size() < 3) {
                return false;
            }
            ApiHandler.TOKEN = stringAt(session, 0);
            sessionRole = UserRole.fromLabel(stringAt(session, 1));
            userId = session.get(2).getAsInt();
            return ApiHandler.TOKEN != null && !ApiHandler.TOKEN.isBlank();
        } catch (RuntimeException e) {
            return false;
        }
    }

    private static String stringAt(JsonArray array, int index) {
        JsonElement value = array.get(index);
        return value == null || value.isJsonNull() ? null : value.getAsString();
    }

}


