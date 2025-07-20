package warewise.server.common.handler;

import warewise.server.common.model.GeneralItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GeneralItemHandler {

    private static GeneralItemHandler instance;

    public static GeneralItemHandler getInstance() {
        if (instance == null) {
            instance = new GeneralItemHandler();
        }
        return instance;
    }

    public void addGeneralItem(GeneralItem item) {
        Connection connection = null;
        PreparedStatement stmt = null;

        try {
            connection = DatabaseConnection.getConnection();
            String query = "INSERT INTO items_general_data (id, name, set_quantity, barcode, category_id, supplier_id,price,expires) VALUES (?, ?, ? ,? , ?, ?, ?,?)";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, item.getId());
            stmt.setString(2, item.getName());
            stmt.setInt(3, item.getSetQuantity());
            stmt.setString(4, item.getBarcode());
            stmt.setInt(5, item.getCategoryId());
            stmt.setInt(6, item.getPrice());
            stmt.setInt(7, item.getSupplierId());
            stmt.setBoolean(8, item.isExpires());
            stmt.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try { if (connection != null) connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public void updateGeneralItem(GeneralItem item) {
        Connection connection = null;
        PreparedStatement stmt = null;

        try {
            connection = DatabaseConnection.getConnection();
            String query = "UPDATE items_general_data SET name = ?, set_quantity = ?, barcode = ?, category_id = ?, supplier_id = ? , price = ?,expires = ? WHERE id = ?";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, item.getName());
            stmt.setInt(2, item.getSetQuantity());
            stmt.setString(3, item.getBarcode());
            stmt.setInt(4, item.getCategoryId());
            stmt.setInt(5, item.getSupplierId());
            stmt.setInt(6, item.getPrice());
            stmt.setBoolean(7, item.isExpires());
            stmt.setInt(8, item.getId());
            stmt.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try { if (connection != null) connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public void deleteGeneralItem(int id) {
        Connection connection = null;
        PreparedStatement stmt = null;

        try {
            connection = DatabaseConnection.getConnection();
            String query = "DELETE FROM items_general_data WHERE id = ?";
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

    public GeneralItem getGeneralItem(int id) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        GeneralItem item = null;

        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM items_general_data WHERE id = ?";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                item = new GeneralItem(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("set_quantity"),
                    rs.getString("barcode"),
                    rs.getInt("category_id"),
                    rs.getInt("supplier_id"),
                    rs.getInt("price"),
                    rs.getBoolean("expires")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); if (stmt != null) stmt.close(); if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        return item;
    }

    public List<GeneralItem> getAllGeneralItems() {
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<GeneralItem> items = new ArrayList<>();

        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM items_general_data";
            stmt = connection.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                GeneralItem item = new GeneralItem(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("set_quantity"),
                    rs.getString("barcode"),
                    rs.getInt("category_id"),
                    rs.getInt("supplier_id"),
                    rs.getInt("price"),
                    rs.getBoolean("expires")

                );
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); if (stmt != null) stmt.close(); if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        return items;
    }
}
