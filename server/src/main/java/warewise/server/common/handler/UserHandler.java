package warewise.server.common.handler;


import warewise.server.common.model.User;
import warewise.server.common.util.enums.UserRole;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserHandler {

    private static UserHandler instance;

    public static UserHandler getInstance(){
        if(instance == null){
            instance = new UserHandler();
        }
        return instance;
    }

    public void addUser(User user) {
        String sql = "INSERT INTO users (username, password_hash, role, email) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, String.valueOf(user.getRole()));
            stmt.setString(4, user.getEmail());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateUser(User user) {
        String sql = "UPDATE users SET username = ?, password_hash = ?, role = ?, email = ? WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set the values for the placeholders
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, String.valueOf(user.getRole()));
            stmt.setString(4, user.getEmail());
            stmt.setInt(5, user.getID());

            // Execute the update statement
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Updated " + rowsAffected + " row(s) successfully.");

        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
        }
    }

    public void deleteUser(User user) {
        String sql = "DELETE FROM users WHERE user_id =(?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, user.getID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<User> loadUsers() {
        String sql = "SELECT * FROM users";
        ResultSet result;
        List<User> output = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             result= stmt.executeQuery();
             while (result.next()){
                 int id = result.getInt(1);
                 String username = result.getString(2);
                 String password_hash = result.getString(3);
                 String role = result.getString(4);
                 String email = result.getString(5);
                 String created_at =result.getString(6);
                 User user = new User(id,created_at,email, UserRole.valueOf(role),
                         password_hash,username);
                 output.add(user);
             }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return output;
    }

    public User loadUser(int userId) {
        String sql = "SELECT * FROM users where user_id=?";
        ResultSet result;
        User output = null;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1,userId);
            result= stmt.executeQuery();
            while (result.next()){
                int id = result.getInt(1);
                String username = result.getString(2);
                String password_hash = result.getString(3);
                String role = result.getString(4);
                String email = result.getString(5);
                String created_at =result.getString(6);
                 output = new User(id,created_at,email, UserRole.valueOf(role),
                        password_hash,username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return output;
    }

}
