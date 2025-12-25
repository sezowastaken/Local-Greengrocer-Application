package com.group25.greengrocer.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbAdapter {
    private static final String URL = "jdbc:mysql://localhost:3306/grocery_db";
    private static final String USER = "myuser";
    private static final String PASSWORD = "1234";

    private static Connection connection;

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
