package com.group25.greengrocer.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database adapter utility class for managing database connections.
 * 
 * This class provides a singleton connection pattern for MySQL database access.
 * It handles connection creation, reuse, and cleanup for the grocery database.
 * The connection is automatically reused if it is still open, or recreated if closed.
 * 
 * Configuration:
 * The database connection parameters (URL, username, password) are hardcoded.
 * For production use, these should be moved to a configuration file.
 */
public class DbAdapter {
    /**
     * Database connection URL for MySQL database.
     */
    private static final String URL = "jdbc:mysql://localhost:3306/grocery_db";
    
    /**
     * Database username for authentication.
     */
    private static final String USER = "myuser";
    
    /**
     * Database password for authentication.
     */
    private static final String PASSWORD = "1234";

    /**
     * Singleton database connection instance.
     * Reused across method calls if still open.
     */
    private static Connection connection;

    /**
     * Gets a database connection. If the connection is null or closed,
     * creates a new connection. Otherwise, returns the existing connection.
     * 
     * This method implements a lazy singleton pattern for database connections,
     * ensuring efficient connection reuse while handling connection failures gracefully.
     * 
     * @return a Connection object to the database
     * @throws SQLException if a database access error occurs or connection cannot be established
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (SQLException e) {
                System.err.println("Connection failed!");
                throw e;
            }
        }
        return connection;
    }

    /**
     * Closes the database connection if it is currently open.
     * 
     * This method safely closes the connection and handles any SQLExceptions
     * that may occur during the closing process. After calling this method,
     * the connection instance is set to null internally (after closing),
     * so subsequent calls to getConnection() will create a new connection.
     * 
     * Note: This method should be called when the application
     * is shutting down or when you want to explicitly release database resources.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
