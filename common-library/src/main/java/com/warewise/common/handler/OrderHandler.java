package com.warewise.common.handler;

import com.warewise.common.model.Order;
import com.warewise.common.util.SequenceManager;
import com.warewise.common.util.enums.OrderStatus;
import com.warewise.common.util.enums.TableName;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderHandler {

    public void addOrder(Order order) {
        String sql = "INSERT INTO orders (order_id,customer_name, customer_email, status) VALUES (?,?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, order.getID());
            stmt.setString(2, order.getCustomerName());
            stmt.setString(3, order.getCustomerEmail());
            stmt.setString(4, order.getStatus().name());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateOrder(Order order) {
        String sql = "UPDATE orders SET customer_name = ?, customer_email = ?, status = ?, updated_at = ? WHERE order_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, order.getCustomerName());
            stmt.setString(2, order.getCustomerEmail());
            stmt.setString(3, order.getStatus().name());
            stmt.setString(4, order.getUpdatedAt());
            stmt.setInt(5, order.getID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteOrder(Order order) {
        String sql = "DELETE FROM orders WHERE order_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, order.getID());
            stmt.executeUpdate();
            SequenceManager.getInstance().onDelete(TableName.ORDERS.getTableName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Order> loadOrders() {
        String sql = "SELECT * FROM orders";
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet resultSet = stmt.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("order_id");
                String customerName = resultSet.getString("customer_name");
                String customerEmail = resultSet.getString("customer_email");
                String statusStr = resultSet.getString("status");
                OrderStatus status = OrderStatus.valueOf(statusStr);
                String createdAt = resultSet.getString("created_at");
                String updatedAt = resultSet.getString("updated_at");

                Order order = new Order(id, customerName, customerEmail, status, createdAt, updatedAt);
                // Optionally, load order items separately if needed.
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
}
