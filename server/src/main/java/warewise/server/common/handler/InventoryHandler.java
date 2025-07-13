package warewise.server.common.handler;

import warewise.server.common.model.Inventory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryHandler {

    private static InventoryHandler instance;

    public static InventoryHandler getInstance() {
        if (instance == null) {
            instance = new InventoryHandler();
        }
        return instance;
    }

    public void addInventory(Inventory inventory) {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "INSERT INTO inventory (name, stock_quantity, description, last_updated, warehouse_id) VALUES (?, ?, ?, ?, ?)";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, inventory.getName());
            stmt.setInt(2, inventory.getQuantity());
            stmt.setString(3, inventory.getDescription());
            stmt.setString(4, inventory.getLastUpdated());
            stmt.setInt(5, inventory.getWarehouseId());
            stmt.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try {
                if (connection != null) connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateInventory(Inventory inventory) {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "UPDATE inventory SET name = ?, stock_quantity = ?, description = ?, last_updated = ?, warehouse_id = ? WHERE inventory_id = ?";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, inventory.getName());
            stmt.setInt(2, inventory.getQuantity());
            stmt.setString(3, inventory.getDescription());
            stmt.setString(4, inventory.getLastUpdated());
            stmt.setInt(5, inventory.getWarehouseId());
            stmt.setInt(6, inventory.getID());
            stmt.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try {
                if (connection != null) connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteInventory(int inventoryId) {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "DELETE FROM inventory WHERE inventory_id = ?";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, inventoryId);
            stmt.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try {
                if (connection != null) connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Inventory getInventory(int inventoryId) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Inventory inventory = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM inventory WHERE inventory_id = ?";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, inventoryId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                inventory = new Inventory(
                        rs.getInt("inventory_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("stock_quantity"),
                        rs.getString("last_updated"),
                        rs.getInt("warehouse_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return inventory;
    }

    public List<Inventory> getAllInventories() {
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Inventory> inventories = new ArrayList<>();
        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM inventory";
            stmt = connection.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                Inventory inventory = new Inventory(
                        rs.getInt("inventory_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("stock_quantity"),
                        rs.getString("last_updated"),
                        rs.getInt("warehouse_id")
                );
                inventories.add(inventory);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return inventories;
    }
}
