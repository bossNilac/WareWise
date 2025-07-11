package warewise.server.common.handler;

import warewise.server.common.model.StockAlert;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StockAlertHandler {

    private static StockAlertHandler instance;

    public static StockAlertHandler getInstance() {
        if (instance == null) {
            instance = new StockAlertHandler();
        }
        return instance;
    }

    public void addStockAlert(StockAlert stockAlert) {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "INSERT INTO stock_alerts (stock_alert_id, product_id, created_at, resolved) VALUES (?, ?, ?, ?, ?)";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, stockAlert.getID());
            stmt.setInt(2, stockAlert.getProductID());
            stmt.setString(3, stockAlert.getCreatedAt());
            stmt.setBoolean(4, stockAlert.getResolved());
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

    public void updateStockAlert(StockAlert stockAlert) {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "UPDATE stock_alerts SET product_id = ?, created_at = ?, resolved = ? WHERE stock_alert_id = ?";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, stockAlert.getProductID());
            stmt.setString(2, stockAlert.getCreatedAt());
            stmt.setBoolean(3, stockAlert.getResolved());
            stmt.setInt(4, stockAlert.getID());
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

    public void deleteStockAlert(int stockAlertId) {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "DELETE FROM stock_alerts WHERE stock_alert_id = ?";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, stockAlertId);
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

    public StockAlert getStockAlert(int stockAlertId) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        StockAlert stockAlert = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM stock_alerts WHERE stock_alert_id = ?";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, stockAlertId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                stockAlert = new StockAlert(
                        rs.getInt("stock_alert_id"),
                        rs.getInt("product_id"),
                        rs.getString("created_at"),
                        rs.getBoolean("resolved")
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
        return stockAlert;
    }

    public List<StockAlert> getAllStockAlerts() {
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<StockAlert> stockAlerts = new ArrayList<>();
        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM stock_alerts";
            stmt = connection.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                StockAlert stockAlert = new StockAlert(
                        rs.getInt("stock_alert_id"),
                        rs.getInt("product_id"),
                        rs.getString("created_at"),
                        rs.getBoolean("resolved")
                );
                stockAlerts.add(stockAlert);
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
        return stockAlerts;
    }
}
