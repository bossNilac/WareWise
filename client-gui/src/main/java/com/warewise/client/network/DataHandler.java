package com.warewise.client.network;

import com.warewise.common.model.*;
import com.warewise.common.util.enums.OrderStatus;
import com.warewise.common.util.enums.StockAlertStatus;
import com.warewise.common.util.enums.UserRole;
import com.warewise.common.util.protocol.Protocol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataHandler {
    public static List<User> userList;
    public static List<Category> categoryList;
    public static List<Supplier> supplierList;
    public static List<Item> itemList;
    public static List<Inventory> inventoryList;
    public static List<Order> orderList;
    public static List<StockAlert> stockAlertList;

    public static final String[] CATEGORIES_COLUMNS = {
            "category_id", "category_name", "description"
    };

    public static void askForWarehouseData(){
        try {
            ServerConnection.sendMessage(Protocol.LIST_USERS+Protocol.SEPARATOR);
            Thread.sleep(1000);
            ServerConnection.sendMessage(Protocol.LIST_INVENTORY+Protocol.SEPARATOR);
            Thread.sleep(1000);
            ServerConnection.sendMessage(Protocol.LIST_ITEMS+Protocol.SEPARATOR);
            Thread.sleep(1000);
            ServerConnection.sendMessage(Protocol.LIST_CATEGORIES+Protocol.SEPARATOR);
            Thread.sleep(1000);
            ServerConnection.sendMessage(Protocol.LIST_ORDERS+Protocol.SEPARATOR);
            Thread.sleep(1000);
            ServerConnection.sendMessage(Protocol.LIST_STOCK_ALERTS+Protocol.SEPARATOR);
            Thread.sleep(1000);
            ServerConnection.sendMessage(Protocol.LIST_SUPPLIERS+Protocol.SEPARATOR);
            Thread.sleep(1000);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void askForWarehouseData(String table){
        try {
            switch (table) {
                case "Users":
                    ServerConnection.sendMessage(Protocol.LIST_USERS+Protocol.SEPARATOR);
                    break;
                case "Category":
                    ServerConnection.sendMessage(Protocol.LIST_CATEGORIES+Protocol.SEPARATOR);
                    break;
                case "Inventory":
                    ServerConnection.sendMessage(Protocol.LIST_INVENTORY+Protocol.SEPARATOR);
                    break;
                case "Item":
                    ServerConnection.sendMessage(Protocol.LIST_ITEMS+Protocol.SEPARATOR);
                    break;
                case "Suppliers":
                    ServerConnection.sendMessage(Protocol.LIST_SUPPLIERS+Protocol.SEPARATOR);
                    break;
                case "Orders":
                    ServerConnection.sendMessage(Protocol.LIST_ORDERS+Protocol.SEPARATOR);
                    break;
                case "Alerts":
                    ServerConnection.sendMessage(Protocol.LIST_STOCK_ALERTS+Protocol.SEPARATOR);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void handle(String response){
        new Thread(() -> {
            if (response.startsWith(Protocol.LIST_USERS)){
                userList = parseListCommand(response,6,User.class);
            } else if (response.startsWith(Protocol.LIST_CATEGORIES)) {
                categoryList = parseListCommand(response,3,Category.class);
            } else if (response.startsWith(Protocol.LIST_INVENTORY)) {
                inventoryList = parseListCommand(response,5,Inventory.class);
            } else if (response.startsWith(Protocol.LIST_ITEMS)) {
                itemList = parseListCommand(response,7,Item.class);
            } else if (response.startsWith(Protocol.LIST_ORDERS)) {
                orderList = parseListCommand(response,6,Order.class);
            } else if (response.startsWith(Protocol.LIST_STOCK_ALERTS)) {
                stockAlertList = parseListCommand(response,5,StockAlert.class);
            } else if (response.startsWith(Protocol.LIST_SUPPLIERS)) {
                supplierList = parseListCommand(response,6,Supplier.class);
            }
        }).start();
    }

    private static <T> T createObject(String[] params, Class<T> clazz) {
        try {
            if (clazz.equals(Category.class)) {
                return clazz.getConstructor(int.class, String.class, String.class)
                        .newInstance(Integer.parseInt(params[0]), params[1], params[2]);

            } else if (clazz.equals(Inventory.class)) {
                return clazz.getConstructor(int.class, String.class, String.class, int.class, String.class)
                        .newInstance(Integer.parseInt(params[0]), params[1], params[2],
                                Integer.parseInt(params[3]), params[4]);

            } else if (clazz.equals(Item.class)) {
                return clazz.getConstructor(int.class,int.class, int.class, int.class, double.class, int.class)
                        .newInstance(Integer.parseInt(params[0]), Integer.parseInt(params[1]),
                                Integer.parseInt(params[2]), Integer.parseInt(params[3]),
                                Double.parseDouble(params[4]),Integer.parseInt(params[6]));

            } else if (clazz.equals(Order.class)) {
                return clazz.getConstructor(int.class, String.class, String.class, OrderStatus.class, String.class, String.class)
                        .newInstance(Integer.parseInt(params[0]), params[1], params[2],
                                OrderStatus.valueOf(params[3]), params[4], params[5]);

            } else if (clazz.equals(StockAlert.class)) {
                return clazz.getConstructor(int.class, int.class, StockAlertStatus.class, String.class, String.class)
                        .newInstance(Integer.parseInt(params[0]), Integer.parseInt(params[1]),
                                StockAlertStatus.valueOf(params[2]), params[3], params[4]);

            } else if (clazz.equals(Supplier.class)) {
                return clazz.getConstructor(int.class, String.class, String.class, String.class, String.class, String.class)
                        .newInstance(Integer.parseInt(params[0]), params[1], params[2], params[3], params[4], params[5]);

            } else if (clazz.equals(User.class)) {
                return clazz.getConstructor(int.class, String.class, String.class, UserRole.class, String.class, String.class)
                        .newInstance(Integer.parseInt(params[0]), params[1], params[2],
                                UserRole.valueOf(params[3]), params[4], params[5]);

            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse object of type " + clazz.getSimpleName(), e);
        }

        return null;
    }



    public static <T> List<T> parseListCommand(String input, int paramCounts, Class<T> clazz) {
        List<T> result = new ArrayList<>();

        input = input.split("~", 2)[1];  // Remove the command prefix
        String[] parts = input.split("~");  // Split into individual entries

        for (String part : parts) {
            String[] params = part.split(Protocol.ARG_SEPARATOR);
            if (params.length != paramCounts) {
                throw new IllegalArgumentException("Incorrect parameter count in section: " + Arrays.toString(params));
            }

            T parsedObject = createObject(params, clazz);
            result.add(parsedObject);
        }

        return result;
    }

    public static void parseAndSendToServer(String header,String[] data) {
        String output = header + Protocol.SEPARATOR;
        if(!header.contains("DELETE")) {
            if (header.equals(Protocol.UPDATE_USER)) {
                output += data[1] + Protocol.SEPARATOR
                        + data[2] + Protocol.SEPARATOR
                        + data[3] + Protocol.SEPARATOR
                        + data[4]  ;
            }else {
                if (header.equals(Protocol.ADD_ITEM)){
                    output += data[0] + Protocol.SEPARATOR
                            + data[1] + Protocol.SEPARATOR
                            + data[2] + Protocol.SEPARATOR
                            + data[3] + Protocol.SEPARATOR
                            + data[5] + Protocol.SEPARATOR;
                }else {
                    for (int i = 0; i < data.length; i++) {
                        if (i != data.length - 1) {
                            output = output + data[i] + Protocol.SEPARATOR;
                        } else {
                            output = output + data[i];
                        }
                    }
                }
            }
        }else {
            output = output + data[0];
        }
        System.out.println(output);
        ServerConnection.sendMessage(output);
    }




}
