package com.warewise.server.database.handler;

import com.warewise.common.model.Supplier;
import com.warewise.common.util.enums.TableName;
import com.warewise.common.util.SequenceManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SupplierHandler {

    public void addSupplier(Supplier supplier) {
        String sql = "INSERT INTO suppliers (supplier_id,supplier_name, contact_email, contact_phone, address) VALUES (?,?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, supplier.getID());
            stmt.setString(2, supplier.getName());
            stmt.setString(3, supplier.getContactEmail());
            stmt.setString(4, supplier.getContactPhoneNo());
            stmt.setString(5, supplier.getAddress());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateSupplier(Supplier supplier) {
        String sql = "UPDATE suppliers SET supplier_name = ?, contact_email = ?, contact_phone = ?, address = ? WHERE supplier_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, supplier.getName());
            stmt.setString(2, supplier.getContactEmail());
            stmt.setString(3, supplier.getContactPhoneNo());
            stmt.setString(4, supplier.getAddress());
            stmt.setInt(5, supplier.getID());
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Updated " + rowsAffected + " row(s) successfully.");
        } catch (SQLException e) {
            System.err.println("Error updating supplier: " + e.getMessage());
        }
    }

    public void deleteSupplier(Supplier supplier) {
        String sql = "DELETE FROM suppliers WHERE supplier_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, supplier.getID());
            stmt.executeUpdate();
            SequenceManager.getInstance().onDelete(TableName.SUPPLIERS.getTableName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Supplier> loadSuppliers() {
        String sql = "SELECT * FROM suppliers";
        List<Supplier> output = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet result = stmt.executeQuery()) {
            while (result.next()) {
                int supplier_id = result.getInt(1);
                String supplierName = result.getString(2);
                String contactEmail = result.getString(3);
                String contactPhone = result.getString(4);
                String address = result.getString(5);
                String created_at = result.getString(6);
                Supplier supplier = new Supplier(supplier_id, supplierName, contactEmail, contactPhone, address, created_at);
                output.add(supplier);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return output;
    }
}
