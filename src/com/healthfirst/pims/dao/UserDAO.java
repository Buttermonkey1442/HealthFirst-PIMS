package com.healthfirst.pims.dao;

import com.healthfirst.pims.model.User;
import com.healthfirst.pims.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User authenticate(String username, String password) throws SQLException {
        String sql = "SELECT user_id, username, password, role, full_name "
                   + "FROM users "
                   + "WHERE username = ? AND password = ?";

        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username.trim());
            stmt.setString(2, password.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }

    public List<User> getAllUsers() throws SQLException {
        String sql = "SELECT user_id, username, password, role, full_name FROM users ORDER BY role ASC, full_name ASC";
        List<User> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToUser(rs));
            }
        }
        return list;
    }

    public boolean isUsernameTaken(String username, int excludeUserId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ? AND user_id != ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username.trim());
            stmt.setInt(2, excludeUserId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public boolean addUser(User user) throws SQLException {
        if (isUsernameTaken(user.getUsername(), -1)) {
            throw new SQLException("The username '" + user.getUsername() + "' is already registered.");
        }

        String sql = "INSERT INTO users (username, password, role, full_name) VALUES (?, ?, ?, ?)";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getUsername().trim());
            stmt.setString(2, user.getPassword().trim());
            stmt.setString(3, user.getRole());
            stmt.setString(4, user.getFullName().trim());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        user.setUserId(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean updateUser(User user) throws SQLException {
        if (isUsernameTaken(user.getUsername(), user.getUserId())) {
            throw new SQLException("The username '" + user.getUsername() + "' is already taken by another account.");
        }

        String sql = "UPDATE users SET username = ?, password = ?, role = ?, full_name = ? WHERE user_id = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername().trim());
            stmt.setString(2, user.getPassword().trim());
            stmt.setString(3, user.getRole());
            stmt.setString(4, user.getFullName().trim());
            stmt.setInt(5, user.getUserId());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteUser(int userId) throws SQLException {
        if (isUserReferencedInSales(userId)) {
            throw new SQLException("Cannot delete account: This user is recorded as the cashier on past sales transactions[cite: 4].");
        }

        String sql = "DELETE FROM users WHERE user_id = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean isUserReferencedInSales(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM sales WHERE user_id = ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("user_id"),
            rs.getString("username"),
            rs.getString("password"),
            rs.getString("role"),
            rs.getString("full_name")
        );
    }
}