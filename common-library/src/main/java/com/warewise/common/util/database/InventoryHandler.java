package com.warewise.common.util.database;

import com.warewise.common.model.Inventory;
import com.warewise.common.model.Item;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InventoryHandler {

    public void addInventory(Inventory inventory) {
        String sql = "INSERT INTO inventory (product_name, description, category, stock_quantity, price, supplier_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, inventory.getName()); // Assuming the first item's name is used
            stmt.setString(2, inventory.getItems().get(0).getCategory().getDescription());
            stmt.setString(3, inventory.getItems().get(0).getCategory().getName());
            stmt.setInt(4, inventory.getQuantity());
            stmt.setDouble(5, inventory.getPrice());
            stmt.setInt(6, inventory.getSupplyID());
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
            stmt.setString(3, inventory.getItems().get(0).getCategory().getName());
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

                Inventory inventory = new Inventory(id, description,productName, null,stockQuantity,price,supplierID,lastUpdated);
                inventoryList.add(inventory);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return inventoryList;
    }
}
