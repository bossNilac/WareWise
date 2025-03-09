package com.warewise.admin.tui.network;

import java.io.*;
import java.net.Socket;

import static com.warewise.admin.tui.commands.UtilityCommands.doHandlingAnimation;

public class NetworkingClass {
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private final ServerResponseHandler handler;


    public NetworkingClass(Socket socket,ServerResponseHandler handler) throws IOException {
        this.socket = socket;
        this.handler = handler;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }



    // Method for listening to server messages
    public void listenToServer() {
        new Thread(() -> {
            try {
                String response;
                while ((response = in.readLine()) != null) {
                    handler.setServerResponse(response);
                }
            } catch (IOException e) {
                System.err.println("Error reading from server: " + e.getMessage());
            }
        }).start();
    }

    // Method to send messages
    public void sendMessage(String message) {
        try {
            doHandlingAnimation(message);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        handler.sendCommand();
        out.println(message);
    }

    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}
