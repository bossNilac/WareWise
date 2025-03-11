package com.warewise.admin.tui.network;

import com.warewise.admin.tui.commands.AdminUtil;
import com.warewise.admin.tui.ui.Dashboard;
import com.warewise.common.util.protocol.Protocol;

import java.io.*;
import java.net.Socket;

import static com.warewise.admin.tui.commands.UtilityCommands.doHandlingAnimation;
import static com.warewise.admin.tui.commands.UtilityCommands.simulateTyping;
import static com.warewise.admin.tui.ui.UiConstants.ANSI_GREEN;
import static com.warewise.admin.tui.ui.UiConstants.ANSI_RESET;

public class NetworkingClass {
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private final Dashboard dashboard;


    public NetworkingClass(Socket socket,Dashboard dashboard) throws IOException {
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
                            try {
                                System.out.println();
                                simulateTyping(ANSI_GREEN + "Server: " + response + ANSI_RESET, 10);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
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
        try {
            if (!(message.contains(Protocol.HELLO)
                    || message.contains(Protocol.LOGIN)))
                doHandlingAnimation(message);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        out.println(message);
    }

    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}
