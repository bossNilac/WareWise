package com.warewise.server.service;

import com.warewise.common.model.Supplier;
import com.warewise.common.util.protocol.Protocol;
import com.warewise.server.server.Server;
import com.warewise.server.server.ServerConnection;
import com.warewise.common.model.StockAlert;
import com.warewise.common.util.enums.StockAlertStatus;

import java.util.stream.Collectors;

/**
 * A concrete ServiceHandler for stock alert management commands.
 */
public class StockAlertServiceHandler extends ServiceHandler {

    public StockAlertServiceHandler(Server server, ServerConnection connection) {
        super(server, connection);
    }

    @Override
    public void sendCommand(String command, String... params) {
        // Implementation not provided as per request.
    }

    @Override
    public void handleDisconnect() {
        System.out.println("StockAlertManagementServiceHandler disconnecting...");
        server.removeClient(this);
    }

    @Override
    public void handleCommand(String command, String[] params) {
        String connectionUser = connection.getAuthHandler().getRegisteredUser();

        switch (command) {
            case Protocol.STOCK_ALERT:
                if (connectionUser == null) {
                    sendCommand(Protocol.ERRORTAG, "Not logged in");
                } else {
                    if (params.length == 3) {
                        int productID = Integer.parseInt(params[0]);
                        StockAlertStatus threshold = StockAlertStatus.valueOf(params[1]);
                        String createdAt = params[2];

                        StockAlert stockAlert = new StockAlert(productID, threshold, createdAt, null);
                        server.getDbLoader().addStockAlert(stockAlert);
                        sendCommand(Protocol.STOCK_ALERT, "Stock alert added successfully: " + stockAlert.getID());
                    } else {
                        sendCommand(Protocol.ERRORTAG, "Invalid parameters for ADD_STOCK_ALERT");
                    }
                }
                break;

            case Protocol.RESOLVE_ALERT:
                if (connectionUser == null) {
                    sendCommand(Protocol.ERRORTAG, "Not logged in");
                } else {
                    if (params.length == 3) {
                        int stockAlertID = Integer.parseInt(params[0]);
                        StockAlertStatus threshold = StockAlertStatus.valueOf(params[1]);
                        String resolved = params[2];

                        StockAlert stockAlert = serverUtil.stockAlertExists(stockAlertID);
                        if (stockAlert != null) {
                            server.getDbLoader().updateStockAlert(stockAlert, threshold, resolved);
                            sendCommand(Protocol.RESOLVE_ALERT, "Stock alert " + stockAlertID + " updated successfully");
                        } else {
                            sendCommand(Protocol.ERRORTAG, "Stock alert does not exist");
                        }
                    } else {
                        sendCommand(Protocol.ERRORTAG, "Invalid parameters for UPDATE_STOCK_ALERT");
                    }
                }
                break;

            case Protocol.UPDATE_STOCK_ALERT:
                if (connectionUser == null) {
                    sendCommand(Protocol.ERRORTAG, "Not logged in");
                } else {
                    if (params.length == 5) {
                        int stockAlertID = Integer.parseInt(params[0]);
                        int productID = Integer.parseInt(params[1]);
                        StockAlertStatus threshold = StockAlertStatus.valueOf(params[2]);
                        String createdAt = params[3];
                        String resolved = params[4];

                        StockAlert stockAlert = serverUtil.stockAlertExists(stockAlertID);
                        if (stockAlert != null) {
                            System.out.println("Updating stock alert " + stockAlertID);
                            if (server.getDbLoader().updateStockAlert(stockAlert, productID, threshold, createdAt, resolved)) {
                                sendCommand(Protocol.UPDATE_STOCK_ALERT, "Stock alert " + stockAlertID + " updated successfully");
                            } else {
                                sendCommand(Protocol.ERRORTAG, "No changes detected for stock alert " + stockAlertID);
                            }
                        } else {
                            sendCommand(Protocol.ERRORTAG, "Stock alert does not exist");
                        }
                    } else {
                        sendCommand(Protocol.ERRORTAG, "Invalid parameters for UPDATE_STOCK_ALERT");
                    }
                }
                break;

            case Protocol.LIST_STOCK_ALERTS:
                System.out.println("Listing stock alerts.");
                String suppliers = server.getStockAlertList().stream()
                        .map(StockAlert::toString)
                        .collect(Collectors.joining(";"));
                sendCommand(Protocol.LIST_SUPPLIERS, suppliers);
                break;

            case Protocol.DELETE_STOCK_ALERT:
                if (connectionUser == null) {
                    sendCommand(Protocol.ERRORTAG, "Not logged in");
                } else {
                    if (params.length == 1) {
                        int stockAlertID = Integer.parseInt(params[0]);

                        StockAlert stockAlert = serverUtil.stockAlertExists(stockAlertID);
                        if (stockAlert != null) {
                            server.getDbLoader().deleteStockAlert(stockAlert);
                            sendCommand(Protocol.DELETE_STOCK_ALERT, "Stock alert " + stockAlertID + " deleted successfully");
                        } else {
                            sendCommand(Protocol.ERRORTAG, "Stock alert does not exist");
                        }
                    } else {
                        sendCommand(Protocol.ERRORTAG, "Invalid parameters for DELETE_STOCK_ALERT");
                    }
                }
                break;    

            default:
                sendCommand(Protocol.ERRORTAG, "Invalid command in StockAlertManagementServiceHandler");
                break;
        }
    }
}
