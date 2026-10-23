package com.healthfirst.pims.dao;

import com.healthfirst.pims.model.User;
import com.healthfirst.pims.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
                    return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("full_name")
                    );
                }
            }
        }
        return null;
    }
}