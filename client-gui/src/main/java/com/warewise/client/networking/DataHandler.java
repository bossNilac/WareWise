package com.warewise.client.networking;


import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.warewise.client.util.AdminUtil;
import com.warewise.client.util.model.*;

public class DataHandler {
    public static final int LOW_STOCK_THRESHOLD = 10;
    public static ArrayList<User>    parsedUsersList;
    public static ArrayList<GeneralItem>    parsedItemsList;
    public static ArrayList<WarehouseItem>    parsedWarehouseItemsList;
    public static ArrayList<Category> parsedCategoriesList;
    public static ArrayList<Inventory> parsedInventoryList;
    public static ArrayList<Order>   parsedOrdersList;
    public static ArrayList<Supplier> parsedSuppliersList;
    public static ArrayList<StockAlert> parsedAlertsList;
    public static ArrayList<Warehouse> parsedWarehousesList;
    public static ArrayList<Log> parsedLogsList;

    /**
     * Generic JSON array → List<T> parser.
     */
    public static <T> ArrayList<T> parse(String jsonArray, Class<T> clazz) {
        System.out.println(jsonArray);
        System.out.println(clazz.getName());
        Type type = TypeToken.getParameterized(ArrayList.class, clazz).getType();
        return new Gson().fromJson(new ApiResponse(jsonArray).getData(), type);
    }

    /**
     * Fetches & parses only the specified table.
     */
    public static void initTables(String table) {
        switch (table) {
            case "Users":
                parsedUsersList = parse(ApiHandler.sendApiCall("GET", ApiHandler.USERS, "get_users", null), User.class);
                break;
            case "GeneralItem":
                parsedItemsList = parse(ApiHandler.sendApiCall("GET", ApiHandler.GENERAL_ITEMS, "get_items", null), GeneralItem.class);
                break;
            case "WarehouseItem":
                parsedWarehouseItemsList = parse(ApiHandler.sendApiCall("GET", ApiHandler.ITEMS, "get_items", null), WarehouseItem.class);
                break;
            case "Category":
                parsedCategoriesList = parse(ApiHandler.sendApiCall("GET", ApiHandler.CATEGORIES, "get_categories", null), Category.class);
                break;
            case "Inventory":
                parsedInventoryList = parse(ApiHandler.sendApiCall("GET", ApiHandler.INVENTORIES, "get_inventory", null), Inventory.class);
                break;
            case "Orders":
                parsedOrdersList = parse(ApiHandler.sendApiCall("GET", ApiHandler.ORDERS, "get_orders", null), Order.class);
                break;
            case "Suppliers":
                parsedSuppliersList = parse(ApiHandler.sendApiCall("GET", ApiHandler.SUPPLIERS, "get_suppliers", null), Supplier.class);
                break;
            case "Alerts":
                parsedAlertsList = parse(ApiHandler.sendApiCall("GET", ApiHandler.STOCK_ALERTS, "get_stock_alerts", null), StockAlert.class);
                break;
            case "Warehouse":
                parsedWarehousesList = parse(ApiHandler.sendApiCall("GET", ApiHandler.WAREHOUSES, "get_warehouses", null), Warehouse.class);
                break;
            case "Logs":
                parsedLogsList = parse(ApiHandler.sendApiCall("GET", ApiHandler.LOGS, "get_logs", null), Log.class);
                break;
            default:
                break;
        }
    }

    public static User getCurrentUser() {
        for (User user : parsedUsersList) {
            if (user.getUsername().equals(AdminUtil.sessionUsername)) {
                return user;
            }
        }return null;
    }

    public static Inventory getInventoryById(int id) {
        for (Inventory inventory : parsedInventoryList) {
            if (inventory.getID() == id) {
                return inventory;
            }
        }return null;
    }

    public static LocalDateTime parseServerDateTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String trimmed = value.trim();
        try {
            return LocalDateTime.parse(trimmed);
        } catch (DateTimeParseException ignored) {
        }

        try {
            return OffsetDateTime.parse(trimmed).toLocalDateTime();
        } catch (DateTimeParseException ignored) {
        }

        if (trimmed.length() > 10 && trimmed.charAt(10) == ' ') {
            try {
                return LocalDateTime.parse(trimmed.substring(0, 10) + "T" + trimmed.substring(11));
            } catch (DateTimeParseException ignored) {
            }
        }

        for (DateTimeFormatter formatter : List.of(
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        )) {
            try {
                return LocalDateTime.parse(trimmed, formatter);
            } catch (DateTimeParseException ignored) {
            }
        }

        return null;
    }

    public static List<String> getRecentActionsForOtherUsers(String username, int i) {
        ArrayList<String> logs = new ArrayList<>();
        for(Log log :parsedLogsList){
            String action = log.getAction();
            if(action == null || action.equals("GET")){
                continue;
            }
            DateTimeFormatter printer = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

            LocalDateTime dt = parseServerDateTime(log.getCreatedAt());
            if (dt == null) {
                continue;
            }
            String pretty = dt.format(printer);
            if(!username.equals(log.getUsername()) && i!=0){
                --i;
                String logUsername = (log.getUsername() == null || log.getUsername().isBlank())
                        ? "System"
                        : log.getUsername();
                logs.add(String.format("%s %s at %s", logUsername, log.getDescription(), pretty));
            }
        }
        return logs;
    }
}
