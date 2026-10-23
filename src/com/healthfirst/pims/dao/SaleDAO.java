package com.healthfirst.pims.dao;

import com.healthfirst.pims.model.CartItem;
import com.healthfirst.pims.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class SaleDAO {

    public int processSale(int userId, double totalAmount, List<CartItem> items) throws SQLException {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Cannot process an empty sale cart.");
        }

        Connection conn = DBConnection.getConnection();
        boolean originalAutoCommit = conn.getAutoCommit();

        String insertSaleSQL = "INSERT INTO sales (total_amount, user_id) VALUES (?, ?)";
        String insertItemSQL = "INSERT INTO sale_items (sale_id, medicine_id, quantity_sold, price_at_sale) VALUES (?, ?, ?, ?)";
        String updateStockSQL = "UPDATE medicines SET quantity_in_stock = quantity_in_stock - ? WHERE medicine_id = ? AND quantity_in_stock >= ?";

        int generatedSaleId = -1;

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement saleStmt = conn.prepareStatement(insertSaleSQL, Statement.RETURN_GENERATED_KEYS)) {
                saleStmt.setDouble(1, totalAmount);
                saleStmt.setInt(2, userId);
                saleStmt.executeUpdate();

                try (ResultSet rs = saleStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedSaleId = rs.getInt(1);
                    } else {
                        throw new SQLException("Failed to obtain generated sale_id.");
                    }
                }
            }

            try (PreparedStatement itemStmt = conn.prepareStatement(insertItemSQL);
                 PreparedStatement stockStmt = conn.prepareStatement(updateStockSQL)) {

                for (CartItem item : items) {
                    itemStmt.setInt(1, generatedSaleId);
                    itemStmt.setInt(2, item.getMedicineId());
                    itemStmt.setInt(3, item.getQuantity());
                    itemStmt.setDouble(4, item.getPrice());
                    itemStmt.addBatch();

                    stockStmt.setInt(1, item.getQuantity());
                    stockStmt.setInt(2, item.getMedicineId());
                    stockStmt.setInt(3, item.getQuantity());
                    stockStmt.addBatch();
                }

                itemStmt.executeBatch();
                int[] stockUpdateResults = stockStmt.executeBatch();

                for (int updatedRows : stockUpdateResults) {
                    if (updatedRows == 0) {
                        throw new SQLException("Transaction aborted: Insufficient stock for one or more medicines.");
                    }
                }
            }

            conn.commit();
            return generatedSaleId;
        } catch (SQLException ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.setAutoCommit(originalAutoCommit);
        }
    }
}