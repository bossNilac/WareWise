package com.warewise.admin.tui.commands;

import com.warewise.admin.tui.TuiClass;
import com.warewise.admin.tui.network.NetworkingClass;
import com.warewise.common.util.protocol.Protocol;

import java.io.*;

public class AdminUtil {

    public static boolean isServerStarted = false;
    public static boolean loggedIn = false;
    private static  String sesionUsername = null;


    public static void startServer(){
            String jarPath = "C:\\Users\\Calin\\IdeaProjects\\WareWise\\admin-console-tui\\libs\\my-server-jar-1.0-SNAPSHOT-all.jar";
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
        if (name.equals(sesionUsername)){
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

    public static String[] getLoginCred(){
        String username = null;
        String password = null;

        // Read and extract credentials manually
        try (BufferedReader reader = new BufferedReader(new FileReader(TuiClass.CREDENTIALS_FILE))) {
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
        sesionUsername = username;
        return new String[]{username, password}; // Return extracted credentials
    }

    private static String extractValue(String jsonLine) {
        return jsonLine.split(":")[1].trim().replace("\"", "").replace(",", "");
    }

}


