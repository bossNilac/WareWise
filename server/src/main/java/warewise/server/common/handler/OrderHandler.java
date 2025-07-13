package warewise.server.common.handler;

import warewise.server.common.model.Order;
import warewise.server.common.util.enums.OrderStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderHandler {

    private static OrderHandler instance;

    public static OrderHandler getInstance() {
        if (instance == null) {
            instance = new OrderHandler();
        }
        return instance;
    }

    public void addOrder(Order order) {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "INSERT INTO orders (customer_name, customer_email, status, created_at, updated_at, user_id) VALUES (?, ?, ?, ?, ?, ?)";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, order.getCustomerName());
            stmt.setString(2, order.getCustomerEmail());
            stmt.setString(3, order.getStatus().toString());
            stmt.setString(4, order.getCreatedAt());
            stmt.setString(5, order.getUpdatedAt());
            stmt.setInt(6, order.getUserId());
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

    public void updateOrder(Order order) {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "UPDATE orders SET customer_name = ?, customer_email = ?, status = ?, created_at = ?, updated_at = ?, user_id = ? WHERE order_id = ?";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, order.getCustomerName());
            stmt.setString(2, order.getCustomerEmail());
            stmt.setString(3, order.getStatus().toString());
            stmt.setString(4, order.getCreatedAt());
            stmt.setString(5, order.getUpdatedAt());
            stmt.setInt(6, order.getUserId());
            stmt.setInt(7, order.getID());
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

    public void deleteOrder(int orderId) {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "DELETE FROM orders WHERE order_id = ?";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, orderId);
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

    public Order getOrder(int orderId) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Order order = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM orders WHERE order_id = ?";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, orderId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                order = new Order(
                        rs.getInt("order_id"),
                        rs.getString("customer_name"),
                        rs.getString("customer_email"),
                        OrderStatus.fromLabel(rs.getString("status")),
                        rs.getString("created_at"),
                        rs.getString("updated_at"),
                        rs.getInt("user_id")
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
        return order;
    }

    public List<Order> getAllOrders() {
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Order> orders = new ArrayList<>();
        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM orders";
            stmt = connection.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                Order order = new Order(
                        rs.getInt("order_id"),
                        rs.getString("customer_name"),
                        rs.getString("customer_email"),
                        OrderStatus.fromLabel(rs.getString("status")),
                        rs.getString("created_at"),
                        rs.getString("updated_at"),
                        rs.getInt("user_id")
                );
                orders.add(order);
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
        return orders;
    }
}
