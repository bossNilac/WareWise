// WareHouseDataHandler.java
package com.warewise.gui.networking;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.warewise.gui.util.model.*;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class WareHouseDataHandler {
    public static ArrayList<User>    parsedUsersList;
    public static ArrayList<Item>    parsedItemsList;
    public static ArrayList<Category> parsedCategoriesList;
    public static ArrayList<Inventory> parsedInventoryList;
    public static ArrayList<Order>   parsedOrdersList;
    public static ArrayList<Supplier> parsedSuppliersList;
    public static ArrayList<StockAlert> parsedAlertsList;
    public static ArrayList<Warehouse> parsedWarehousesList;
    // Logs commented out for now
     public static ArrayList<Log> parsedLogsList;

    /**
     * Generic JSON array → List<T> parser.
     */
    public static <T> ArrayList<T> parse(String jsonArray, Class<T> clazz) {
        System.out.println(jsonArray);
        Type type = TypeToken.getParameterized(ArrayList.class, clazz).getType();
        return new Gson().fromJson(new ApiResponse(jsonArray).getData(), type);
    }

    /**
     * Fetches & parses all tables from the API.
     */
    public static void initTables() {
        System.out.println("WareHouseDataHandler.initTables()");
        parsedUsersList      = parse(ApiHandler.sendApiCall("GET", ApiHandler.USERS,       "get_users",       null), User.class);
        parsedItemsList      = parse(ApiHandler.sendApiCall("GET", ApiHandler.ITEMS,       "get_items",       null), Item.class);
        parsedCategoriesList = parse(ApiHandler.sendApiCall("GET", ApiHandler.CATEGORIES,  "get_categories",  null), Category.class);
        parsedInventoryList  = parse(ApiHandler.sendApiCall("GET", ApiHandler.INVENTORIES, "get_inventory",   null), Inventory.class);
        parsedOrdersList     = parse(ApiHandler.sendApiCall("GET", ApiHandler.ORDERS,     "get_orders",     null), Order.class);
        parsedSuppliersList  = parse(ApiHandler.sendApiCall("GET", ApiHandler.SUPPLIERS,  "get_suppliers",  null), Supplier.class);
        parsedAlertsList     = parse(ApiHandler.sendApiCall("GET", ApiHandler.STOCK_ALERTS, "get_stock_alerts", null), StockAlert.class);
        parsedWarehousesList = parse(ApiHandler.sendApiCall("GET", ApiHandler.WAREHOUSES,  "get_warehouses",  null), Warehouse.class);
        parsedLogsList    = parse(ApiHandler.sendApiCall("GET", ApiHandler.LOGS,       "get_logs",       null), Log.class);
    }

    /**
     * Fetches & parses only the specified table.
     */
    public static void initTables(String table) {
        switch (table) {
            case "Users":
                parsedUsersList = parse(ApiHandler.sendApiCall("GET", ApiHandler.USERS, "get_users", null), User.class);
                break;
            case "Item":
                parsedItemsList = parse(ApiHandler.sendApiCall("GET", ApiHandler.ITEMS, "get_items", null), Item.class);
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
             case "Logs":
                 parsedLogsList = parse(ApiHandler.sendApiCall("GET", ApiHandler.LOGS, "get_logs", null), Log.class);
                 break;
            case "Warehouse":
                parsedWarehousesList = parse(ApiHandler.sendApiCall("GET", ApiHandler.WAREHOUSES, "get_warehouses", null), Warehouse.class);
                break;
            default:
                break;
        }
    }
}
