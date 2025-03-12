package com.warewise.gui.networking;


import com.warewise.gui.controller.LoginPrompt;
import com.warewise.gui.controller.MainController;
import com.warewise.gui.controller.ServerApplication;
import com.warewise.gui.util.AdminUtil;
import com.warewise.gui.util.DashboardHandler;
import com.warewise.gui.util.UtilityCommands;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class NetworkingClass {
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private final DashboardHandler dashboard;
    private final MainController controller;


    public NetworkingClass(Socket socket, DashboardHandler dashboard, FXMLLoader fxmlLoader) throws IOException {
        this.socket = socket;
        this.dashboard = dashboard;
        this.controller = fxmlLoader.getController();
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }



    // Method for listening to server messages
    public void listenToServer() {
        new Thread(() -> {
            try {
                String response;
                while ((response = in.readLine()) != null) {
                    if(response.contains(Protocol.HELLO)){continue;}
                    if(!response.contains(Protocol.SEND_BENCHMARK)) {
                        if(response.equals(Protocol.LOGIN_SUCCESS)){
                            AdminUtil.loggedIn = true;
                        }else {
                            if (response.equals(Protocol.LOGIN_FAILURE)){
                                UtilityCommands.displayNotificationPanel(3,"Login failure");
                                String[] params =  LoginPrompt.promptLogin();
                                AdminUtil.logIn(this,params[0],params[1]);

                            }else {
                                //TODO
                            }
                        }
                    }else {
                        if (controller.isDashBoardVisible()){
                            String[] params = response.split(Protocol.SEPARATOR);
                            dashboard.updateMetrics(params[1],params[2],params[3]);
                            Platform.runLater(() -> {
                                controller.setCpuNumberLabel(formatNumber(Double.parseDouble(params[1])));
                                controller.setConnNumberLabel(params[3]);
                                controller.setMemNumberLabel(formatNumber(Double.parseDouble(params[2])));
                            });
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading from server: " + e.getMessage());
            }
        }).start();
    }

    // Method to send messages
    public void sendMessage(String message) {
        out.println(message);
    }

    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }

    public static String formatNumber(double number) {
        if (number == 100) {
            return "100.0"; // Special case for exactly 100
        }

        String formatted;
        if (number >= 10 && number < 100) {
            formatted = String.format("%.2f", number); // ab.cd format
        } else if (number > 0 && number < 10) {
            formatted = String.format("%.3f", number); // a.bcd format
        } else {
            return "Invalid"; // Handle numbers outside the expected range
        }

        // Ensure the output is max 5 characters long
        return formatted.length() > 5 ? formatted.substring(0, 5) : formatted;
    }

}
