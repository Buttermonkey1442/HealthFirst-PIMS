package com.healthfirst.pims.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {

    private static final String DB_NAME = "pims_db";
    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    
    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME 
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
    
    private static final String USER = "root";
    private static final String PASSWORD = "Admin#2025"; 

    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    private static Connection connection = null;

    private DBConnection() {}

    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName(DRIVER_CLASS);
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found. Ensure Connector/J is added to project libraries.", e);
            }
        }
        return connection;
    }

    public static synchronized void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException ex) {
                System.err.println("Error closing database connection: " + ex.getMessage());
            } finally {
                connection = null;
            }
        }
    }


    public static void main(String[] args) {
        System.out.println("--- HealthFirst PIMS: Database Diagnostic Check ---");
        
        try (Connection testConn = DBConnection.getConnection()) {
            if (testConn != null && !testConn.isClosed()) {
                System.out.println("[SUCCESS] Successfully connected to MySQL database: " + DB_NAME);
                
                DatabaseMetaData meta = testConn.getMetaData();
                System.out.println("[INFO] RDBMS Engine: " + meta.getDatabaseProductName() + " " + meta.getDatabaseProductVersion());
                System.out.println("[INFO] Driver Version: " + meta.getDriverName() + " " + meta.getDriverVersion());

                String testQuery = "SELECT table_name FROM information_schema.tables WHERE table_schema = '" + DB_NAME + "'";
                try (Statement stmt = testConn.createStatement();
                     ResultSet rs = stmt.executeQuery(testQuery)) {
                    
                    System.out.println("\nDetected Tables in Schema:");
                    int count = 0;
                    while (rs.next()) {
                        System.out.println("  - " + rs.getString("table_name"));
                        count++;
                    }
                    System.out.println("Total tables verified: " + count + "/5");
                }
            }
        } catch (SQLException ex) {
            System.err.println("[FAILURE] Could not establish connection to MySQL.");
            System.err.println("Error Code: " + ex.getErrorCode());
            System.err.println("SQL State: " + ex.getSQLState());
            System.err.println("Message: " + ex.getMessage());
            System.err.println("\nTroubleshooting:");
            System.err.println("1. Verify MySQL Server service is running in Windows Services or MySQL Workbench.");
            System.err.println("2. Confirm database 'pims_db' was created with database.sql.");
            System.err.println("3. Ensure the 'PASSWORD' constant matches your MySQL root account password.");
        }
    }
}