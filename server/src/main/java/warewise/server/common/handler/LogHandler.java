package warewise.server.common.handler;

import warewise.server.common.model.Log;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LogHandler {

    private static LogHandler instance;

    public static LogHandler getInstance() {
        if (instance == null) {
            instance = new LogHandler();
        }
        return instance;
    }

    public void addLog(Log log) {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "INSERT INTO logs (user_id, action, description, created_at) VALUES (?, ?, ?, ?)";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, log.getUserID());
            stmt.setString(2, log.getAction());
            stmt.setString(3, log.getDescription());
            stmt.setString(4, log.getCreatedAt());
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

    public void updateLog(Log log) {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "UPDATE logs SET user_id = ?, action = ?, description = ?, created_at = ? WHERE log_id = ?";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, log.getUserID());
            stmt.setString(2, log.getAction());
            stmt.setString(3, log.getDescription());
            stmt.setString(4, log.getCreatedAt());
            stmt.setInt(5, log.getID());
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

    public void deleteLog(int logId) {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "DELETE FROM logs WHERE log_id = ?";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, logId);
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

    public Log getLog(int logId) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Log log = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM logs WHERE log_id = ?";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, logId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                log = new Log(
                        rs.getInt("log_id"),
                        rs.getInt("user_id"),
                        rs.getString("action"),
                        rs.getString("description"),
                        rs.getString("created_at")
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
        return log;
    }

    public List<Log> getAllLogs() {
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Log> logs = new ArrayList<>();
        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM logs";
            stmt = connection.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                Log log = new Log(
                        rs.getInt("log_id"),
                        rs.getInt("user_id"),
                        rs.getString("action"),
                        rs.getString("description"),
                        rs.getString("created_at")
                );
                logs.add(log);
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
        return logs;
    }
}
