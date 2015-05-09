package db;

import main.data.Country;

import java.sql.*;
import java.util.Collection;
import java.util.Iterator;

public class DBConnection {

    private Connection conn;

    /**
     * Opens new connection to the db and initialize conn with it.
     * Throws DBException if something bad happened.
     * */
    public void connect() throws DBException {

        // loading the driver
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new DBException("Unable to load the MySQL JDBC driver..");
        }
        System.out.println("Driver loaded successfully");

        // creating the connection
        System.out.print("Trying to connect... ");
        try {
            conn = DriverManager.getConnection(String.format(CONNECTION_STRING, DEFAULT_HOST, DEFAULT_SCHEMA), DEFAULT_USER, DEFAULT_PASSWORD);
        } catch (SQLException e) {
            conn = null;
            throw new DBException("Unable to connect : " + e.getMessage());
        }
        System.out.println("Connected!");
    }

    /**
     * Disconnect, ignoring errors.
     */
    public void disconnect() {
        try {
            conn.close();
            System.out.println("Connection closed!");
        } catch (SQLException e) {
        }
    }

    /**
     * Upload countries to DB.
     * @throws DBException - Error while uploading data.
     */
    public void uploadCountries(Collection<Country> countries) throws DBException {
        try (PreparedStatement pstmt = conn
                .prepareStatement("INSERT INTO country(name) VALUES(?)");) {

            conn.setAutoCommit(false);

            int counter = 0;
            for (Country country : countries) {
                if (counter % 10000 == 0) {     // Execute batch once in 10000 iterations
                    pstmt.executeBatch();
                }
                pstmt.setString(1, country.name);
                pstmt.addBatch();

                counter++;
            }
            pstmt.executeBatch();

            conn.commit();

        } catch (SQLException e) {
            throw new DBException("Error while uploading countries to DB : " + e.getMessage());
        } finally {
            safelySetAutoCommit();
        }
    }

    /**
     * @return How many countries there are in the DB
     */
    public int getCountOfCountries() throws DBException {
        try (Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM country");) {
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new DBException("Error while counting countries : " + e.getMessage());
        }
    }

    /**
     * Clears data from DB.
     */
    public void deleteData() throws DBException {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM country");
        } catch (SQLException e) {
            throw new DBException("Error while deleting data from country : " + e.getMessage());
        }
    }

    /**
     * Attempts to set the connection back to auto-commit, ignoring errors.
     */
    private void safelySetAutoCommit() {
        try {
            conn.setAutoCommit(true);
        } catch (Exception e) {
        }
    }

    private static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static String CONNECTION_STRING = "jdbc:mysql://%s/%s";
    private static String DEFAULT_HOST = "localhost:3306";
    private static String DEFAULT_SCHEMA = "toyt";
    private static String DEFAULT_USER = "root";
    private static String DEFAULT_PASSWORD = "root";
}
