package com.warewise.client.networking;

import com.google.gson.GsonBuilder;
import com.warewise.client.util.UtilityCommands;
import okhttp3.*;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ApiHandler {

    static String URL= "http://localhost:8080";

    public static String TOKEN = null;

    public static String POST = "POST";
    public static String PATCH = "PATCH";



    public static String USERS="users";
    public static String CATEGORIES="categories";
    public static String WAREHOUSES="warehouses";
    public static String ITEMS="items";
    public static String GENERAL_ITEMS="general_items";
    public static String INVENTORIES="inventory";
    public static String STOCK_ALERTS="stock_alerts";
    public static String ORDERS="orders";
    public static String SUPPLIERS="suppliers";
    public static final String LOGS = "logs";


    public static String sendApiCall(String method, String subpath, String command, String string_body) {
        if (TOKEN == null) {
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            Request.Builder requestBuilder = new Request.Builder()
                    .url(buildUrl(subpath, command));
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
                    .url(buildUrl(subpath, command))
                    .header("Authorization", "Bearer " +TOKEN);

            if (method.equalsIgnoreCase("POST") || method.equalsIgnoreCase("PATCH")) {
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, string_body != null ? string_body : "");
                requestBuilder.method(method, body);
            } else if (method.equalsIgnoreCase("DELETE")) {
                requestBuilder.delete();
            } else if (method.equalsIgnoreCase("GET")) {
                requestBuilder.get();
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

    private static String buildUrl(String subpath, String command) {
        StringBuilder builder = new StringBuilder(URL).append("/api/").append(subpath);
        if (command != null && !command.isBlank()) {
            builder.append("/").append(command);
        }
        return builder.toString();
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
            case "DELETE_GENERAL_ITEM":
                usersResponse = ApiHandler.sendApiCall("DELETE", ApiHandler.GENERAL_ITEMS, "delete_item/" + id, null);
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
