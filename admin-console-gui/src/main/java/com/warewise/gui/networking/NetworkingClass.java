package com.warewise.gui.networking;


import com.warewise.gui.util.AdminUtil;
import com.warewise.gui.util.DashboardHandler;

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


    public NetworkingClass(Socket socket,DashboardHandler dashboard) throws IOException {
        this.socket = socket;
        this.dashboard = dashboard;
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
                                //TODO
                            }else {
                                //TODO
                            }
                        }
                    }else {
                        String[] params = response.split(Protocol.SEPARATOR);
                        dashboard.updateMetrics(params[1],params[2],params[3]);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading from server: " + e.getMessage());
            }
        }).start();
    }

    // Method to send messages
    public void sendMessage(String message) {
        //TODO
        out.println(message);
    }

    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}
