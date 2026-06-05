package com.warewise.client.util;


import com.google.gson.Gson;
import com.warewise.client.networking.ApiHandler;
import com.warewise.client.networking.ApiResponse;

import java.util.LinkedHashMap;
import java.util.Map;

public class AdminUtil {

    public static boolean loggedIn = false;
    public static  String sessionUsername = null;
    public static  int userId = -1;

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
            ApiHandler.TOKEN = response.getData();
            loggedIn = true;
            return true;
        }
    }

    public static void logOut(){
        String loginResponse = ApiHandler.sendApiCall("GET","auth","logout",null);
        ApiResponse response = new ApiResponse(loginResponse);
        if(!response.getSuccess()){
            UtilityCommands.displayNotificationPanel(3,"Logout failed ");
            loggedIn = true;
        }else {
            loggedIn = false;
        }
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


