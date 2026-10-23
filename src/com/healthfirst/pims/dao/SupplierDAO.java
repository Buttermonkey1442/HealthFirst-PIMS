package com.healthfirst.pims.dao;

import com.healthfirst.pims.model.Supplier;
import com.healthfirst.pims.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {

    public List<Supplier> getAllSuppliers() throws SQLException {
        String sql = "SELECT supplier_id, name, contact_person, phone, email, address "
                   + "FROM suppliers ORDER BY name ASC";
        List<Supplier> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToSupplier(rs));
            }
        }
        return list;
    }

    public List<Supplier> searchSuppliers(String keyword) throws SQLException {
        String sql = "SELECT supplier_id, name, contact_person, phone, email, address "
                   + "FROM suppliers "
                   + "WHERE LOWER(name) LIKE ? OR LOWER(contact_person) LIKE ? "
                   + "ORDER BY name ASC";
        List<Supplier> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            String pattern = "%" + keyword.toLowerCase().trim() + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToSupplier(rs));
                }
            }
        }
        return list;
    }

    public boolean addSupplier(Supplier supplier) throws SQLException {
        String sql = "INSERT INTO suppliers (name, contact_person, phone, email, address) VALUES (?, ?, ?, ?, ?)";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, supplier.getName().trim());
            stmt.setString(2, supplier.getContactPerson().trim());
            stmt.setString(3, supplier.getPhone().trim());
            stmt.setString(4, supplier.getEmail().trim());
            stmt.setString(5, supplier.getAddress().trim());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        supplier.setSupplierId(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean updateSupplier(Supplier supplier) throws SQLException {
        String sql = "UPDATE suppliers SET name = ?, contact_person = ?, phone = ?, email = ?, address = ? "
                   + "WHERE supplier_id = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, supplier.getName().trim());
            stmt.setString(2, supplier.getContactPerson().trim());
            stmt.setString(3, supplier.getPhone().trim());
            stmt.setString(4, supplier.getEmail().trim());
            stmt.setString(5, supplier.getAddress().trim());
            stmt.setInt(6, supplier.getSupplierId());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteSupplier(int supplierId) throws SQLException {
        if (isSupplierReferenced(supplierId)) {
            throw new SQLException("Cannot delete supplier: Active medicines are linked to this supplier. Reassign or delete the medicines first.");
        }

        String sql = "DELETE FROM suppliers WHERE supplier_id = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, supplierId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean isSupplierReferenced(int supplierId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM medicines WHERE supplier_id = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, supplierId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private Supplier mapResultSetToSupplier(ResultSet rs) throws SQLException {
        return new Supplier(
            rs.getInt("supplier_id"),
            rs.getString("name"),
            rs.getString("contact_person"),
            rs.getString("phone"),
            rs.getString("email"),
            rs.getString("address")
        );
    }
}