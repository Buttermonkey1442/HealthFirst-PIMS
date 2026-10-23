package com.healthfirst.pims.dao;

import com.healthfirst.pims.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ReportDAO {

    public List<Vector<Object>> getSalesReport() throws SQLException {
        String sql = "SELECT s.sale_id, s.sale_date, u.full_name AS cashier_name, "
                   + "       COUNT(si.sale_item_id) AS total_items, s.total_amount "
                   + "FROM sales s "
                   + "JOIN users u ON s.user_id = u.user_id "
                   + "LEFT JOIN sale_items si ON s.sale_id = si.sale_id "
                   + "GROUP BY s.sale_id, s.sale_date, u.full_name, s.total_amount "
                   + "ORDER BY s.sale_date DESC";

        List<Vector<Object>> data = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("sale_id"));
                row.add(rs.getTimestamp("sale_date").toString());
                row.add(rs.getString("cashier_name"));
                row.add(rs.getInt("total_items"));
                row.add(String.format("R %.2f", rs.getDouble("total_amount")));
                data.add(row);
            }
        }
        return data;
    }

    public List<Vector<Object>> getItemWiseSalesReport() throws SQLException {
        String sql = "SELECT m.medicine_id, m.name, m.company, m.medicine_type, "
                   + "       COALESCE(SUM(si.quantity_sold), 0) AS units_sold, "
                   + "       COALESCE(SUM(si.quantity_sold * si.price_at_sale), 0.00) AS total_revenue "
                   + "FROM medicines m "
                   + "LEFT JOIN sale_items si ON m.medicine_id = si.medicine_id "
                   + "GROUP BY m.medicine_id, m.name, m.company, m.medicine_type "
                   + "ORDER BY units_sold DESC, total_revenue DESC";

        List<Vector<Object>> data = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("medicine_id"));
                row.add(rs.getString("name"));
                row.add(rs.getString("company"));
                row.add(rs.getString("medicine_type"));
                row.add(rs.getInt("units_sold"));
                row.add(String.format("R %.2f", rs.getDouble("total_revenue")));
                data.add(row);
            }
        }
        return data;
    }

    public List<Vector<Object>> getLowStockReport() throws SQLException {
        String sql = "SELECT m.medicine_id, m.name, m.company, m.quantity_in_stock, "
                   + "       m.reorder_level, s.name AS supplier_name, s.phone AS supplier_phone "
                   + "FROM medicines m "
                   + "JOIN suppliers s ON m.supplier_id = s.supplier_id "
                   + "WHERE m.quantity_in_stock <= m.reorder_level "
                   + "ORDER BY m.quantity_in_stock ASC";

        List<Vector<Object>> data = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("medicine_id"));
                row.add(rs.getString("name"));
                row.add(rs.getString("company"));
                row.add(rs.getInt("quantity_in_stock"));
                row.add(rs.getInt("reorder_level"));
                row.add(rs.getString("supplier_name"));
                row.add(rs.getString("supplier_phone"));
                data.add(row);
            }
        }
        return data;
    }

    public List<Vector<Object>> getExpiryReport() throws SQLException {
        String sql = "SELECT m.medicine_id, m.name, m.company, m.expiry_date, "
                   + "       DATEDIFF(m.expiry_date, CURDATE()) AS days_left, "
                   + "       m.quantity_in_stock, s.name AS supplier_name "
                   + "FROM medicines m "
                   + "JOIN suppliers s ON m.supplier_id = s.supplier_id "
                   + "WHERE m.expiry_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 1 MONTH) "
                   + "   OR m.expiry_date < CURDATE() "
                   + "ORDER BY m.expiry_date ASC";

        List<Vector<Object>> data = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("medicine_id"));
                row.add(rs.getString("name"));
                row.add(rs.getString("company"));
                row.add(rs.getDate("expiry_date").toString());
                int daysLeft = rs.getInt("days_left");
                row.add(daysLeft < 0 ? "EXPIRED (" + Math.abs(daysLeft) + "d ago)" : daysLeft + " days");
                row.add(rs.getInt("quantity_in_stock"));
                row.add(rs.getString("supplier_name"));
                data.add(row);
            }
        }
        return data;
    }
}