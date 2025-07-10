package warewise.server.common.handler;

import warewise.server.common.model.Item;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemHandler {

    private static ItemHandler instance;

    public static ItemHandler getInstance() {
        if (instance == null) {
            instance = new ItemHandler();
        }
        return instance;
    }

    public void addItem(Item item) {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "INSERT INTO items (item_id, order_id, inventory_id, quantity, price, total, category_id, supplier_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, item.getID());
            stmt.setInt(2, item.getOrderID());
            stmt.setInt(3, item.getInventoryID());
            stmt.setInt(4, item.getQuantity());
            stmt.setDouble(5, item.getPrice());
            stmt.setDouble(6, item.getTotal());
            stmt.setInt(7, item.getCategoryID());
            stmt.setInt(8, item.getSupplierId());
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

    public void updateItem(Item item) {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "UPDATE items SET order_id = ?, inventory_id = ?, quantity = ?, price = ?, total = ?, category_id = ?, supplier_id = ? WHERE item_id = ?";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, item.getOrderID());
            stmt.setInt(2, item.getInventoryID());
            stmt.setInt(3, item.getQuantity());
            stmt.setDouble(4, item.getPrice());
            stmt.setDouble(5, item.getTotal());
            stmt.setInt(6, item.getCategoryID());
            stmt.setInt(7, item.getSupplierId());
            stmt.setInt(8, item.getID());
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

    public Item getItem(int itemId) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Item item = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM items WHERE item_id = ?";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, itemId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                item = new Item(
                        rs.getInt("item_id"),
                        rs.getInt("order_id"),
                        rs.getInt("inventory_id"),
                        rs.getDouble("price"),
                        rs.getInt("quantity"),
                        rs.getDouble("total"),
                        rs.getInt("category_id"),
                        rs.getInt("supplier_id")
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
        return item;
    }

    public List<Item> getAllItems() {
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Item> items = new ArrayList<>();
        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM items";
            stmt = connection.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                Item item = new Item(
                        rs.getInt("item_id"),
                        rs.getInt("order_id"),
                        rs.getInt("inventory_id"),
                        rs.getDouble("price"),
                        rs.getInt("quantity"),
                        rs.getDouble("total"),
                        rs.getInt("category_id"),
                        rs.getInt("supplier_id")
                );
                items.add(item);
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
        return items;
    }
}
