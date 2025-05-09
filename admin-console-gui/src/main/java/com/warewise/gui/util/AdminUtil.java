package com.warewise.gui.util;

import com.warewise.gui.networking.Protocol;
import com.warewise.gui.networking.NetworkingClass;

import java.io.*;

public class AdminUtil {

    public static boolean isServerStarted = false;
    public static boolean loggedIn = false;

    private static  String sessionUsername = null;
    private static final String jarPath = System.getProperty("user.home") + "/WareWiseFiles/my-server-jar-1.0-all.jar";
    public static final String CREDENTIALS_FILE = System.getProperty("user.home") + "/WareWiseFiles/passwd/user_credentials.json";

    public static void startServer(){
            try {
                String os = System.getProperty("os.name").toLowerCase();
                ProcessBuilder processBuilder;

                if (os.contains("win")) {
                    // Windows (cmd.exe)
                    processBuilder = new ProcessBuilder("cmd", "/c", "start", "cmd", "/k",
                            "java -jar \"" + jarPath + "\" " + 12345 );                }
                else if (os.contains("mac")) {
                    // macOS (Terminal.app)
                    processBuilder = new ProcessBuilder("osascript", "-e",
                            "tell app \"Terminal\" to do script \"java -jar " + jarPath + "\" "+ 12345);
                } else {
                    // Linux (GNOME/KDE/Xfce Terminal)
                    processBuilder = new ProcessBuilder("x-terminal-emulator", "-e", "java -jar " + jarPath+ "\" "+ 12345);
                }

                isServerStarted = true;
                processBuilder.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
    }


    public static void closeServer(NetworkingClass object){
        if(isServerStarted){
            object.sendMessage(Protocol.SHUTDOWN_SIGNAL+Protocol.SEPARATOR+10);
            isServerStarted = false;
            loggedIn = false;
        }else {
            notLoggedInError();
        }
    }

    public static void listUsers(NetworkingClass object){
        if(isServerStarted && loggedIn) object.sendMessage(Protocol.LIST_ONLINE_USERS);
        else notLoggedInError();
    }

    public static void kickUser(NetworkingClass object,String name){
        if (name.equals(sessionUsername)){
            UtilityCommands.displayNotificationPanel(2,"Cannot kick yourself ");
            return;
        }
        if(isServerStarted && loggedIn)object.sendMessage(Protocol.KICK_USER+Protocol.SEPARATOR+name);
        else notLoggedInError();
    }

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

    public static String[] getLoginCred(){
        String username = null;
        String password = null;

        // Read and extract credentials manually
        try (BufferedReader reader = new BufferedReader(new FileReader(CREDENTIALS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim(); // Remove leading/trailing spaces

                if (line.startsWith("\"username\"")) {
                    username = extractValue(line);
                } else if (line.startsWith("\"password\"")) {
                    password = extractValue(line);
                }
            }
        } catch (IOException e) {
            UtilityCommands.displayNotificationPanel(3,"Credentials were not read successfully ");
            return null;
        }

        if (username == null || password == null) {
            UtilityCommands.displayNotificationPanel(3,"Credentials were not read successfully ");
            return null;
        }
        sessionUsername = username;
        return new String[]{username, password}; // Return extracted credentials
    }

    public static void logIn(NetworkingClass object,String sessionUsername,String passwd){
        object.sendMessage(Protocol.HELLO);
        object.sendMessage(Protocol.LOGIN+Protocol.SEPARATOR+sessionUsername+Protocol.SEPARATOR+passwd);
    }

    public static void logOut(NetworkingClass object){
        System.out.println(sessionUsername);
        object.sendMessage(Protocol.LOGOUT+sessionUsername);
    }

    private static String extractValue(String jsonLine) {
        return jsonLine.split(":")[1].trim().replace("\"", "").replace(",", "");
    }

    public static String getSessionUsername() {
        return sessionUsername;
    }

}


