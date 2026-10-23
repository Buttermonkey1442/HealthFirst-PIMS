package com.healthfirst.pims.dao;

import com.healthfirst.pims.model.Medicine;
import com.healthfirst.pims.util.DBConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MedicineDAO {

    public List<Medicine> getAllMedicines() throws SQLException {
        String sql = "SELECT m.medicine_id, m.name, m.company, m.medicine_type, "
                   + "       m.price, m.quantity_in_stock, m.reorder_level, "
                   + "       m.expiry_date, m.supplier_id, s.name AS supplier_name "
                   + "FROM medicines m "
                   + "LEFT JOIN suppliers s ON m.supplier_id = s.supplier_id "
                   + "ORDER BY m.name ASC";

        List<Medicine> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToMedicine(rs));
            }
        }
        return list;
    }

    public List<Medicine> searchMedicines(String keyword) throws SQLException {
        String sql = "SELECT m.medicine_id, m.name, m.company, m.medicine_type, "
                   + "       m.price, m.quantity_in_stock, m.reorder_level, "
                   + "       m.expiry_date, m.supplier_id, s.name AS supplier_name "
                   + "FROM medicines m "
                   + "LEFT JOIN suppliers s ON m.supplier_id = s.supplier_id "
                   + "WHERE LOWER(m.name) LIKE ? OR LOWER(m.company) LIKE ? OR LOWER(m.medicine_type) LIKE ? "
                   + "ORDER BY m.name ASC";

        List<Medicine> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            String pattern = "%" + keyword.toLowerCase().trim() + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToMedicine(rs));
                }
            }
        }
        return list;
    }

    public boolean addMedicine(Medicine med) throws SQLException {
        String sql = "INSERT INTO medicines (name, company, medicine_type, price, quantity_in_stock, "
                   + "reorder_level, expiry_date, supplier_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, med.getName().trim());
            stmt.setString(2, med.getCompany().trim());
            stmt.setString(3, med.getMedicineType().trim());
            stmt.setBigDecimal(4, med.getPrice());
            stmt.setInt(5, med.getQuantityInStock());
            stmt.setInt(6, med.getReorderLevel());
            stmt.setDate(7, Date.valueOf(med.getExpiryDate()));
            stmt.setInt(8, med.getSupplierId());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        med.setMedicineId(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean updateMedicine(Medicine med) throws SQLException {
        String sql = "UPDATE medicines SET name = ?, company = ?, medicine_type = ?, price = ?, "
                   + "quantity_in_stock = ?, reorder_level = ?, expiry_date = ?, supplier_id = ? "
                   + "WHERE medicine_id = ?";

        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, med.getName().trim());
            stmt.setString(2, med.getCompany().trim());
            stmt.setString(3, med.getMedicineType().trim());
            stmt.setBigDecimal(4, med.getPrice());
            stmt.setInt(5, med.getQuantityInStock());
            stmt.setInt(6, med.getReorderLevel());
            stmt.setDate(7, Date.valueOf(med.getExpiryDate()));
            stmt.setInt(8, med.getSupplierId());
            stmt.setInt(9, med.getMedicineId());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteMedicine(int medicineId) throws SQLException {
        if (isMedicineReferencedInSales(medicineId)) {
            throw new SQLException("Cannot delete medicine: Transaction history references this item in sale_items. Archive it or update stock instead.");
        }

        String sql = "DELETE FROM medicines WHERE medicine_id = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, medicineId);
            return stmt.executeUpdate() > 0;
        }
    }


    public boolean isMedicineReferencedInSales(int medicineId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM sale_items WHERE medicine_id = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, medicineId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private Medicine mapResultSetToMedicine(ResultSet rs) throws SQLException {
        Medicine med = new Medicine(
            rs.getInt("medicine_id"),
            rs.getString("name"),
            rs.getString("company"),
            rs.getString("medicine_type"),
            rs.getBigDecimal("price"),
            rs.getInt("quantity_in_stock"),
            rs.getInt("reorder_level"),
            rs.getDate("expiry_date").toLocalDate(),
            rs.getInt("supplier_id")
        );
        med.setSupplierName(rs.getString("supplier_name"));
        return med;
    }
}