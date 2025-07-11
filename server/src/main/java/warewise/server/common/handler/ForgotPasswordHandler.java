package warewise.server.common.handler;

import warewise.server.common.model.ForgotPasswordToken;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ForgotPasswordHandler {

    private static ForgotPasswordHandler instance;

    public static ForgotPasswordHandler getInstance() {
        if (instance == null) {
            instance = new ForgotPasswordHandler();
        }
        return instance;
    }

    public void addToken(ForgotPasswordToken token) {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "INSERT INTO forgot_password_table (id, user_id, token, expiration_date) VALUES (?, ?, ?, ?)";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, token.getId());
            stmt.setInt(2, token.getUserId());
            stmt.setString(3, token.getToken());
            stmt.setString(4, token.getExpirationDate());
            stmt.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try { if (connection != null) connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public ForgotPasswordToken getTokenById(int id) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ForgotPasswordToken token = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM forgot_password_table WHERE id = ?";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                token = new ForgotPasswordToken(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("token"),
                    rs.getString("expiration_date")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); if (stmt != null) stmt.close(); if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return token;
    }

    public ForgotPasswordToken getTokenByToken(String  param) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ForgotPasswordToken token = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM forgot_password_table WHERE token = ?";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, param);
            rs = stmt.executeQuery();
            if (rs.next()) {
                token = new ForgotPasswordToken(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("token"),
                        rs.getString("expiration_date")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); if (stmt != null) stmt.close(); if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return token;
    }

    public List<ForgotPasswordToken> getTokensByUserId(int userId) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<ForgotPasswordToken> tokens = new ArrayList<>();
        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM forgot_password_table WHERE user_id = ?";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                tokens.add(new ForgotPasswordToken(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("token"),
                    rs.getString("expiration_date")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); if (stmt != null) stmt.close(); if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return tokens;
    }

    public void deleteToken(int id) {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "DELETE FROM forgot_password_table WHERE id = ?";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);
            stmt.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try { if (connection != null) connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}
