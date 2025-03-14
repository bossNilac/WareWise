package com.warewise.server.service;

import com.warewise.common.logs.AppLogger;
import com.warewise.common.logs.LogLevel;
import com.warewise.common.util.protocol.Protocol;
import com.warewise.server.server.Server;
import com.warewise.server.server.ServerConnection;
import com.warewise.common.model.Order;
import com.warewise.common.util.enums.OrderStatus;
import com.warewise.server.server.util.ServerUtil;

import java.util.stream.Collectors;

/**
 * A concrete ServiceHandler for order management commands.
 */
public class OrderManagementServiceHandler extends ServiceHandler {

    public OrderManagementServiceHandler(Server server, ServerConnection connection) {
        super(server, connection);
    }

    @Override
    public void handleDisconnect() {
        System.out.println("OrderManagementServiceHandler disconnecting...");
        server.removeClient(this);
    }

    @Override
    public void handleCommand(String command, String[] params) {
        String connectionUser = connection.getAuthHandler().getRegisteredUser();
        String response = null;

        switch (command) {
            case Protocol.CREATE_ORDER:
                if (connectionUser == null) {
                    response=sendCommand(Protocol.ERRORTAG, "Not logged in");
                } else {
                    if (params.length == 5) {
                        String customerName = params[0];
                        String customerEmail = params[1];
                        OrderStatus status = OrderStatus.valueOf(params[2]);
                        String createdAt = params[3];
                        String updatedAt = params[4];

                        Order order = new Order(customerName, customerEmail, status, createdAt, updatedAt);
                        server.getDbLoader().addOrder(order);
                        response=sendCommand(Protocol.CREATE_ORDER, "Order added successfully: " + order.getID());
                    } else {
                        response=sendCommand(Protocol.ERRORTAG, "Invalid parameters for ADD_ORDER");
                    }
                }
                break;

            case Protocol.UPDATE_ORDER:
                if (connectionUser == null) {
                    response=sendCommand(Protocol.ERRORTAG, "Not logged in");
                } else {
                    if (params.length == 5) {
                        int orderID = Integer.parseInt(params[0]);
                        String customerName = params[1];
                        String customerEmail = params[2];
                        OrderStatus status = OrderStatus.valueOf(params[3]);
                        String updatedAt = params[4];

                        Order order = serverUtil.orderExists(orderID);
                        if (order != null) {
                            System.out.println("Updating order " + orderID);
                            if (server.getDbLoader().updateOrder(order, customerName, customerEmail, status, updatedAt)) {
                                response=sendCommand(Protocol.UPDATE_ORDER, "Order " + orderID + " updated successfully");
                            } else {
                                response=sendCommand(Protocol.ERRORTAG, "No changes detected for order " + orderID);
                            }
                        } else {
                            response=sendCommand(Protocol.ERRORTAG, "Order does not exist, use ADD_ORDER");
                        }
                    } else {
                        response=sendCommand(Protocol.ERRORTAG, "Invalid parameters for UPDATE_ORDER");
                    }
                }
                break;

            case Protocol.DELETE_ORDER:
                if (connectionUser == null) {
                    response=sendCommand(Protocol.ERRORTAG, "Not logged in");
                } else {
                    if (params.length == 1) {
                        int orderID = Integer.parseInt(params[0]);

                        Order order = serverUtil.orderExists(orderID);
                        if (order != null) {
                            System.out.println("Deleting order: " + orderID);
                            server.getDbLoader().deleteOrder(order);
                            response=sendCommand(Protocol.DELETE_ORDER, "Order " + orderID + " deleted successfully");
                        } else {
                            response=sendCommand(Protocol.ERRORTAG, "Order does not exist");
                        }
                    } else {
                        response=sendCommand(Protocol.ERRORTAG, "Invalid parameters for DELETE_ORDER");
                    }
                }
                break;

            case Protocol.LIST_ORDERS:
                System.out.println("Listing orders.");
                String orders = server.getOrders().stream()
                        .map(Order::toString)
                        .collect(Collectors.joining(Protocol.SEPARATOR));
                response=sendCommand(Protocol.LIST_ORDERS, orders);
                break;

            default:
                response=sendCommand(Protocol.ERRORTAG, "Invalid command in OrderManagementServiceHandler");
                break;
        }
    AppLogger.log(LogLevel.INFO, ServerUtil.SENT_COMMAND+response);
    }
}
