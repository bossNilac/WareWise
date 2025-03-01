package com.warewise.server.database.handler;

import com.warewise.common.model.Inventory;
import com.warewise.common.util.enums.TableName;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InventoryHandler {

    public void addInventory(Inventory inventory) {
        String sql = "INSERT INTO inventory (product_id,product_name, description, category, stock_quantity, price, supplier_id) VALUES (?,?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, inventory.getID());
            stmt.setString(2, inventory.getName());
            // Use inventory's own description instead of the first item's category description
            stmt.setString(3, inventory.getDescription());
            // Derive category from the first item if available; otherwise, set to null
            String categoryName = null;
            if (inventory.getItems() != null && !inventory.getItems().isEmpty()) {
                categoryName = inventory.getItems().get(0).getCategory().getName();
            }
            stmt.setString(4, categoryName);
            stmt.setInt(5, inventory.getQuantity());
            stmt.setDouble(6, inventory.getPrice());
            stmt.setInt(7, inventory.getSupplyID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateInventory(Inventory inventory) {
        String sql = "UPDATE inventory SET product_name = ?, description = ?, category = ?, stock_quantity = ?, price = ?, supplier_id = ? WHERE product_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, inventory.getName());
            stmt.setString(2, inventory.getDescription());
            String categoryName = null;
            if (inventory.getItems() != null && !inventory.getItems().isEmpty()) {
                categoryName = inventory.getItems().get(0).getCategory().getName();
            }
            stmt.setString(3, categoryName);
            stmt.setInt(4, inventory.getQuantity());
            stmt.setDouble(5, inventory.getPrice());
            stmt.setInt(6, inventory.getSupplyID());
            stmt.setInt(7, inventory.getID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteInventory(Inventory inventory) {
        String sql = "DELETE FROM inventory WHERE product_id = ?";
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
                int id = resultSet.getInt("product_id");
                String productName = resultSet.getString("product_name");
                String description = resultSet.getString("description");
                String category = resultSet.getString("category");
                int stockQuantity = resultSet.getInt("stock_quantity");
                double price = resultSet.getDouble("price");
                int supplierID = resultSet.getInt("supplier_id");
                String lastUpdated = resultSet.getString("last_updated");

                // Since we don't have the items list stored in the table,
                // we pass null or create it later if needed.
                Inventory inventory = new Inventory(id, productName, description, null, stockQuantity, price, supplierID, lastUpdated);
                inventoryList.add(inventory);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return inventoryList;
    }
}
