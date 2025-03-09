package com.warewise.admin.tui;

import com.warewise.admin.tui.commands.*;
import com.warewise.admin.tui.network.NetworkingClass;
import com.warewise.admin.tui.network.ServerResponseHandler;
import com.warewise.admin.tui.ui.Dashboard;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

import static com.warewise.admin.tui.ui.UiConstants.*;
import static com.warewise.admin.tui.commands.UtilityCommands.*;
import static com.warewise.common.util.protocol.Protocol.*;

public class TuiClass {

    static Dashboard dashboard;
    static ServerResponseHandler serverResponseHandler;
    static NetworkingClass networkingObject;
    static Socket socket;
    static Scanner scanner = new Scanner(System.in);
    private static final int port = 12345;
    private boolean DbActionFlag = false;
    private boolean running = true;

    public void initialise() {
        try {
            dashboard = new Dashboard();
            animateProgressBar("Dashboard", 10, 100); // 30 steps with a 50ms delay each
            serverResponseHandler = new ServerResponseHandler();
            animateProgressBar("ServerResponseHandler", 10, 100); // 30 steps with a 50ms delay each
            socket = new Socket(InetAddress.getLocalHost(), port);
            networkingObject = new NetworkingClass(socket, serverResponseHandler);
            animateProgressBar("Networking Module", 10, 100); // 30 steps with a 50ms delay each
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        clearScreen();
        printHeader();
        dashboard.start();
        networkingObject.listenToServer();
        running = true;
        this.runMainMenu();
    }

    public static void main(String[] args) throws InterruptedException {
        displayNotificationPanel();

    }

    public void runMainMenu() {
        while (running) {
            clearScreen();
            printHeader();
            printServerControlMenu();
            switch (askForInput()) {
                case "1":
                    AdminUtil.startServer(networkingObject);
                    break;
                case "2":
                    AdminUtil.closeServer(networkingObject);
                    break;
                case "3":
                    this.DbActionFlag = true;
                    runDbActionMenu();
                    break;
                case "4":
                    AdminUtil.listUsers(networkingObject);
                    break;
                case "5":
                    System.out.print("Enter ID to kick: ");
                    String userID = scanner.nextLine();
                    AdminUtil.kickUser(networkingObject,userID);
                    break;
                case "6":
                    if (showModalDialog("Exit")) {
                        exit();
                    } else {
                        break;
                    }
                default:
                    System.out.println("\n Invalid option. Please try again.");
            }
        }
    }

    public  void runDbActionMenu() {
        while (DbActionFlag) {
            printDbActionMenu();
            String command = "";

            switch (askForInput()) {
                case "1":
                    command = handleAddOrUpdate(true);  // Add
                    break;
                case "2":
                    command = handleAddOrUpdate(false); // Update
                    break;
                case "3":
                    command = handleList();
                    break;
                case "4":
                    command = handleDelete();
                    break;
                case "5":
                    DbActionFlag = false;
                default:
                    System.out.println("Invalid option.");
            }

            if (!command.isBlank()) {
                networkingObject.sendMessage(command);
            }else {
                DbActionFlag = false;
            }
        }
    }


    // Method for Add and Update (Combined)
    private static String handleAddOrUpdate(boolean isAdd) {
        printDbHandlerMenu();
        String command = askForInput();

        String actionPrefix = isAdd ? "ADD_" : "UPDATE_";
        String id = "";

        if (!isAdd) {  // If updating, ask for ID
            System.out.print("Enter ID to update: ");
            id = scanner.nextLine();
        }

        return switch (command) {
            case "1" -> actionPrefix + "USER" + buildParams(isAdd, id, "Username", "Role", "Email", "Password");
            case "2" ->
                    actionPrefix + "ITEM" + buildParams(isAdd, id, "Product Name", "Description", "Quantity", "Price", "Supplier ID");
            case "3" -> actionPrefix + "CATEGORY" + buildParams(isAdd, id, "Category Name", "Description");
            case "4" ->
                    actionPrefix + "INVENTORY" + buildParams(isAdd, id, "Inventory Name", "Description", "Quantity", "Last Updated");
            case "5" -> isAdd
                    ? "CREATE_ORDER" + buildParams(true, "", "Customer Name", "Item ID", "Quantity")
                    : "UPDATE_ORDER" + buildParams(false, id, "Status");
            case "6" -> actionPrefix + "SUPPLIER" + buildParams(isAdd, id, "Name", "Email", "Phone", "Address");
            case "7" -> isAdd
                    ? "STOCK_ALERT" + buildParams(true, "", "Product ID", "Threshold")
                    : "UPDATE_STOCK_ALERT" + buildParams(false, id, "Product ID", "Threshold", "Created At", "Resolved");
            case "8" -> "";
            default -> {
                System.out.println("Invalid category.");
                yield "";
            }
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
            case "1" -> LIST_USERS;
            case "2" -> LIST_ITEMS;
            case "3" -> LIST_CATEGORIES;
            case "4" -> LIST_INVENTORY;
            case "5" -> LIST_ORDERS;
            case "6" -> LIST_SUPPLIERS;
            case "7" -> LIST_STOCK_ALERTS;
            case "8" -> "";
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

        System.out.print("Enter ID to delete: ");
        String id = scanner.nextLine();

        return switch (command) {
            case "1" -> DELETE_USER + SEPARATOR + id;
            case "2" -> DELETE_ITEM + SEPARATOR + id;
            case "3" -> DELETE_CATEGORY + SEPARATOR + id;
            case "4" -> DELETE_INVENTORY + SEPARATOR + id;
            case "5" -> DELETE_ORDER + SEPARATOR + id;
            case "6" -> DELETE_SUPPLIER + SEPARATOR + id;
            case "7" -> DELETE_STOCK_ALERT + SEPARATOR + id;
            case "8" -> "";
            default -> {
                System.out.println("Invalid category.");
                yield "";
            }
        };
    }

    private static void exit() {
    }

    // Helper method for handling parameters (empty fields become ~~)
    private static String buildParams(boolean isAdd, String id, String... params) {
        StringBuilder commandBuilder = new StringBuilder("~");
        if (!isAdd) {
            commandBuilder.append(id).append("~");
        }

        for (String param : params) {
            System.out.print(param + ": ");
            String input = scanner.nextLine();
            commandBuilder.append(input.isEmpty() ? "" : input).append("~");
        }

        String command = commandBuilder.toString();
        return command.endsWith("~") ? command.substring(0, command.length() - 1) : command; // Remove trailing '~'
    }
}