package com.warewise.admin.tui.ui;

import static com.warewise.admin.tui.ui.UiConstants.*;
import static com.warewise.admin.tui.commands.UtilityCommands.moveCursor;

public class Dashboard {

    private Thread dashboardThread;
    private volatile boolean running = false;

    // Metrics that will be updated dynamically
    private String cpuUsage = "CPU: N/A";
    private String memUsage = "MEM: N/A";
    private String activeConnections = "Conns: N/A";

    /**
     * Starts the dashboard thread.
     */
    public void start() {
        running = true;
        dashboardThread = new Thread(() -> {
            int row = 2; // starting row for the dashboard
            while (running) {
                // Save the current cursor position
                System.out.print(SAVE_CURSOR);

                // Move cursor to the dashboard area (right side, column 50)
                moveCursor(row, 75);
                System.out.print(ANSI_WHITE + ANSI_BOLD + "== Dashboard ==" + ANSI_RESET);
                moveCursor(row + 1, 75);
                System.out.print(ANSI_GREEN + cpuUsage + "         " + ANSI_RESET);
                moveCursor(row + 2, 75);
                System.out.print(ANSI_GREEN + memUsage + "         " + ANSI_RESET);
                moveCursor(row + 3, 75);
                System.out.print(ANSI_GREEN + activeConnections + "         " + ANSI_RESET);

                // Restore the previous cursor position so that other TUI elements are undisturbed
                System.out.print(RESTORE_CURSOR);

                // Sleep for 2-3 seconds before updating
                try {
                    Thread.sleep(2000 + (int)(Math.random() * 1000)); // Random delay between 2-3 seconds
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        dashboardThread.setDaemon(true); // does not block the application from exiting
        dashboardThread.start();
    }

    /**
     * Updates the dashboard metrics with new values.
     * This method can be called from anywhere in your application.
     */
    public synchronized void updateMetrics(String cpu, String mem, String conns) {
        this.cpuUsage = "CPU: " + cpu + "%";
        this.memUsage = "MEM: " + mem + "MB";
        this.activeConnections = "Conns: " + conns;
    }

    /**
     * Stops the dashboard thread.
     */
    public void stop() {
        running = false;
        if (dashboardThread != null) {
            dashboardThread.interrupt();
        }
    }
}
