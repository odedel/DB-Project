package db;

import main.data.City;
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
        ResultSet rs = null;
        try (PreparedStatement pstmt = conn
                .prepareStatement("INSERT INTO country(name, creation_date, economic_growth, poverty, population, unemployment, gini, influation, population_density) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);) {

            conn.setAutoCommit(false);

            int counter = 0;
            for (Country country : countries) {
//                if (counter % 100000000 == 0) {     // Execute batch once in 10000 iterations,
//                                                        //  think if you want that, if so - add the generated keys
//                    pstmt.executeBatch();
//                }
                pstmt.setString(1, country.name);
                if (country.creationDate != null) {
                    pstmt.setDate(2, Date.valueOf(country.creationDate));
                } else {
                    pstmt.setDate(2, null);
                }
                pstmt.setFloat(3, country.economicGrowth);
                pstmt.setFloat(4, country.poverty);
                pstmt.setLong(5, country.population);
                pstmt.setFloat(6, country.unemployment);
                pstmt.setFloat(7, country.gini);
                pstmt.setFloat(8, country.inflation);
                pstmt.setFloat(9, country.populationDensity);
                pstmt.addBatch();

                counter++;
            }
            pstmt.executeBatch();

            rs = pstmt.getGeneratedKeys();
            Iterator<Country> countryIterator = countries.iterator();
            while (rs.next()) {
                countryIterator.next().id = rs.getInt(1);
            }

            conn.commit();

        } catch (SQLException e) {
            throw new DBException("Error while uploading countries to DB : " + e.getMessage());
        } finally {
            safelySetAutoCommit();
            if (rs != null)
                try {
                    rs.close();
                } catch (SQLException e) {
                    throw new DBException("Error while uploading countries to DB : " + e.getMessage());
                }
        }
    }

    /**
     * Upload cities to DB.
     * @throws DBException - Error while uploading data.
     */
    public void uploadCities(Collection<City> cities) throws DBException {
        ResultSet rs = null;
        try (PreparedStatement pstmt = conn
                .prepareStatement("INSERT INTO city(name, country_id) VALUES(?, ?)",
                        Statement.RETURN_GENERATED_KEYS);) {

            conn.setAutoCommit(false);

            int counter = 0;
            for (City city : cities) {
//                if (counter % 100000000 == 0) {     // Execute batch once in 10000 iterations,
//                                                        //  think if you want that, if so - add the generated keys
//                    pstmt.executeBatch();
//                }
                pstmt.setString(1, city.name);
                pstmt.setInt(2, city.country.id);
                pstmt.addBatch();

                counter++;
            }
            pstmt.executeBatch();

            rs = pstmt.getGeneratedKeys();
            Iterator<City> cityIterator = cities.iterator();
            while (rs.next()) {
                cityIterator.next().id = rs.getInt(1);
            }

            conn.commit();

        } catch (SQLException e) {
            throw new DBException("Error while uploading countries to DB : " + e.getMessage());
        } finally {
            safelySetAutoCommit();
            if (rs != null)
                try {
                    rs.close();
                } catch (SQLException e) {
                    throw new DBException("Error while uploading countries to DB : " + e.getMessage());
                }
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
