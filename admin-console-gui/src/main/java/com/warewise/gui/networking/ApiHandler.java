package com.warewise.gui.networking;

import com.google.gson.GsonBuilder;
import com.warewise.gui.util.UtilityCommands;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

public class ApiHandler {

    static String URL= "http://localhost:8080";

    public static String TOKEN = null;

    public static String USERS="users";
    public static String CATEGORIES="categories";
    public static String WAREHOUSES="warehouses";
    public static String ITEMS="general_items";
    public static String INVENTORIES="inventory";
    public static String STOCK_ALERTS="stock_alerts";
    public static String ORDERS="orders";
    public static String SUPPLIERS="suppliers";
    public static String LOGS="logs";


    public static String sendApiCall(String method, String subpath, String command, String string_body) {
        String url = URL + "/api/" + subpath + (command == null || command.isEmpty() ? "" : "/" + command);
        if (TOKEN == null) {
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            Request.Builder requestBuilder = new Request.Builder()
                    .url(url);
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
            Request.Builder requestBuilder = new Request.Builder()
                    .url(url)
                    .header("Authorization", "Bearer " +TOKEN);

            if (method.equalsIgnoreCase("POST") || method.equalsIgnoreCase("PATCH")) {
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, string_body != null ? string_body : "");
                requestBuilder.method(method, body);
            } else if (method.equalsIgnoreCase("GET")) {
                requestBuilder.get();
            } else if (method.equalsIgnoreCase("DELETE")) {
                requestBuilder.delete();
            } else {
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
            case "LIST_LOGS":
                usersResponse = ApiHandler.sendApiCall("GET", ApiHandler.LOGS, "get_logs", null);
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
                usersResponse = ApiHandler.sendApiCall("DELETE", ApiHandler.USERS, "delete_user/" + id, null);
                break;
            case "DELETE_ITEM":
                usersResponse = ApiHandler.sendApiCall("DELETE", ApiHandler.ITEMS, "delete_item/" + id, null);
                break;
            case "DELETE_CATEGORY":
                usersResponse = ApiHandler.sendApiCall("DELETE", ApiHandler.CATEGORIES, "delete_category/" + id, null);
                break;
            case "DELETE_INVENTORY":
                usersResponse = ApiHandler.sendApiCall("DELETE", ApiHandler.INVENTORIES, "delete_inventory/" + id, null);
                break;
            case "DELETE_ORDER":
                usersResponse = ApiHandler.sendApiCall("DELETE", ApiHandler.ORDERS, "delete_order/" + id, null);
                break;
            case "DELETE_SUPPLIER":
                usersResponse = ApiHandler.sendApiCall("DELETE", ApiHandler.SUPPLIERS, "delete_supplier/" + id, null);
                break;
            case "DELETE_STOCK_ALERT":
                usersResponse = ApiHandler.sendApiCall("DELETE", ApiHandler.STOCK_ALERTS, "delete_stock_alert/" + id, null);
                break;
            case "DELETE_WAREHOUSE":
                usersResponse = ApiHandler.sendApiCall("DELETE", ApiHandler.WAREHOUSES, "delete_warehouse/" + id, null);
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

    /**
     * Builds a JSON payload for a DELETE request using a primitive id.
     *
     * @param id            the identifier value to include
     * @param idFieldName   the JSON field name to use for the ID (e.g. "userId")
     * @return              a pretty-printed JSON string containing only the ID
     */
    public static String buildDeleteParams(int id, String idFieldName) {
        Map<String, Object> jsonMap = new LinkedHashMap<>();

        if (idFieldName != null && !idFieldName.trim().isEmpty()) {
            jsonMap.put(idFieldName, id);
        }

        return new GsonBuilder()
                .setPrettyPrinting()
                .create()
                .toJson(jsonMap);
    }
}
