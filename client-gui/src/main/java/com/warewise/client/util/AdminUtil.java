package com.warewise.client.util;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.warewise.client.networking.ApiHandler;
import com.warewise.client.networking.ApiResponse;
import com.warewise.client.util.enums.UserRole;

import java.util.LinkedHashMap;
import java.util.Map;

public class AdminUtil {

    public static boolean loggedIn = false;
    public static  String sessionUsername = null;
    public static  int userId = -1;
    public static UserRole sessionRole = null;

    public static void notLoggedInError(){
        UtilityCommands.displayNotificationPanel(3,"User not logged in");
    }

    public static void saveLoginCred(String username, String password) {
        ConfigManager.getProperties().put("username", username);
        ConfigManager.getProperties().put("password", password);
        ConfigManager.saveProperties();
    }

    public static String getLoginCred(){
        String username = ConfigManager.getProperties().get("username").toString();
        String password = ConfigManager.getProperties().get("password").toString();

        Map<String, Object> m = new LinkedHashMap<>();
        m.put("username",  username);
        m.put("password",       password);

        sessionUsername = username;

        return new Gson().toJson(m);

    }


    public static boolean doLogin(){
        System.out.println(AdminUtil.getLoginCred());
        String loginResponse = ApiHandler.sendApiCall("POST","auth","login",AdminUtil.getLoginCred());
        ApiResponse response = new ApiResponse(loginResponse);
        if(!response.getSuccess()){
            UtilityCommands.displayNotificationPanel(3,"Login Failed,wrong login credentials");
            return false;
        }else {
            if (!applyLoginSession(response.getData())) {
                UtilityCommands.displayNotificationPanel(3,"Login response was invalid");
                loggedIn = false;
                return false;
            }
            loggedIn = true;
            return true;
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

    public static boolean parseResponse (String unparsedResponse){
        ApiResponse response = new ApiResponse(unparsedResponse);
        System.out.println(unparsedResponse);
        if(unparsedResponse == null || !response.getSuccess()  ){
            UtilityCommands.displayNotificationPanel(3,"Unsuccessful operation");
        }else {
            UtilityCommands.displayNotificationPanel(1,"Successful operation");
        }

        return !(unparsedResponse == null || !response.getSuccess());

    }

}


