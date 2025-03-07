package com.warewise.server.service;

import com.warewise.common.logs.AppLogger;
import com.warewise.common.logs.LogLevel;
import com.warewise.common.model.Supplier;
import com.warewise.common.util.protocol.Protocol;
import com.warewise.server.server.Server;
import com.warewise.server.server.ServerConnection;
import com.warewise.common.model.StockAlert;
import com.warewise.common.util.enums.StockAlertStatus;
import com.warewise.server.server.util.ServerUtil;

import java.util.stream.Collectors;

/**
 * A concrete ServiceHandler for stock alert management commands.
 */
public class StockAlertServiceHandler extends ServiceHandler {

    public StockAlertServiceHandler(Server server, ServerConnection connection) {
        super(server, connection);
    }
    
    @Override
    public void handleDisconnect() {
        System.out.println("StockAlertManagementServiceHandler disconnecting...");
        server.removeClient(this);
    }

    @Override
    public void handleCommand(String command, String[] params) {
        String connectionUser = connection.getAuthHandler().getRegisteredUser();
        String response = null;
        switch (command) {
            case Protocol.STOCK_ALERT:
                if (connectionUser == null) {
                    response=sendCommand(Protocol.ERRORTAG, "Not logged in");
                } else {
                    if (params.length == 3) {
                        int productID = Integer.parseInt(params[0]);
                        StockAlertStatus threshold = StockAlertStatus.valueOf(params[1]);
                        String createdAt = params[2];

                        StockAlert stockAlert = new StockAlert(productID, threshold, createdAt, null);
                        server.getDbLoader().addStockAlert(stockAlert);
                        response=sendCommand(Protocol.STOCK_ALERT, "Stock alert added successfully: " + stockAlert.getID());
                    } else {
                        response=sendCommand(Protocol.ERRORTAG, "Invalid parameters for ADD_STOCK_ALERT");
                    }
                }
                break;

            case Protocol.RESOLVE_ALERT:
                if (connectionUser == null) {
                    response=sendCommand(Protocol.ERRORTAG, "Not logged in");
                } else {
                    if (params.length == 3) {
                        int stockAlertID = Integer.parseInt(params[0]);
                        StockAlertStatus threshold = StockAlertStatus.valueOf(params[1]);
                        String resolved = params[2];

                        StockAlert stockAlert = serverUtil.stockAlertExists(stockAlertID);
                        if (stockAlert != null) {
                            server.getDbLoader().updateStockAlert(stockAlert, threshold, resolved);
                            response=sendCommand(Protocol.RESOLVE_ALERT, "Stock alert " + stockAlertID + " updated successfully");
                        } else {
                            response=sendCommand(Protocol.ERRORTAG, "Stock alert does not exist");
                        }
                    } else {
                        response=sendCommand(Protocol.ERRORTAG, "Invalid parameters for UPDATE_STOCK_ALERT");
                    }
                }
                break;

            case Protocol.UPDATE_STOCK_ALERT:
                if (connectionUser == null) {
                    response=sendCommand(Protocol.ERRORTAG, "Not logged in");
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
                                response=sendCommand(Protocol.UPDATE_STOCK_ALERT, "Stock alert " + stockAlertID + " updated successfully");
                            } else {
                                response=sendCommand(Protocol.ERRORTAG, "No changes detected for stock alert " + stockAlertID);
                            }
                        } else {
                            response=sendCommand(Protocol.ERRORTAG, "Stock alert does not exist");
                        }
                    } else {
                        response=sendCommand(Protocol.ERRORTAG, "Invalid parameters for UPDATE_STOCK_ALERT");
                    }
                }
                break;

            case Protocol.LIST_STOCK_ALERTS:
                System.out.println("Listing stock alerts.");
                String suppliers = server.getStockAlertList().stream()
                        .map(StockAlert::toString)
                        .collect(Collectors.joining(";"));
                response=sendCommand(Protocol.LIST_SUPPLIERS, suppliers);
                break;

            case Protocol.DELETE_STOCK_ALERT:
                if (connectionUser == null) {
                    response=sendCommand(Protocol.ERRORTAG, "Not logged in");
                } else {
                    if (params.length == 1) {
                        int stockAlertID = Integer.parseInt(params[0]);

                        StockAlert stockAlert = serverUtil.stockAlertExists(stockAlertID);
                        if (stockAlert != null) {
                            server.getDbLoader().deleteStockAlert(stockAlert);
                            response=sendCommand(Protocol.DELETE_STOCK_ALERT, "Stock alert " + stockAlertID + " deleted successfully");
                        } else {
                            response=sendCommand(Protocol.ERRORTAG, "Stock alert does not exist");
                        }
                    } else {
                        response=sendCommand(Protocol.ERRORTAG, "Invalid parameters for DELETE_STOCK_ALERT");
                    }
                }
                break;    

            default:
                response=sendCommand(Protocol.ERRORTAG, "Invalid command in StockAlertManagementServiceHandler");
                break;
        }
    AppLogger.log(LogLevel.INFO, ServerUtil.SENT_COMMAND+response);
    }
}
