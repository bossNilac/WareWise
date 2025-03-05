package com.warewise.common.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

public class SequenceManager implements Runnable {

    // Singleton instance
    private static SequenceManager instance;

    // Concurrent map to hold the next available IDs for each table
    private final ConcurrentHashMap<String, Integer> sequences = new ConcurrentHashMap<>();

    private volatile boolean running = true;

    // List of tables with auto-increment IDs
    private final String[] tables = {
            "users", "inventory", "orders", "order_items", "suppliers", "categories", "logs", "stock_alerts"
    };

    // Private constructor for Singleton pattern
    private SequenceManager() {
        initializeSequences();
        startSequenceThread();
    }

    // Get singleton instance
    public static synchronized SequenceManager getInstance() {
        if (instance == null) {
            instance = new SequenceManager();
        }
        return instance;
    }

    // Initialize sequence values from the database
    private void initializeSequences() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            for (String table : tables) {
                int nextId = getNextAvailableId(conn, table);
                sequences.put(table, nextId);
                System.out.println("Initialized sequence for " + table + ": " + nextId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get the next available ID for a given table
    private int getNextAvailableId(Connection conn, String table) throws SQLException {
        String sql = "SELECT IFNULL(MAX(rowid), 0) + 1 AS next_id FROM " + table;
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("next_id");
            }
        }
        return 1; // Default to 1 if the table is empty
    }

    // Start the sequence management thread
    private void startSequenceThread() {
        Thread sequenceThread = new Thread(this);
        sequenceThread.setDaemon(true);
        sequenceThread.start();
    }

    // Runnable method to keep the thread alive
    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(10000); // Polling interval, adjust as needed
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // Get the next available ID and increment the sequence
    public synchronized int getNextId(String table) {
        if (!sequences.containsKey(table)) {
            throw new IllegalArgumentException("No sequence found for table: " + table);
        }
        int nextId = sequences.get(table);
        sequences.put(table, nextId + 1);
        return nextId;
    }

    // Update sequence after a delete operation
    public synchronized void updateSequenceAfterDelete(String table) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            int nextId = getNextAvailableId(conn, table);
            sequences.put(table, nextId -1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Stop the sequence thread
    public void stop() {
        running = false;
    }

    public void updateSequenceIdTable(String tableName)  {
        String sql = "UPDATE sqlite_sequence" + " SET seq = ?" +" WHERE name = ?";

        try (PreparedStatement preparedStatement = DatabaseConnection.getConnection().prepareStatement(sql)) {
            int ID = sequences.get(tableName)  ;
            preparedStatement.setInt(1, ID);
            preparedStatement.setString(2, tableName);
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Updated sequence for table: " + tableName + " to " + ID);
            } else {
                System.out.println("No rows updated. Table may not exist: " + tableName);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public void onDelete(String tableName) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        updateSequenceAfterDelete(tableName);
        updateSequenceIdTable(tableName);
    }

}
