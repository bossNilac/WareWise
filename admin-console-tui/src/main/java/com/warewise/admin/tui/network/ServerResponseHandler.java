package com.warewise.admin.tui.network;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

import static com.warewise.admin.tui.ui.UiConstants.*;
import static com.warewise.admin.tui.commands.UtilityCommands.simulateTyping;

public class ServerResponseHandler extends Thread {

    private static final Lock lock = new ReentrantLock();
    private static final Condition responseReceived = lock.newCondition();
    private static final Queue<String> responseQueue = new LinkedList<>(); // Queue to store multiple responses
    private volatile boolean running = true; // Control flag for stopping the thread
    private volatile boolean sentCommand = false;

    /**
     * Constructor - Starts the thread when an instance is created.
     */
    public ServerResponseHandler() {
        this.start(); // Start the thread automatically when instantiated
    }

    /**
     * Thread execution logic. Waits for new responses indefinitely and prints them as they arrive.
     */
    @Override
    public void run() {
        String[] spinner = {"|", "/", "-", "\\"};
        int i = 0;

        while (running) { // Keep listening for responses
            lock.lock();
            try {
                // Wait while the queue is empty (i.e., no new responses)
                while (responseQueue.isEmpty() && running && sentCommand) {
                    System.out.print("\r" + ANSI_YELLOW + "Waiting for server response " + spinner[i % spinner.length] + ANSI_RESET);
                    Thread.sleep(150);
                    i++;
                    responseReceived.awaitNanos(150_000_000); // Wait for a signal with timeout to update spinner
                }

                if (!running) break; // Exit if the thread is stopped

                // Display the response using simulateTyping effect
                if(sentCommand) {
                    // Retrieve and remove the first response in the queue
                    String serverResponse = responseQueue.poll();

                    // Clear the spinner line
                    System.out.print("\r" + " ".repeat(50) + "\r");
                    simulateTyping(ANSI_GREEN + "Server: " + serverResponse + ANSI_RESET, 50);
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Preserve interrupt status
                break;
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * Adds a new response to the queue and signals waiting threads.
     */
    public void setServerResponse(String response) {
        lock.lock();
        try {
            responseQueue.add(response); // Add response to queue
            responseReceived.signal(); // Notify waiting thread
        } finally {
            sentCommand = false;
            lock.unlock();
        }
    }

    /**
     * Simulate sending a command by setting a "waiting" state.
     * This can be used before sending a request.
     */
    public void sendCommand() {
        lock.lock();
        sentCommand = true;
        try {
            responseQueue.clear(); // Clear previous responses (simulate new waiting state)
        } finally {
            lock.unlock();
        }
    }

    /**
     * Stops the thread gracefully.
     */
    public void stopHandler() {
        running = false;
        lock.lock();
        try {
            responseReceived.signalAll(); // Wake up any waiting threads
        } finally {
            lock.unlock();
        }
    }
}
