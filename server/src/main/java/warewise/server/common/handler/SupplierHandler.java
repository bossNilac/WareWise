package warewise.server.common.handler;

import warewise.server.common.model.Supplier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierHandler {

    private static SupplierHandler instance;

    public static SupplierHandler getInstance() {
        if (instance == null) {
            instance = new SupplierHandler();
        }
        return instance;
    }

    public void addSupplier(Supplier supplier) {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "INSERT INTO suppliers (supplier_name, contact_email, contact_phone, address, created_at) VALUES (?, ?, ?, ?, ?)";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, supplier.getName());
            stmt.setString(2, supplier.getContactEmail());
            stmt.setString(3, supplier.getContactPhoneNo());
            stmt.setString(4, supplier.getAddress());
            stmt.setString(5, supplier.getCreatedAt());
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

    public void updateSupplier(Supplier supplier) {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "UPDATE suppliers SET supplier_name = ?, contact_email = ?, contact_phone = ?, address = ?, created_at = ? WHERE supplier_id = ?";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, supplier.getName());
            stmt.setString(2, supplier.getContactEmail());
            stmt.setString(3, supplier.getContactPhoneNo());
            stmt.setString(4, supplier.getAddress());
            stmt.setString(5, supplier.getCreatedAt());
            stmt.setInt(6, supplier.getID());
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

    public void deleteSupplier(int supplierId) {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "DELETE FROM suppliers WHERE supplier_id = ?";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, supplierId);
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

    public Supplier getSupplier(int supplierId) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Supplier supplier = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM suppliers WHERE supplier_id = ?";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, supplierId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                supplier = new Supplier(
                        rs.getInt("supplier_id"),
                        rs.getString("supplier_name"),
                        rs.getString("contact_email"),
                        rs.getString("contact_phone"),
                        rs.getString("address"),
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
        return supplier;
    }

    public List<Supplier> getAllSuppliers() {
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Supplier> suppliers = new ArrayList<>();
        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM suppliers";
            stmt = connection.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                Supplier supplier = new Supplier(
                        rs.getInt("supplier_id"),
                        rs.getString("supplier_name"),
                        rs.getString("contact_email"),
                        rs.getString("contact_phone"),
                        rs.getString("address"),
                        rs.getString("created_at")
                );
                suppliers.add(supplier);
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
        return suppliers;
    }
}
