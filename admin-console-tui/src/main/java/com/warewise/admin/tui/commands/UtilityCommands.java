package com.warewise.admin.tui.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import static com.warewise.admin.tui.ui.UiConstants.*;

public class UtilityCommands {

    static Scanner scanner = new Scanner(System.in);

    public static void printHeader(){
        System.out.println(ANSI_CYAN + ANSI_BOLD + uiHeader + ANSI_RESET);
    }

    public static void printServerControlMenu() {
        System.out.println(ANSI_WHITE + ANSI_BOLD);
        System.out.println(topBorder);
        System.out.println("| 1. Start Server  |");
        System.out.println("| 2. Close Server  |");
        System.out.println("| 3. DB Action     |");
        System.out.println("| 4. List Users    |");
        System.out.println("| 5. Kick User     |");
        System.out.println("| 6. Exit          |");
        System.out.println("| 7. Clear screen  |");
        System.out.println("| 0. Setup profile |");
        System.out.println(bottomBorder + ANSI_RESET);
    }

    public static void printDbActionMenu(){
        System.out.println(ANSI_WHITE + ANSI_BOLD);
        System.out.println(topBorder);
        System.out.println("| 1. Add    | ");
        System.out.println("| 2. Update | ");
        System.out.println("| 3. List   | ");
        System.out.println("| 4. Delete | ");
        System.out.println("| 5. Exit   | ");
        System.out.println(bottomBorder + ANSI_RESET);
    }

    public static void printDbHandlerMenu() {
        clearScreen();
        System.out.println(ANSI_WHITE + ANSI_BOLD);
        System.out.println(topBorder);
        System.out.println("|         MAIN MENU           |");
        System.out.println(middleBorder);
        System.out.println("| 1. User Management          |");
        System.out.println("| 2. Item Management          |");
        System.out.println("| 3. Category Management      |");
        System.out.println("| 4. Inventory Management     |");
        System.out.println("| 5. Order Management         |");
        System.out.println("| 6. Supplier Management      |");
        System.out.println("| 7. Stock Alert Management   |");
        System.out.println("| 8. Exit                     |");
        System.out.println(bottomBorder + ANSI_RESET);
    }

    public static void animateProgressBar(String element,int totalSteps, int delayMillis) throws InterruptedException {
        System.out.print(ANSI_GREEN + "Initializing " + element + ": [");
        for (int i = 0; i < totalSteps; i++) {
            System.out.print("=");
            Thread.sleep(delayMillis);
        }
        System.out.println("] " + ANSI_RESET);
    }

    public static void clearScreen() {
        System.out.print(CLEAR_SCREEN);
        System.out.flush();
    }

    public static void moveCursor(int row, int col) {
        System.out.printf("\033[%d;%dH", row, col);
    }

    public static boolean showModalDialog(String message) {
        scanner = new Scanner(System.in);
        // Draw a simple modal using box-drawing characters
        System.out.println();
        System.out.println(topBorder);
        System.out.println("| " + message);
        System.out.println("| Confirm? (Y/N): ");
        System.out.println(bottomBorder);
        String input = scanner.nextLine().trim().toUpperCase();
        return input.equals("Y");
    }

    public static void simulateTyping(String message, int delayMillis) throws InterruptedException {
        for (char ch : message.toCharArray()) {
            System.out.print(ch);
            Thread.sleep(delayMillis);
        }
        System.out.println();
    }


    public static void displayNotificationPanel(int type,String message) {
        // This method prints notifications at the bottom of the terminal.
        int termRows = 25; // assuming a 25-row terminal for this dummy example
        moveCursor(termRows - 4, 1);
        switch (type){
            case 1:
                System.out.println(ANSI_GREEN + "[INFO]"+ message +   ANSI_RESET);
                break;
            case 2:
                System.out.println(ANSI_YELLOW + "[WARN]"+ message  + ANSI_RESET);
                break;
            case 3:
                System.out.println(ANSI_RED + ANSI_BLINK + "[ALERT]" + message + ANSI_RESET);
                break;
            default:break;
        }
    }

    public static void displayAppHeader(){
        clearScreen();
        printHeader();
        printServerControlMenu();
    }

    public static void askForCred() {
        System.out.print(ANSI_WHITE + "Enter your username: " + ANSI_RESET);
        String username = scanner.nextLine();
        System.out.print(ANSI_WHITE + "Enter your password: " + ANSI_RESET);
        String password = scanner.nextLine();
        AdminUtil.saveLoginCred(username,password);
    }

    public static String  askForInput (){
        try {
            Thread.sleep(750);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.print(ANSI_WHITE + "Enter your command: " + ANSI_RESET);
        return scanner.nextLine();
    }

    public static void doHandlingAnimation(String input) throws InterruptedException {
        // Simulate processing by animating the display of the entered command.
        simulateTyping(ANSI_CYAN + "Processing your input: " + input + ANSI_RESET, 20);
    }

    public static void animateGraph(double[] data) throws InterruptedException {
        // Dummy data for the bar chart (fictional values)
        System.out.println(ANSI_CYAN + ANSI_BOLD + "\n== Performance Graph ==" + ANSI_RESET);
        for (double value : data) {
            // Each bar will be built with a sequence of '#' characters.
            StringBuilder bar = new StringBuilder();
            for (int i = 0; i < value/10; i++) {
                bar.append("#");
                System.out.print("\r" + bar);
                Thread.sleep(50);
            }
            System.out.println(" (" + value + ")");
        }
    }

    public static boolean isFileEmpty(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return reader.readLine() == null;  // Returns true if the file is empty
        } catch (IOException e) {
            return true; // Treat it as empty if there's an error reading
        }

    }
}
