package warewise.server.common.handler;

import warewise.server.common.model.Warehouse;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WarehouseHandler {

    private static WarehouseHandler instance;

    public static WarehouseHandler getInstance(){
        if(instance == null){
            instance = new WarehouseHandler();
        }
        return  instance;
    }

    public void addWarehouse(Warehouse warehouse) {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "INSERT INTO warehouse (name, address) VALUES (?, ?)";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, warehouse.getName());
            stmt.setString(2, warehouse.getAddress());
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

    public void updateWarehouse(Warehouse warehouse) {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "UPDATE warehouse SET name = ?, address = ? WHERE warehouse_id = ?";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, warehouse.getName());
            stmt.setString(2, warehouse.getAddress());
            stmt.setInt(3, warehouse.getWarehouse_id());
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

    public void deleteWarehouse(int warehouse_id) {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "DELETE FROM warehouse WHERE warehouse_id = ?";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, warehouse_id);
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

    public Warehouse getWarehouse(int warehouse_id) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Warehouse warehouse = null;
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM warehouse WHERE warehouse_id = ?";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, warehouse_id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                warehouse = new Warehouse(
                    rs.getInt("warehouse_id"),
                    rs.getString("name"),
                    rs.getString("address")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return warehouse;
    }

    public List<Warehouse> getAllWarehouses() {
        Statement stmt = null;
        ResultSet rs = null;
        List<Warehouse> warehouses = new ArrayList<>();
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM warehouse";
            stmt = connection.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                warehouses.add(new Warehouse(
                    rs.getInt("warehouse_id"),
                    rs.getString("name"),
                    rs.getString("address")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return warehouses;
    }
}
