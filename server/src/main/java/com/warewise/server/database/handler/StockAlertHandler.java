package com.warewise.server.database.handler;

import com.warewise.common.model.StockAlert;
import com.warewise.common.util.enums.StockAlertStatus;
import com.warewise.common.util.enums.TableName;
import com.warewise.common.util.SequenceManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StockAlertHandler {

    public void addStockAlert(StockAlert alert) {
        String sql = "INSERT INTO stock_alerts (alert_id,product_id, threshold, created_at, resolved) VALUES (?,?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, alert.getID());
            stmt.setInt(2, alert.getProductID());
            // Store the enum name for threshold (or its ordinal, if preferred)
            stmt.setString(3, alert.getThreshold().name());
            stmt.setString(4, alert.getCreatedAt());
            stmt.setString(5, alert.getResolved());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateStockAlert(StockAlert alert) {
        String sql = "UPDATE stock_alerts SET product_id = ?, threshold = ?, created_at = ?, resolved = ? WHERE alert_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, alert.getProductID());
            stmt.setString(2, alert.getThreshold().name());
            stmt.setString(3, alert.getCreatedAt());
            stmt.setString(4, alert.getResolved());
            stmt.setInt(5, alert.getID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteStockAlert(StockAlert alert) {
        String sql = "DELETE FROM stock_alerts WHERE alert_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, alert.getID());
            stmt.executeUpdate();
            SequenceManager.getInstance().onDelete(TableName.STOCK_ALERTS.getTableName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<StockAlert> loadStockAlerts() {
        String sql = "SELECT * FROM stock_alerts";
        List<StockAlert> alerts = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet resultSet = stmt.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("alert_id");
                int productID = resultSet.getInt("product_id");
                // Convert stored threshold string back to enum
                String thresholdStr = resultSet.getString("threshold");
                StockAlertStatus threshold = StockAlertStatus.valueOf(thresholdStr);
                String createdAt = resultSet.getString("created_at");
                String resolved = resultSet.getString("resolved");

                StockAlert alert = new StockAlert(id, productID, threshold, createdAt, resolved);
                alerts.add(alert);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alerts;
    }
}
