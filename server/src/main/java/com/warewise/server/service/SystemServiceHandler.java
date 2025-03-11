package com.warewise.server.service;

import com.warewise.common.util.protocol.Protocol;
import com.warewise.server.server.Server;
import com.warewise.server.server.ServerConnection;

/**
 * A concrete ServiceHandler for system-level commands.
 */
public class SystemServiceHandler extends ServiceHandler {

    public SystemServiceHandler(Server server, ServerConnection connection) {
        super(server, connection);
    }

    @Override
    public void handleDisconnect() {
        System.out.println("SystemServiceHandler disconnecting...");
        server.removeClient(this);
    }

    @Override
    public void handleCommand(String command, String[] params) {
        switch (command) {
            case Protocol.SERVER_ERROR:
                if (params.length >= 1) {
                    String message = params[0];
                    System.err.println("Server error reported: " + message);
                    // Possibly log or perform further error processing.
                } else {
                    sendCommand(Protocol.ERRORTAG, "Missing message for SERVER_ERROR");
                }
                break;
            case Protocol.INVALID_COMMAND:
                if (params.length >= 1) {
                    String reason = params[0];
                    System.err.println("Invalid command: " + reason);
                } else {
                    sendCommand(Protocol.ERRORTAG, "Missing reason for INVALID_COMMAND");
                }
                break;
            case Protocol.HEARTBEAT:
                if (params.length >= 1) {
                    String timestamp = params[0];
                    System.out.println("Heartbeat received at " + timestamp);
                    // Optionally, respond with a heartbeat acknowledgment.
                } else {
                    sendCommand(Protocol.ERRORTAG, "Missing timestamp for HEARTBEAT");
                }
                break;
            case Protocol.SHUTDOWN_SIGNAL:
                if (params.length == 1) {
                    String timestamp = params[0];
                    try {
                        Thread.sleep(1000*Long.parseLong(timestamp));
                        System.exit(0);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    // Optionally, respond with a heartbeat acknowledgment.
                } else {
                    sendCommand(Protocol.ERRORTAG, "Missing time for SHUTDOWN");
                }
                break;
            default:
                sendCommand(Protocol.ERRORTAG, "Invalid system command");
                break;
        }
    }
}
