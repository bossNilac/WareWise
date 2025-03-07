package com.warewise.common.handler;

import com.warewise.common.model.Inventory;
import com.warewise.common.util.SequenceManager;
import com.warewise.common.util.enums.TableName;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InventoryHandler {

    public void addInventory(Inventory inventory) {
        String sql = "INSERT INTO inventory (inventory_id,name, stock_quantity, description , last_updated  ) VALUES (? , ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, inventory.getID());
            stmt.setString(2, inventory.getName());
            // Use inventory's own description instead of the first item's category description
            stmt.setString(4, inventory.getDescription());
            // Derive category from the first item if available; otherwise, set to null
            stmt.setInt(3, inventory.getQuantity());
            stmt.setString(5, inventory.getLastUpdated());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateInventory(Inventory inventory) {
        String sql = "UPDATE inventory SET name = ?, stock_quantity = ?, description = ?, last_updated = ? WHERE inventory_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, inventory.getName());
            stmt.setInt(2, inventory.getQuantity());
            stmt.setString(3, inventory.getDescription());
            stmt.setString(4, inventory.getLastUpdated());
            stmt.setInt(5, inventory.getID());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void deleteInventory(Inventory inventory) {
        String sql = "DELETE FROM inventory WHERE inventory_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, inventory.getID());
            stmt.executeUpdate();
            SequenceManager.getInstance().onDelete(TableName.INVENTORY.getTableName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Inventory> loadInventory() {
        String sql = "SELECT * FROM inventory";
        List<Inventory> inventoryList = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet resultSet = stmt.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("inventory_id");
                String productName = resultSet.getString("name");
                String description = resultSet.getString("description");
                int stockQuantity = resultSet.getInt("stock_quantity");
                String lastUpdated = resultSet.getString("last_updated");

                // Since we don't have the items list stored in the table,
                // we pass null or create it later if needed.
                Inventory inventory = new Inventory(id, productName,description,  stockQuantity, lastUpdated);
                inventoryList.add(inventory);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return inventoryList;
    }
}
