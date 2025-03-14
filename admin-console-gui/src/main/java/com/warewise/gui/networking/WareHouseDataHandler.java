package com.warewise.gui.networking;

import com.warewise.gui.controller.MainController;
import com.warewise.gui.controller.MainController.*;
import com.warewise.gui.controller.ServerApplication;

import java.util.ArrayList;
import java.util.List;

public class WareHouseDataHandler {

    public static final String[] CATEGORIES_COLUMNS = {
            "category_id", "category_name", "description"
    };

    public static final String[] INVENTORY_COLUMNS = {
            "inventory_id", "name", "description", "stock_quantity", "last_updated"
    };

    public static final String[] ORDERS_COLUMNS = {
            "order_id", "customer_name", "customer_email", "status", "created_at", "updated_at"
    };

    public static final String[] ORDER_ITEMS_COLUMNS = {
            "order_item_id", "order_id", "quantity", "price", "inventory_id", "category_id"
    };

    public static final String[] STOCK_ALERTS_COLUMNS = {
            "alert_id", "product_id", "threshold", "created_at", "resolved"
    };

    public static final String[] SUPPLIERS_COLUMNS = {
            "supplier_id", "supplier_name", "contact_email", "contact_phone", "address", "created_at"
    };

    public static final String[] USERS_COLUMNS = {
            "user_id", "username", "password_hash", "role", "email", "created_at"
    };

    public static final int USER_COL_NO = USERS_COLUMNS.length;           // 6 columns
    public static final int CATEGORY_COL_NO = CATEGORIES_COLUMNS.length;  // 3 columns
    public static final int INVENTORY_COL_NO = INVENTORY_COLUMNS.length;  // 5 columns
    public static final int ITEMS_COL_NO = ORDER_ITEMS_COLUMNS.length;    // 6 columns
    public static final int ORDERS_COL_NO = ORDERS_COLUMNS.length;        // 6 columns
    public static final int STOCK_ALERT_COL_NO = STOCK_ALERTS_COLUMNS.length; // 5 columns
    public static final int SUPPLIERS_COL_NO = SUPPLIERS_COLUMNS.length;  // 6 columns



    public static void handle(String response){
        if (response.startsWith(Protocol.LIST_USERS)){
            MainController.setParsedUsersList(parseCategories(response,USER_COL_NO));
        } else if (response.startsWith(Protocol.LIST_CATEGORIES)) {
            MainController.setParsedCategoriesList(parseCategories(response,CATEGORY_COL_NO));
        } else if (response.startsWith(Protocol.LIST_INVENTORY)) {
            MainController.setParsedInventoryList(parseCategories(response,INVENTORY_COL_NO));
        } else if (response.startsWith(Protocol.LIST_ITEMS)) {
            MainController.setParsedItemsList(parseCategories(response,ITEMS_COL_NO));
        } else if (response.startsWith(Protocol.LIST_ORDERS)) {
            MainController.setParsedOrdersList(parseCategories(response,ORDERS_COL_NO));
        } else if (response.startsWith(Protocol.LIST_STOCK_ALERTS)) {
            MainController.setParsedAlertsList(parseCategories(response,STOCK_ALERT_COL_NO));
        } else if (response.startsWith(Protocol.LIST_SUPPLIERS)) {
            MainController.setParsedSuppliersList(parseCategories(response,SUPPLIERS_COL_NO));
        }
    }

    public static void initTables(){
        try {
            ServerApplication.getNetworkingObject().sendMessage(Protocol.LIST_USERS+Protocol.SEPARATOR);
            Thread.sleep(1000);
            ServerApplication.getNetworkingObject().sendMessage(Protocol.LIST_INVENTORY+Protocol.SEPARATOR);
            Thread.sleep(1000);
            ServerApplication.getNetworkingObject().sendMessage(Protocol.LIST_ITEMS+Protocol.SEPARATOR);
            Thread.sleep(1000);
            ServerApplication.getNetworkingObject().sendMessage(Protocol.LIST_CATEGORIES+Protocol.SEPARATOR);
            Thread.sleep(1000);
            ServerApplication.getNetworkingObject().sendMessage(Protocol.LIST_ORDERS+Protocol.SEPARATOR);
            Thread.sleep(1000);
            ServerApplication.getNetworkingObject().sendMessage(Protocol.LIST_STOCK_ALERTS+Protocol.SEPARATOR);
            Thread.sleep(1000);
            ServerApplication.getNetworkingObject().sendMessage(Protocol.LIST_SUPPLIERS+Protocol.SEPARATOR);
            Thread.sleep(1000);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String[]> parseCategories(String input, int paramCounts) {
        List<String[]> result = new ArrayList<>();

        input = input.split("~", 2)[1];
        // Split the input string by "~"
        String[] parts = input.split("~");
        // Validate that the number of parts matches the number of param counts
        if (parts.length != paramCounts) {
            throw new IllegalArgumentException("The number of sections does not match the expected param counts.");
        }
        // Iterate over the parts and split them by "," to generate the arrays
        for (int i = 0; i < parts.length; i++) {
            // Split the part by ","
            String[] params = parts[i].split(",");
            // Check if the number of parameters matches the expected count
            if (params.length != paramCounts) {
                throw new IllegalArgumentException("The number of parameters in section " + (i+1) + " does not match the expected count.");
            }
            // Add the params array to the result list
            result.add(params);
        }

        return result;
    }


    public static void parseAndSendToServer(String header,String[] data) {
        String output = header + Protocol.SEPARATOR;
        if(!header.contains("DELETE")) {
            for (int i = 0; i < data.length; i++) {
                if (i != data.length - 1) {
                    output = output + data[i] + Protocol.SEPARATOR;
                } else {
                    output = output + data[i];
                }
            }
        }else {
            output = output + data[0];
        }
        ServerApplication.getNetworkingObject().sendMessage(output);
    }
}
