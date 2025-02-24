package com.warewise.common.util.database.handler;

import com.warewise.common.model.Logs;
import com.warewise.common.util.database.SequenceManager;
import com.warewise.common.util.enums.TableName;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LogsHandler {

    public void addLog(Logs log) {
        String sql = "INSERT INTO logs (log_id,user_id, action, description) VALUES (?,?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, log.getID());
            stmt.setInt(2, log.getUserID());
            stmt.setString(3, log.getAction());
            stmt.setString(4, log.getDescription());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateLog(Logs log) {
        String sql = "UPDATE logs SET user_id = ?, action = ?, description = ? WHERE log_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, log.getUserID());
            stmt.setString(2, log.getAction());
            stmt.setString(3, log.getDescription());
            stmt.setInt(4, log.getID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteLog(Logs log) {
        String sql = "DELETE FROM logs WHERE log_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, log.getID());
            stmt.executeUpdate();
            SequenceManager.getInstance().onDelete(TableName.LOGS.getTableName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Logs> loadLogs() {
        String sql = "SELECT * FROM logs";
        List<Logs> logsList = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet resultSet = stmt.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("log_id");
                int userId = resultSet.getInt("user_id");
                String action = resultSet.getString("action");
                String description = resultSet.getString("description");
                String createdAt = resultSet.getString("created_at");

                Logs log = new Logs(id, userId, action, description, createdAt);
                logsList.add(log);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logsList;
    }
}
