package warewise.server.common.handler;

import warewise.server.common.model.WarehouseItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WarehouseItemHandler {

    private static WarehouseItemHandler instance;

    public static WarehouseItemHandler getInstance() {
        if (instance == null) {
            instance = new WarehouseItemHandler();
        }
        return instance;
    }

    public void addItem(WarehouseItem warehouseItem) {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "INSERT INTO items (order_id, inventory_id, quantity, total,general_item_id,expire_date,sold) VALUES (?, ?, ?, ?,?,?,?)";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, warehouseItem.getOrderID());
            stmt.setInt(2, warehouseItem.getInventoryID());
            stmt.setInt(3, warehouseItem.getQuantity());
            stmt.setDouble(4, warehouseItem.getTotal());
            stmt.setDouble(5, warehouseItem.getGeneralItemId());
            stmt.setString(6, warehouseItem.getExpireDate());
            stmt.setBoolean(7, warehouseItem.isSold());
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

    public void updateItem(WarehouseItem warehouseItem) {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "UPDATE items SET order_id = ?, inventory_id = ?, quantity = ? ,total = ?, general_item_id= ?,expire_date = ?, sold = ? WHERE item_id = ?";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, warehouseItem.getOrderID());
            stmt.setInt(2, warehouseItem.getInventoryID());
            stmt.setInt(3, warehouseItem.getQuantity());
            stmt.setDouble(4, warehouseItem.getTotal());
            stmt.setDouble(5, warehouseItem.getGeneralItemId());
            stmt.setString(6, warehouseItem.getExpireDate());
            stmt.setBoolean(7, warehouseItem.isSold());
            stmt.setInt(8, warehouseItem.getID());
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

    public void deleteItem(int itemId) {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "DELETE FROM items WHERE item_id = ?";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, itemId);
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

    public WarehouseItem getItem(int itemId) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        WarehouseItem warehouseItem = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM items WHERE item_id = ?";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, itemId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                warehouseItem = new WarehouseItem(
                        rs.getInt("item_id"),
                        rs.getInt("order_id"),
                        rs.getInt("inventory_id"),
                        rs.getInt("quantity"),
                        rs.getDouble("total"),
                        rs.getInt("general_item_id"),
                        rs.getString("expire_date"),
                        rs.getBoolean("sold")
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
        return warehouseItem;
    }

    public List<WarehouseItem> getAllItems() {
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<WarehouseItem> warehouseItems = new ArrayList<>();
        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM items";
            stmt = connection.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                WarehouseItem warehouseItem = new WarehouseItem(
                        rs.getInt("item_id"),
                        rs.getInt("order_id"),
                        rs.getInt("inventory_id"),
                        rs.getInt("quantity"),
                        rs.getDouble("total"),
                        rs.getInt("general_item_id"),
                        rs.getString("expire_date"),
                        rs.getBoolean("sold")
                );
                warehouseItems.add(warehouseItem);
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
        return warehouseItems;
    }
}
