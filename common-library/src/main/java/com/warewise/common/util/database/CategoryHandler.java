package com.warewise.common.util.database;

import com.warewise.common.model.Category;
import com.warewise.common.model.Inventory;
import com.warewise.common.model.Item;
import com.warewise.common.model.Logs;
import com.warewise.common.model.Order;
import com.warewise.common.model.StockAlert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryHandler {

    public void addCategory(Category category) {
        String sql = "INSERT INTO categories (category_name, description) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCategory(Category category) {
        String sql = "UPDATE categories SET category_name = ?, description = ? WHERE category_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());
            stmt.setInt(3, category.getID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCategory(Category category) {
        String sql = "DELETE FROM categories WHERE category_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, category.getID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Category> loadCategories() {
        String sql = "SELECT * FROM categories";
        List<Category> categories = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet resultSet = stmt.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                String createdAt = resultSet.getString("created_at");
                categories.add(new Category(id, name, description, createdAt));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }
}