package com.warewise.admin.tui;

import com.warewise.admin.tui.commands.UtilityCommands;
import okhttp3.*;

import java.io.IOException;
import java.util.Objects;

public class ApiHandler {

    static String URL= "http://localhost:8080";

    public static String TOKEN = null;

    public static String USERS="users";
    public static String CATEGORIES="categories";
    public static String WAREHOUSES="warehouses";
    public static String ITEMS="general_items";
    public static String W_ITEMS="items";
    public static String INVENTORIES="inventory";
    public static String STOCK_ALERTS="stock_alerts";
    public static String ORDERS="orders";
    public static String SUPPLIERS="suppliers";


    public static String sendApiCall(String method, String subpath, String command, String string_body) {
        if (TOKEN == null) {
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            Request.Builder requestBuilder = new Request.Builder()
                    .url(URL + "/api/" + subpath + "/" + command);
            if (method.equalsIgnoreCase("POST")) {
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, string_body != null ? string_body : "");
                requestBuilder.method(method, body);
            } else if (method.equalsIgnoreCase("GET")) {
                requestBuilder.get();
            }
                Response response;
                try {
                    response = client.newCall(requestBuilder.build()).execute();
                    return response.body().string();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        } else {
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            if (method.equals("DELETE")) command = command + "/" + string_body;
            Request.Builder requestBuilder = new Request.Builder()
                    .url(URL + "/api/" + subpath + "/" + command)
                    .header("Authorization", "Bearer " +TOKEN);

            if (method.equalsIgnoreCase("POST") || method.equalsIgnoreCase("PATCH") ) {
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, string_body != null ? string_body : "");
                requestBuilder.method(method, body);
            } else if (method.equalsIgnoreCase("GET")) {
                requestBuilder.get();
            }
            else if (method.equalsIgnoreCase("DELETE")) {
                requestBuilder.delete();
            }else {
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
            }

            try {
                Response response = client.newCall(requestBuilder.build()).execute();
                if (response.body() != null) {
                    return response.body().string(); //
                } else {
                    return null;
                }
            } catch (IOException e) {
                throw new RuntimeException("API call failed", e);
            }
        }
    }

    public static void sendListCall(String value) {
        String usersResponse;
        ApiResponse apiResponse;

        switch (value) {
            case "LIST_USERS":
                usersResponse = ApiHandler.sendApiCall("GET", ApiHandler.USERS, "get_users", null);
                apiResponse = new ApiResponse(usersResponse);
                break;
            case "LIST_ITEMS":
                usersResponse = ApiHandler.sendApiCall("GET", ApiHandler.ITEMS, "get_items", null);
                apiResponse = new ApiResponse(usersResponse);
                break;
            case "LIST_CATEGORIES":
                usersResponse = ApiHandler.sendApiCall("GET", ApiHandler.CATEGORIES, "get_categories", null);
                apiResponse = new ApiResponse(usersResponse);
                break;
            case "LIST_INVENTORY":
                usersResponse = ApiHandler.sendApiCall("GET", ApiHandler.INVENTORIES, "get_inventory", null);
                apiResponse = new ApiResponse(usersResponse);
                break;
            case "LIST_ORDERS":
                usersResponse = ApiHandler.sendApiCall("GET", ApiHandler.ORDERS, "get_orders", null);
                apiResponse = new ApiResponse(usersResponse);
                break;
            case "LIST_SUPPLIERS":
                usersResponse = ApiHandler.sendApiCall("GET", ApiHandler.SUPPLIERS, "get_suppliers", null);
                apiResponse = new ApiResponse(usersResponse);
                break;
            case "LIST_STOCK_ALERTS":
                usersResponse = ApiHandler.sendApiCall("GET", ApiHandler.STOCK_ALERTS, "get_stock_alerts", null);
                apiResponse = new ApiResponse(usersResponse);
                break;
            case "LIST_WAREHOUSES":
                usersResponse = ApiHandler.sendApiCall("GET", ApiHandler.WAREHOUSES, "get_warehouses", null);
                apiResponse = new ApiResponse(usersResponse);
                break;
            case "LIST_WAREHOUSE_ITEMS":
                usersResponse = ApiHandler.sendApiCall("GET", ApiHandler.W_ITEMS, "get_warehouses", null);
                apiResponse = new ApiResponse(usersResponse);
                break;
            case "":
                    return;
            default:
                System.out.println("Invalid value.");
                return;
        }

        if (apiResponse.getSuccess()) {
            UtilityCommands.displayNotificationPanel(1, apiResponse.getData());
        } else {
            UtilityCommands.displayNotificationPanel(3, "Unsuccessful operation");
        }
    }

    public static void sendDeleteCall(String value, int id) {
        String usersResponse;
        ApiResponse apiResponse;

        switch (value) {
            case "DELETE_USER":
                usersResponse = ApiHandler.sendApiCall("DELETE", ApiHandler.USERS, value.toLowerCase(), String.valueOf(id));
                break;
            case "DELETE_ITEM":
                usersResponse = ApiHandler.sendApiCall("DELETE", ApiHandler.ITEMS, value.toLowerCase(), String.valueOf(id));
                break;
            case "DELETE_CATEGORY":
                usersResponse = ApiHandler.sendApiCall("DELETE", ApiHandler.CATEGORIES, value.toLowerCase(), String.valueOf(id));
                break;
            case "DELETE_INVENTORY":
                usersResponse = ApiHandler.sendApiCall("DELETE", ApiHandler.INVENTORIES, value.toLowerCase(), String.valueOf(id));
                break;
            case "DELETE_ORDER":
                usersResponse = ApiHandler.sendApiCall("DELETE", ApiHandler.ORDERS, value.toLowerCase(), String.valueOf(id));
                break;
            case "DELETE_SUPPLIER":
                usersResponse = ApiHandler.sendApiCall("DELETE", ApiHandler.SUPPLIERS, value.toLowerCase(), String.valueOf(id));
                break;
            case "DELETE_STOCK_ALERT":
                usersResponse = ApiHandler.sendApiCall("DELETE", ApiHandler.STOCK_ALERTS, value.toLowerCase(), String.valueOf(id));
                break;
            case "DELETE_WAREHOUSE":
                usersResponse = ApiHandler.sendApiCall("DELETE", ApiHandler.WAREHOUSES, value.toLowerCase(), String.valueOf(id));
                break;
            case "DELETE_WAREHOUSE_ITEM":
                usersResponse = ApiHandler.sendApiCall("DELETE", ApiHandler.W_ITEMS, value.toLowerCase(), String.valueOf(id));
                break;
            case "":
                return;
            default:
                System.out.println("Invalid value: " + value);
                return;
        }

        apiResponse = new ApiResponse(usersResponse);
        if (apiResponse.getSuccess()) {
            UtilityCommands.displayNotificationPanel(1, apiResponse.getMessage());
        } else {
            UtilityCommands.displayNotificationPanel(3, "Unsuccessful operation");
        }
    }
}
