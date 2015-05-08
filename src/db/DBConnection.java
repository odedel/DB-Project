package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private Connection conn;

    /** Opens new connection to the db and initialize conn with it. Returns true if the connection is open */
    public boolean connect() {

        // loading the driver
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println("Unable to load the MySQL JDBC driver..");
            return false;
        }
        System.out.println("Driver loaded successfully");

        // creating the connection
        System.out.print("Trying to connect... ");
        try {
            conn = DriverManager.getConnection(String.format(CONNECTION_STRING, DEFAULT_HOST, DEFAULT_SCHEMA), DEFAULT_USER, DEFAULT_PASSWORD);
        } catch (SQLException e) {
            System.out.println("Unable to connect - " + e.getMessage());
            conn = null;
            return false;
        }
        System.out.println("Connected!");
        return true;
    }

    /**
     * close the connection
     */
    public void close() {
        try {
            conn.close();
            System.out.println("Connection closed!");
        } catch (SQLException e) {
            System.out.println("Unable to close the connection - "
                    + e.getMessage());
        }
    }

    private static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static String CONNECTION_STRING = "jdbc:mysql://%s/%s";
    private static String DEFAULT_HOST = "localhost:3306";
    private static String DEFAULT_SCHEMA = "sakila";
    private static String DEFAULT_USER = "root";
    private static String DEFAULT_PASSWORD = "root";
}
