package warewise.server.common.handler;

import warewise.server.common.model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryHandler {

    private static CategoryHandler instance;

    public static CategoryHandler getInstance() {
        if (instance == null) {
            instance = new CategoryHandler();
        }
        return instance;
    }

    public void addCategory(Category category) {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "INSERT INTO categories (category_id, name, description) VALUES (?, ?, ?)";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, category.getID());
            stmt.setString(2, category.getName());
            stmt.setString(3, category.getDescription());
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

    public void updateCategory(Category category) {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "UPDATE categories SET name = ?, description = ? WHERE category_id = ?";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());
            stmt.setInt(3, category.getID());
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

    public void deleteCategory(int categoryId) {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "DELETE FROM categories WHERE category_id = ?";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, categoryId);
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

    public Category getCategory(int categoryId) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Category category = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM categories WHERE category_id = ?";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, categoryId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                category = new Category(rs.getInt("category_id"),rs.getString("name"),rs.getString("description"));
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
        return category;
    }

    public List<Category> getAllCategories() {
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Category> categories = new ArrayList<>();
        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM categories";
            stmt = connection.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                Category category = new Category(rs.getInt("category_id"),rs.getString("name"),rs.getString("description"));
                categories.add(category);
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
        return categories;
    }
}
