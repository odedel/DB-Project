package db;

import main.data.City;
import main.data.Country;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class DBConnection {

    private Connection conn;

    /**
     * Opens new connection to the db and initialize conn with it.
     * Throws DBException if something bad happened.
     * */
    public void connect(User user) throws DBException {

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
            conn = DriverManager.getConnection(String.format(CONNECTION_STRING, DEFAULT_HOST, DEFAULT_SCHEMA),
                    user.toString().toLowerCase(), user.toString().toLowerCase());
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
            if (!conn.isClosed()) {
                conn.close();
            }
            System.out.println("Connection closed!");
        } catch (SQLException e) {
        }
    }

    /**
     * Upload countries to DB.
     * @throws DBException - Error while uploading data.
     */
    public void uploadCountries(List<Country> countries) throws DBException {
        ResultSet rs = null;
        try (PreparedStatement pstmt = conn
                .prepareStatement("INSERT INTO country(name, creation_date, economic_growth, poverty, population, unemployment, gini, inflation, population_density) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)",
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
    public void uploadCities(List<City> cities) throws DBException {
        ResultSet rs = null;
        try (PreparedStatement pstmt = conn
                .prepareStatement("INSERT INTO city(name, country_id, creation_date, economic_growth, poverty, population, unemployment, gini, inflation, population_density) " +
                                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
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
                if (city.creationDate != null) {
                    pstmt.setDate(3, Date.valueOf(city.creationDate));
                } else {
                    pstmt.setDate(3, null);
                }
                pstmt.setFloat(4, city.economicGrowth);
                pstmt.setFloat(5, city.poverty);
                pstmt.setLong(6, city.population);
                pstmt.setFloat(7, city.unemployment);
                pstmt.setFloat(8, city.gini);
                pstmt.setFloat(9, city.inflation);
                pstmt.setFloat(10, city.populationDensity);
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

    public int getCountOfCities() throws DBException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM city");) {
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new DBException("Error while counting countries : " + e.getMessage());
        }
    }

    public Map<Integer, Country> getAllCountriesData() throws DBException {
        Map<Integer, Country> countries = new HashMap<>();

        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM COUNTRY");

            while (rs.next()) {
                Country country = new Country();

                country.id = rs.getInt("id");
                country.name = rs.getString("name");

                Date creationDate = rs.getDate("creation_date");
                if (creationDate != null) {
                    country.creationDate = creationDate.toLocalDate();
                }
                country.economicGrowth = rs.getFloat("economic_growth");
                country.poverty = rs.getFloat("poverty");
                country.population = rs.getLong("population");
                country.unemployment = rs.getFloat("unemployment");
                country.gini = rs.getFloat("gini");
                country.inflation = rs.getFloat("inflation");
                country.populationDensity = rs.getFloat("population_density");

                countries.put(country.id, country);
            }

        } catch (SQLException e) {
            throw new DBException("Error while fetching countries data : " + e.getMessage());
        }
        return countries;
    }

    public Map<Integer, City> getAllCitiesData(Map<Integer, Country> countries) throws DBException {
        Map<Integer, City> cities = new HashMap<>();

        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM CITY");

            while (rs.next()) {
                City city = new City();

                city.id = rs.getInt("id");
                city.name = rs.getString("name");
                city.country = countries.get(rs.getInt("country_id"));
                Date creationDate = rs.getDate("creation_date");
                if (creationDate != null) {
                    city.creationDate = creationDate.toLocalDate();
                }
                city.economicGrowth = rs.getFloat("economic_growth");
                city.poverty = rs.getFloat("poverty");
                city.population = rs.getLong("population");
                city.unemployment = rs.getFloat("unemployment");
                city.gini = rs.getFloat("gini");
                city.inflation = rs.getFloat("inflation");
                city.populationDensity = rs.getFloat("population_density");

                cities.put(city.id, city);
            }

        } catch (SQLException e) {
            throw new DBException("Error while fetching cities data : " + e.getMessage());
        }
        return cities;
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
}
