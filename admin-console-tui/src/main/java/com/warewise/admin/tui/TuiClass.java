package com.warewise.admin.tui;

import com.google.gson.GsonBuilder;
import com.warewise.admin.tui.commands.*;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import static com.warewise.admin.tui.commands.AdminUtil.loggedIn;
import static com.warewise.admin.tui.ui.UiConstants.*;
import static com.warewise.admin.tui.commands.UtilityCommands.*;

public class TuiClass {

    static Scanner scanner = new Scanner(System.in);
    private boolean DbActionFlag = false;
    private boolean running = true;

    public static final String FOLDER = System.getProperty("user.home") + "/WareWise";
    public static final String CREDENTIALS_FILE =FOLDER + "/user_credentials.json";


    public void doLogin(){
        String loginResponse = ApiHandler.sendApiCall("POST","auth","login",AdminUtil.getLoginCred());
        try {
            animateProgressBar("Login",5,200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        ApiResponse response = new ApiResponse(loginResponse);
        if(!response.getSuccess()){
            UtilityCommands.displayNotificationPanel(3,"Login Failed,wrong login credentials");
            loggedIn = false;
        }else {
            ApiHandler.TOKEN = response.getData();
            loggedIn = true;
        }
    }

    public boolean isServerDown(){
        String loginResponse;
        try {
            loginResponse = ApiHandler.sendApiCall("GET", "status", "", null);
            System.out.println( loginResponse );
        }catch (Exception e){
            return  true;
        }
        return loginResponse == null;
    }

    public void serverInit() {
        System.out.println(isServerDown());
        if(isServerDown()){
            UtilityCommands.displayNotificationPanel(3,"Login Failed,server down");
            running = false;
        }else {
            doLogin();
            clearScreen();
            printHeader();
            running = true;
            while (!loggedIn) {
                askForCred();
                doLogin();
            }
            this.runDbActionMenu();
        }
    }

    public static void main(String[] args) {

        File folder = new File(TuiClass.FOLDER);
        File f = new File(TuiClass.CREDENTIALS_FILE);
        try {
            folder.mkdir();
            f.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        new TuiClass().runMainMenu();
    }

    public void runMainMenu() {
        serverInit();
        displayAppHeader();
        while (running) {
            switch (askForInput()) {
                case "3":
                    this.DbActionFlag = true;
                    runDbActionMenu();
                    break;
                case "4":
                    String usersResponse = ApiHandler.sendApiCall("GET",ApiHandler.USERS,"get_users",null);
                    ApiResponse apiResponse = new ApiResponse(usersResponse);
                    UtilityCommands.displayNotificationPanel(1,apiResponse.getData());
                    break;
                case "5":
                    String logsResponse = ApiHandler.sendApiCall("GET",ApiHandler.LOGS,"get_logs",null);
                    ApiResponse logsApiResponse = new ApiResponse(logsResponse);
                    UtilityCommands.displayNotificationPanel(1,logsApiResponse.getData());
                    break;
                case "6":
                    if (showModalDialog("Exit")) {
                        exit();
                    } else {
                        break;
                    }
                case "0":
                    UtilityCommands.askForCred();
                case "7":
                    displayAppHeader();
                    break;
                default:
                    System.out.println("\n Invalid option. Please try again.");
                }
            }
    }

    public  void runDbActionMenu() {
        while (DbActionFlag) {
            printDbActionMenu();
            String command;

            switch (askForInput()) {
                case "1":
                    handleAddOrUpdate(true);  // Add
                    break;
                case "2":
                    handleAddOrUpdate(false); // Update
                    break;
                case "3":
                    command = handleList();
                    ApiHandler.sendListCall(command);
                    break;
                case "4":
                    command = handleDelete();
                    System.out.print("Enter ID to delete: ");
                    int id = Integer.parseInt(scanner.nextLine());
                    ApiHandler.sendDeleteCall(command, id);
                    break;
                case "5":
                    displayAppHeader();
                    DbActionFlag = false;
                    break;
                default:
                    System.out.println("Invalid option.");
                    break;
            }
        }
    }


    // Method for Add and Update (Combined)
    private static void handleAddOrUpdate(boolean isAdd) {
        String method = "POST";
        String command_prefix = "add_";
        printDbHandlerMenu();
        String command = askForInput();

        String id = "";

        if (!isAdd) {  // If updating, ask for ID
            System.out.print("Enter ID to update: ");
            id = scanner.nextLine();
            method = "PATCH";
            command_prefix = "update_";
        }

        String apiCall;
        ApiResponse apiResponse;
        String body;

         switch (command) {
             case "1" :
                     if(isAdd){
                             body = buildParams(true,null ,null,
                                     "username", "password", "email","role","warehouseIds");
                     }
                     else{
                             body = buildParams(false,id ,"userId",
                                     "username", "password", "email","role","warehouseIds");
                     }
                     apiCall = ApiHandler.sendApiCall(method, ApiHandler.USERS, command_prefix+"user", body);
                     apiResponse = new ApiResponse(apiCall);
                     System.out.println(apiResponse.getMessage());
                     break;

             case "2" :
                     if(isAdd){
                         body = buildParams(true,null ,null,
                                 "name", "setQuantity", "barcode","categoryId","supplierId","price","expires");
                     }
                    else{
                         body = buildParams(false,id ,"itemId",
                                 "name", "setQuantity", "barcode","categoryId","supplierId","price","expires");
                     }
                     apiCall = ApiHandler.sendApiCall(method, ApiHandler.ITEMS, command_prefix+"item", body);
                     apiResponse = new ApiResponse(apiCall);
                     System.out.println(apiResponse.getMessage());
                     break;
                 case "3" :
                     if(isAdd){
                         body = buildParams(true,null ,null,
                                 "name", "description");
                     }
                     else{
                         body = buildParams(false,id ,"categoryId",
                                 "name", "description");
                     }
                     apiCall = ApiHandler.sendApiCall(method, ApiHandler.CATEGORIES, command_prefix+"category", body);
                     apiResponse = new ApiResponse(apiCall);
                     System.out.println(apiResponse.getMessage());
                     break;
                 case "4" :
                     if(isAdd){
                         body = buildParams(true,null ,null,
                                 "name", "stockQuantity","description","lastUpdated","warehouseId");
                     }
                     else{
                         body = buildParams(false,id ,"inventoryId",
                                 "name", "stockQuantity","description","lastUpdated","warehouseId");
                     }
                     apiCall = ApiHandler.sendApiCall(method, ApiHandler.INVENTORIES, command_prefix+"inventory", body);
                     apiResponse = new ApiResponse(apiCall);
                     System.out.println(apiResponse.getMessage());
                     break;
                 case "5" :
                     if(isAdd){
                         body = buildParams(true,null ,null,
                                 "general_item_id", "quantity","status","createdAt","updatedAt","userId");
                     }
                     else{
                         body = buildParams(false,id ,"orderId",
                                 "general_item_id", "quantity","status","createdAt","userId");
                     }
                     apiCall = ApiHandler.sendApiCall(method, ApiHandler.ORDERS, command_prefix+"order", body);
                     apiResponse = new ApiResponse(apiCall);
                     System.out.println(apiResponse.getMessage());
                     break;
                case "6" :
                     if(isAdd){
                         body = buildParams(true,null ,null,
                                 "supplierName", "contactEmail","contactPhone","address","createdAt");
                     }
                     else{
                         body = buildParams(false,id ,"supplierId",
                                 "supplierName", "contactEmail","contactPhone","address","createdAt");
                     }
                     apiCall = ApiHandler.sendApiCall(method, ApiHandler.SUPPLIERS, command_prefix+"supplier", body);
                     apiResponse = new ApiResponse(apiCall);
                     System.out.println(apiResponse.getMessage());
                     break;
                case "7" :
                     if(isAdd){
                         body = buildParams(true,null ,null,
                                 "productId", "createdAt","resolved");
                     }
                     else{
                         body = buildParams(false,id ,"stockAlertId",
                                 "productId", "createdAt","resolved");
                     }
                     apiCall = ApiHandler.sendApiCall(method, ApiHandler.STOCK_ALERTS, command_prefix+"stock_alert", body);
                     apiResponse = new ApiResponse(apiCall);
                     System.out.println(apiResponse.getMessage());
                     break;
                case "8" :
                     if(isAdd){
                         body = buildParams(true,null ,null,
                                 "name", "address");
                     }
                     else{
                         body = buildParams(false,id ,"warehouseId",
                                 "name", "address");
                     }
                     apiCall = ApiHandler.sendApiCall(method, ApiHandler.WAREHOUSES, command_prefix+"warehouse", body);
                     apiResponse = new ApiResponse(apiCall);
                     System.out.println(apiResponse.getMessage());
                     break;
             case "10" :
                 if (isAdd) {
                     body = buildParams(
                             true,
                             null, null, "orderId", "inventoryId",
                             "quantity", "general_item_id", "expireDate", "sold"
                     );
                 } else {
                     body = buildParams(
                             false,
                             id, "itemId",
                             "orderId", "inventoryId", "quantity",
                              "general_item_id", "expireDate", "sold"
                     );
                 }

                 apiCall = ApiHandler.sendApiCall(method, ApiHandler.W_ITEMS, command_prefix+"item", body);
                 apiResponse = new ApiResponse(apiCall);
                 System.out.println(apiResponse.getMessage());
                 break;

                case "9" :
                    displayAppHeader();
                    break;
                default :
                    System.out.println("Invalid category.");
                    break;
        };
    }

    // Method for List Operations
    private static String handleList() {
        System.out.println(topBorder);
        System.out.println("\n | Select a category to list:");
        printDbHandlerMenu();
        System.out.println(bottomBorder);
        System.out.print("Choose a category: ");
        String categoryChoice = scanner.nextLine();

        return switch (categoryChoice) {
            case "1" -> "LIST_USERS";
            case "2" -> "LIST_ITEMS";
            case "3" -> "LIST_CATEGORIES";
            case "4" -> "LIST_INVENTORY";
            case "5" -> "LIST_ORDERS";
            case "6" -> "LIST_SUPPLIERS";
            case "7" -> "LIST_STOCK_ALERTS";
            case "8" -> "LIST_WAREHOUSES";
            case "10" -> "LIST_WAREHOUSE_ITEMS";
            case "9" -> "";
            default -> {
                System.out.println("Invalid category.");
                yield "";
            }
        };
    }

    // Method for Delete Operations
    private static String handleDelete() {
        System.out.println("\nSelect a category to delete from:");
        printDbHandlerMenu();
        String command = askForInput();

        return switch (command) {
            case "1" -> "DELETE_USER"  ;
            case "2" -> "DELETE_ITEM"  ;
            case "3" -> "DELETE_CATEGORY" ;
            case "4" -> "DELETE_INVENTORY";
            case "5" -> "DELETE_ORDER" ;
            case "6" -> "DELETE_SUPPLIER"  ;
            case "7" -> "DELETE_STOCK_ALERT" ;
            case "8" -> "DELETE_WAREHOUSE" ;
            case "10" -> "DELETE_WAREHOUSE_ITEM" ;
            case "9" -> "";
            default -> {
                System.out.println("Invalid category.");
                yield "";
            }
        };
    }

    private static void exit() {
        System.exit(0);
    }

    private static Object parseValue(String input) {
        if (input.contains(",")) {
            return java.util.Arrays.stream(input.split(","))
                    .map(String::trim)
                    .filter(value -> !value.isEmpty())
                    .map(Integer::parseInt)
                    .toList();
        }
        if (input.equalsIgnoreCase("true") || input.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(input);
        }
        try {
            if (input.contains(".")) {
                return Double.parseDouble(input);
            }
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return input; // fallback to string
        }
    }

    public static String buildParams(boolean isAdd, Object id, String idFieldName, String... paramNames) {
        Map<String, Object> jsonMap = new LinkedHashMap<>();

        if (!isAdd && id != null && idFieldName != null && !idFieldName.isEmpty()) {
            jsonMap.put(idFieldName, id);
        }

        for (String paramName : paramNames) {
            if (paramName == null) continue;
            System.out.print(paramName + ": ");
            String input = scanner.nextLine();
            if (input == null || input.trim().isEmpty()) {
                continue;
            }
            Object value = input.isEmpty() ? "" : parseValue(input);
            jsonMap.put(paramName, value);
        }

        return new GsonBuilder().setPrettyPrinting().create().toJson(jsonMap);
    }

}
