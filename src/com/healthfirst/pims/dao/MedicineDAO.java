package com.healthfirst.pims.dao;

import com.healthfirst.pims.model.Medicine;
import com.healthfirst.pims.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
                   + "WHERE LOWER(m.name) LIKE ? OR LOWER(m.company) LIKE ? "
                   + "ORDER BY m.name ASC";

        List<Medicine> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            String pattern = "%" + keyword.toLowerCase().trim() + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToMedicine(rs));
                }
            }
        }
        return list;
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