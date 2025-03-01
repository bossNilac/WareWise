package com.warewise.server.service;

import com.warewise.common.util.protocol.Protocol;
import com.warewise.server.server.Server;
import com.warewise.server.server.ServerConnection;

/**
 * A concrete ServiceHandler for stock alert commands.
 */
public class StockAlertServiceHandler extends ServiceHandler {

    public StockAlertServiceHandler(Server server, ServerConnection connection) {
        super(server, connection);
    }

    @Override
    public void sendCommand(String command, String... params) {
        StringBuilder sb = new StringBuilder(command);
        for (String param : params) {
            sb.append(Protocol.SEPARATOR).append(param);
        }
        connection.sendMessage(sb.toString());
    }

    @Override
    public void handleDisconnect() {
        System.out.println("StockAlertServiceHandler disconnecting...");
        server.removeClient(this);
    }

    @Override
    public void handleCommand(String command, String[] params) {
        switch (command) {
            case Protocol.STOCK_ALERT:
                if (params.length >= 2) {
                    String productID = params[0];
                    String threshold = params[1];
                    System.out.println("Stock alert for product " + productID + " with threshold " + threshold);
                    sendCommand(Protocol.STOCK_ALERT, "Alert registered for product " + productID);
                } else {
                    sendCommand(Protocol.ERRORTAG, "Invalid parameters for STOCK_ALERT");
                }
                break;
            case Protocol.RESOLVE_ALERT:
                if (params.length >= 1) {
                    String alertID = params[0];
                    System.out.println("Resolving alert: " + alertID);
                    sendCommand(Protocol.RESOLVE_ALERT, "Alert " + alertID + " resolved");
                } else {
                    sendCommand(Protocol.ERRORTAG, "Invalid parameters for RESOLVE_ALERT");
                }
                break;
            default:
                sendCommand(Protocol.ERRORTAG, "Invalid command in StockAlertServiceHandler");
                break;
        }
    }
}
