package warewise.server.common.handler;

import warewise.server.common.model.User;
import warewise.server.common.util.enums.UserRole;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserHandler {
    private static UserHandler instance;

    public static UserHandler getInstance() {
        if (instance == null) {
            instance = new UserHandler();
        }
        return instance;
    }

    public void addUser(User user) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet keys = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "INSERT INTO users (username, password_hash, email, created_at, role) VALUES (?, ?, ?, ?, ?)";
            stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getCreatedAt());
            stmt.setString(5, user.getRole().toString());
            stmt.executeUpdate();

            keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                int userId = keys.getInt(1);
                user.setID(userId);
                replaceUserWarehouses(connection, userId, user.getWarehouseIds());
            }
            connection.commit();
        } catch (SQLException e) {
            rollback(connection);
            e.printStackTrace();
        } finally {
            close(keys, stmt, connection);
        }
    }

    public void updateUser(User user) {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "UPDATE users SET username = ?, password_hash = ?, email = ?, created_at = ?, role = ? WHERE user_id = ?";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getCreatedAt());
            stmt.setString(5, user.getRole().toString());
            stmt.setInt(6, user.getID());
            stmt.executeUpdate();
            replaceUserWarehouses(connection, user.getID(), user.getWarehouseIds());
            connection.commit();
        } catch (SQLException e) {
            rollback(connection);
            e.printStackTrace();
        } finally {
            close(null, stmt, connection);
        }
    }

    public void setUserWarehouses(int userId, List<Integer> warehouseIds) {
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            replaceUserWarehouses(connection, userId, warehouseIds);
            connection.commit();
        } catch (SQLException e) {
            rollback(connection);
            e.printStackTrace();
        } finally {
            close(null, null, connection);
        }
    }

    public void deleteUser(int userId) {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "DELETE FROM users WHERE user_id = ?";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, userId);
            stmt.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            rollback(connection);
            e.printStackTrace();
        } finally {
            close(null, stmt, connection);
        }
    }

    public User getUser(int userId) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        User user = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT user_id, username, password_hash, email, created_at, role FROM users WHERE user_id = ?";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                user = readUser(rs, getWarehouseIds(connection, userId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(rs, stmt, connection);
        }
        return user;
    }

    public User getUserByUsername(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }

        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        User user = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT user_id, username, password_hash, email, created_at, role FROM users WHERE username = ?";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("user_id");
                user = readUser(rs, getWarehouseIds(connection, userId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(rs, stmt, connection);
        }
        return user;
    }

    public List<User> getAllUsers() {
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<User> users = new ArrayList<>();
        try {
            connection = DatabaseConnection.getConnection();
            Map<Integer, List<Integer>> warehouseIdsByUser = getWarehouseIdsByUser(connection);
            String query = "SELECT user_id, username, password_hash, email, created_at, role FROM users ORDER BY user_id";
            stmt = connection.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                int userId = rs.getInt("user_id");
                users.add(readUser(rs, warehouseIdsByUser.getOrDefault(userId, List.of())));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(rs, stmt, connection);
        }
        return users;
    }

    private User readUser(ResultSet rs, List<Integer> warehouseIds) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getString("created_at"),
                rs.getString("email"),
                UserRole.fromLabel(rs.getString("role")),
                rs.getString("password_hash"),
                rs.getString("username"),
                warehouseIds
        );
    }

    private List<Integer> getWarehouseIds(Connection connection, int userId) throws SQLException {
        String query = "SELECT warehouse_id FROM user_warehouses WHERE user_id = ? ORDER BY warehouse_id";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Integer> warehouseIds = new ArrayList<>();
                while (rs.next()) {
                    warehouseIds.add(rs.getInt("warehouse_id"));
                }
                return warehouseIds;
            }
        }
    }

    private Map<Integer, List<Integer>> getWarehouseIdsByUser(Connection connection) throws SQLException {
        Map<Integer, List<Integer>> result = new HashMap<>();
        String query = "SELECT user_id, warehouse_id FROM user_warehouses ORDER BY user_id, warehouse_id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                result.computeIfAbsent(rs.getInt("user_id"), id -> new ArrayList<>())
                        .add(rs.getInt("warehouse_id"));
            }
        }
        return result;
    }

    private void replaceUserWarehouses(Connection connection, int userId, List<Integer> warehouseIds) throws SQLException {
        try (PreparedStatement delete = connection.prepareStatement("DELETE FROM user_warehouses WHERE user_id = ?")) {
            delete.setInt(1, userId);
            delete.executeUpdate();
        }

        if (warehouseIds == null || warehouseIds.isEmpty()) {
            return;
        }

        try (PreparedStatement insert = connection.prepareStatement(
                "INSERT INTO user_warehouses (user_id, warehouse_id) VALUES (?, ?) ON CONFLICT DO NOTHING")) {
            for (Integer warehouseId : warehouseIds) {
                if (warehouseId == null || warehouseId <= 0) {
                    continue;
                }
                insert.setInt(1, userId);
                insert.setInt(2, warehouseId);
                insert.addBatch();
            }
            insert.executeBatch();
        }
    }

    private void rollback(Connection connection) {
        try {
            if (connection != null) {
                connection.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void close(ResultSet rs, Statement stmt, Connection connection) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (connection != null) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
