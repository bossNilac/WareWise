package com.warewise.server.database.handler;




import com.warewise.common.model.Item;
import com.warewise.common.util.SequenceManager;
import com.warewise.common.util.enums.TableName;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemHandler {

    /**
     * Inserts a new order item into the order_items table.
     * The 'total' field is computed automatically by SQLite.
     *
     * @param item The item to insert.
     */
    public void addItem(Item item) {
        String sql = "INSERT INTO order_items (order_item_id,order_id, product_id, quantity, price) VALUES (?,?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, item.getID());
            stmt.setInt(2, item.getOrderID());
            stmt.setInt(3, item.getProductID());
            stmt.setInt(4, item.getQuantity());
            stmt.setDouble(5, item.getPrice());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates an existing order item.
     *
     * @param item The item with updated values.
     */
    public void updateItem(Item item) {
        String sql = "UPDATE order_items SET order_id = ?, product_id = ?, quantity = ?, price = ? WHERE order_item_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, item.getOrderID());
            stmt.setInt(2, item.getProductID());
            stmt.setInt(3, item.getQuantity());
            stmt.setDouble(4, item.getPrice());
            stmt.setInt(5, item.getID());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes an order item from the order_items table.
     *
     * @param item The item to delete.
     */
    public void deleteItem(Item item) {
        String sql = "DELETE FROM order_items WHERE order_item_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, item.getID());
            stmt.executeUpdate();
            SequenceManager.getInstance().onDelete(TableName.INVENTORY.getTableName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads order items.
     *
     * @param orderId If provided, only load items for that order; otherwise, load all items.
     * @return A list of order items.
     */
    public List<Item> loadItems(Integer orderId) {
        List<Item> items = new ArrayList<>();
        String sql;
        if (orderId != null) {
            sql = "SELECT * FROM order_items WHERE order_id = ?";
        } else {
            sql = "SELECT * FROM order_items";
        }
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (orderId != null) {
                stmt.setInt(1, orderId);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("order_item_id");
                    int orderID = rs.getInt("order_id");
                    int productID = rs.getInt("product_id");
                    int quantity = rs.getInt("quantity");
                    double price = rs.getDouble("price");
                    double total = rs.getDouble("total"); // Retrieved computed total

                    // As the SQL table does not include a category column, set category to null.
                    Item item = new Item(id, orderID, productID, quantity, price, total, null);
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
}
