package com.warewise.client.network;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import com.warewise.client.util.AlertUtil;
import com.warewise.common.util.protocol.Protocol;

public class ServerConnection {

    private static final String host = "127.0.0.1";
    private static final int port = 12345;

    private static BufferedReader in;
    private static PrintWriter out;

    static Socket instance;

    public static Socket getConnection(){
        if(instance == null){
            try {
                instance = new Socket(host,port);
                in = new BufferedReader(new InputStreamReader(instance.getInputStream()));
                out = new PrintWriter(instance.getOutputStream(), true);
                sendMessage(Protocol.HELLO);
            } catch (IOException e) {
                instance = null;
            }
        }
        return instance;
    }

    public static boolean login(String username, String password){
        sendMessage(Protocol.LOGIN+Protocol.SEPARATOR+username+Protocol.SEPARATOR+password);
        String response ;
        try {
            while ((response = in.readLine()) != null) {
                if (response.contains(Protocol.HELLO)) {
                    continue;
                }
                if (response.equals(Protocol.LOGIN_SUCCESS)) {
                    listenToServer();
                    return true;
                } else if (response.equals(Protocol.LOGIN_FAILURE)) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    // Method for listening to server messages
    public static void listenToServer() {
        new Thread(() -> {
            try {
                String response;
                while ((response = in.readLine()) != null) {
                    if (response.startsWith("LIST")){
                        DataHandler.handle(response);
                    }
                }
            } catch (SocketException e) {
                System.err.println("Socket error: " + e.getMessage());
                AlertUtil.serverError();
            } catch (EOFException e) {
                System.err.println("Server closed connection: " + e.getMessage());
                AlertUtil.serverError();
            } catch (SocketTimeoutException e) {
                System.err.println("Connection timed out: " + e.getMessage());
                AlertUtil.serverError();
            } catch (IOException e) {
                System.err.println("I/O error: " + e.getMessage());
                close();
            } catch (Exception e) {
                System.err.println("Unexpected error: " + e.getMessage());
                close();
            }
        }).start();
    }

    // Method to send messages
    public static void sendMessage(String message) {
        out.println(message);
    }

    public static void close()  {
        try {
            in.close();
            out.close();
            instance.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.exit(0);
    }

}
