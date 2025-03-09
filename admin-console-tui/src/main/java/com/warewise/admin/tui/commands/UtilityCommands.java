package com.warewise.admin.tui.commands;

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
        System.out.println("| 2. Category Management      |");
        System.out.println("| 3. Item Management          |");
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


    public static void displayNotificationPanel() {
        // This method prints notifications at the bottom of the terminal.
        int termRows = 25; // assuming a 25-row terminal for this dummy example
        moveCursor(termRows - 4, 1);
        System.out.println(ANSI_WHITE + ANSI_BOLD + "== Notifications ==" + ANSI_RESET);
        moveCursor(termRows - 3, 1);
        System.out.println(ANSI_GREEN + "[INFO] Service started successfully." + ANSI_RESET);
        moveCursor(termRows - 2, 1);
        System.out.println(ANSI_YELLOW + "[WARN] High memory usage detected." + ANSI_RESET);
        moveCursor(termRows - 1, 1);
        System.out.println(ANSI_RED + ANSI_BLINK + "[ALERT] Unauthorized access attempt!" + ANSI_RESET);
    }


    public static String  askForInput (){
        System.out.print(ANSI_WHITE + "Enter your command: " + ANSI_RESET);
        return scanner.nextLine();
    }

    public static void doHandlingAnimation(String input) throws InterruptedException {
        // Simulate processing by animating the display of the entered command.
        simulateTyping(ANSI_CYAN + "Processing your input: " + input + ANSI_RESET, 50);
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

}
