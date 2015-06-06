package db;

import collect_data.entities.*;
import collect_data.util.Utils;

import java.sql.*;
import java.util.*;

public class DBConnection {

    private Connection conn;

    /**
     * Opens new connection to the db and initialize conn with it.
     * Throws DBException if something bad happened.
     * */
    public void connect(DBUser user) throws DBException {

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

    public boolean isConnected() throws DBException {
        try {
            return !conn.isClosed();
        } catch (SQLException e) {
            throw new DBException("Unable to check connection status : " + e.getMessage());
        }
    }

    public void uploadCountries(List<Country> countries) throws DBException {
        try (PreparedStatement pstmt = conn
                .prepareStatement("INSERT INTO country(name, creation_date, economic_growth, poverty, population, unemployment, gini, inflation, population_density) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {

            conn.setAutoCommit(false);

            List<Entity> batchingList = new LinkedList<>();
            for (Country country : countries) {
                pstmt.setString(1, country.getName());
                pstmt.setDate(2, Utils.localDateToDate(country.getCreationDate()));
                pstmt.setFloat(3, country.getEconomicGrowth());
                pstmt.setFloat(4, country.getPoverty());
                pstmt.setLong(5, country.getPopulation());
                pstmt.setFloat(6, country.getUnemployment());
                pstmt.setFloat(7, country.getGini());
                pstmt.setFloat(8, country.getInflation());
                pstmt.setFloat(9, country.getPopulationDensity());

                addBatchAndExecuteIfNeeded(pstmt, batchingList, country, true);
            }
            executeBatch(pstmt, batchingList);

            conn.commit();
        } catch (SQLException e) {
            throw new DBException("Error while uploading countries : " + e.getMessage());
        } finally {
            safelySetAutoCommit();
        }
    }

    public void uploadCities(List<City> cities) throws DBException {
        try (PreparedStatement pstmt = conn
                .prepareStatement("INSERT INTO city(name, country_id, creation_date, economic_growth, poverty, population, unemployment, gini, inflation, population_density) " +
                                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {

            conn.setAutoCommit(false);

            List<Entity> batchingList = new LinkedList<>();
            for (City city : cities) {
                pstmt.setString(1, city.getName());
                pstmt.setInt(2, city.getCountry().getId());
                pstmt.setDate(3, Utils.localDateToDate(city.getCreationDate()));
                pstmt.setFloat(4, city.getEconomicGrowth());
                pstmt.setFloat(5, city.getPoverty());
                pstmt.setLong(6, city.getPopulation());
                pstmt.setFloat(7, city.getUnemployment());
                pstmt.setFloat(8, city.getGini());
                pstmt.setFloat(9, city.getInflation());
                pstmt.setFloat(10, city.getPopulationDensity());

                addBatchAndExecuteIfNeeded(pstmt, batchingList, city, true);
            }
            executeBatch(pstmt, batchingList);

            conn.commit();
        } catch (SQLException e) {
            throw new DBException("Error while uploading countries : " + e.getMessage());
        } finally {
            safelySetAutoCommit();
        }
    }

    public void uploadUniversities(List<University> universities) throws DBException {
        try {
            conn.setAutoCommit(false);

            uploadUniversitiesEntities(universities);
            uploadUniversityCountryRelation(universities);
            uploadUniversityCityRelation(universities);

            conn.commit();
        } catch (SQLException e) {
            throw new DBException("Error while uploading universities : " + e.getMessage());
        } finally {
            safelySetAutoCommit();
        }
    }

    private void uploadUniversitiesEntities(List<University> universities) throws DBException {
        try (PreparedStatement pstmt = conn
                .prepareStatement("INSERT INTO university(name, creation_date) " +
                                "VALUES(?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {

            List<Entity> batchingList = new LinkedList<>();
            for (University university : universities) {
                pstmt.setString(1, university.getName());
                pstmt.setDate(2, Utils.localDateToDate(university.getCreationDate()));

                addBatchAndExecuteIfNeeded(pstmt, batchingList, university, true);
            }
            executeBatch(pstmt, batchingList);
        } catch (SQLException e) {
            throw new DBException("Error while uploading universities : " + e.getMessage());
        }
    }

    private void uploadUniversityCountryRelation(List<University> universities) throws DBException {
        try (PreparedStatement pstmt = conn
                .prepareStatement("INSERT INTO University_Country_Relation(university_id, country_id) " +
                        "VALUES(?, ?)")) {

            List<Entity> batchingList = new LinkedList<>();
            for (University university : universities) {
                for (Country country : university.getCountries()) {
                    createRelation(pstmt, university, country);
                    addBatchAndExecuteIfNeeded(pstmt, batchingList, country, false);
                }
            }
            executeBatch(pstmt, null);
        } catch (SQLException e) {
            throw new DBException("Error while uploading politicians : " + e.getMessage());
        }
    }

    private void uploadUniversityCityRelation(List<University> universities) throws DBException {
        try (PreparedStatement pstmt = conn
                .prepareStatement("INSERT INTO University_City_Relation(university_id, city_id) " +
                        "VALUES(?, ?)")) {

            List<Entity> batchingList = new LinkedList<>();
            for (University university : universities) {
                for (City city : university.getCities()) {
                    createRelation(pstmt, university, city);
                    addBatchAndExecuteIfNeeded(pstmt, batchingList, university, false);
                }
            }
            executeBatch(pstmt, null);
        } catch (SQLException e) {
            throw new DBException("Error while uploading politicians : " + e.getMessage());
        }
    }

    public void uploadPersons(List<Person> persons) throws DBException {
        try {
            conn.setAutoCommit(false);

            uploadPersonsEntities(persons);
            uploadPoliticianUniversityRelation(persons);
            uploadPersonsPoliticianOfCountryRelation(persons);
            uploadBusinessCreatorRelation(persons);

            conn.commit();
        } catch (SQLException e) {
            throw new DBException("Error while uploading persons : " + e.getMessage());
        } finally {
            safelySetAutoCommit();
        }
    }

    private void uploadPersonsEntities(List<Person> persons) throws DBException {
        try (PreparedStatement pstmt = conn
                .prepareStatement("INSERT INTO person(name, birth_city_id, birth_date, death_city_id, death_date) " +
                                "VALUES(?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {

            List<Entity> batchingList = new LinkedList<>();
            for (Person person : persons) {
                pstmt.setString(1, person.getName());

                if (person.getBirthCity() != null) {
                    pstmt.setInt(2, person.getBirthCity().getId());
                } else {
                    pstmt.setNull(2, java.sql.Types.INTEGER);
                }
                pstmt.setDate(3, Utils.localDateToDate(person.getBirthDate()));

                if (person.getDeathCity() != null) {
                    pstmt.setInt(4, person.getDeathCity().getId());
                } else {
                    pstmt.setNull(4, java.sql.Types.INTEGER);
                }
                pstmt.setDate(5, Utils.localDateToDate(person.getDeathDate()));

                addBatchAndExecuteIfNeeded(pstmt, batchingList, person, true);
            }
            executeBatch(pstmt, batchingList);
        } catch (SQLException e) {
            throw new DBException("Error while uploading persons : " + e.getMessage());
        }
    }

    private void uploadPersonsPoliticianOfCountryRelation(List<Person> persons) throws DBException {
        try (PreparedStatement pstmt = conn
                .prepareStatement("INSERT INTO Person_Politician_Of_Country_Relation(country_id, politician_id) " +
                                "VALUES(?, ?)")) {

            List<Entity> batchingList = new LinkedList<>();
            for (Person person : persons) {
                for (Country country : person.getPoliticianOf()) {
                    createRelation(pstmt, country, person);
                    addBatchAndExecuteIfNeeded(pstmt, batchingList, country, false);
                }
            }
            executeBatch(pstmt, null);
        } catch (SQLException e) {
            throw new DBException("Error while uploading Persons-Politician-Of-Relation : " + e.getMessage());
        }
    }

    private void uploadPoliticianUniversityRelation(List<Person> persons) throws DBException {
        try (PreparedStatement pstmt = conn
                .prepareStatement("INSERT INTO University_Person_Relation(person_id, university_id) " +
                        "VALUES(?, ?)")) {

            List<Entity> batchingList = new LinkedList<>();
            for (Person person : persons) {
                for (University university : person.getUniversities()) {
                    createRelation(pstmt, person, university);
                     addBatchAndExecuteIfNeeded(pstmt, batchingList, university, false);
                }
            }
            executeBatch(pstmt, null);
        } catch (SQLException e) {
            throw new DBException("Error while uploading Person-University-Relation : " + e.getMessage());
        }
    }

    private void uploadBusinessCreatorRelation(List<Person> persons) throws DBException {
        try (PreparedStatement pstmt = conn
                .prepareStatement("INSERT INTO Business_Creator_Relation(creator_id, business_id) " +
                        "VALUES(?, ?)")) {

            List<Entity> batchingList = new LinkedList<>();
            for (Person person : persons) {
                for (Business business : person.getBusinesses()) {
                    createRelation(pstmt, person, business);
                    addBatchAndExecuteIfNeeded(pstmt, batchingList, business, false);
                }
            }
            executeBatch(pstmt, null);
        } catch (SQLException e) {
            throw new DBException("Error while uploading business-creator-relation : " + e.getMessage());
        }
    }

    private void uploadArtifactPersonRelation(List<Artifact> artifacts) throws DBException {
        try (PreparedStatement pstmt = conn
                .prepareStatement("INSERT INTO Artifact_Creator_Relation(creator_id, artifact_id) " +
                        "VALUES(?, ?)")) {

            List<Entity> batchingList = new LinkedList<>();
            for (Artifact artifact : artifacts) {
                for (Person creator : artifact.getCreators()) {
                    createRelation(pstmt, creator, artifact);
                    addBatchAndExecuteIfNeeded(pstmt, batchingList, creator, false);
                }
            }
            executeBatch(pstmt, null);
        } catch (SQLException e) {
            throw new DBException("Error while uploading creators : " + e.getMessage());
        }
    }

    public void uploadBusinesses(List<Business> businesses) throws DBException {
        try {
            conn.setAutoCommit(false);

            uploadBusinessesEntity(businesses);
            uploadBusinessCityRelation(businesses);
            uploadBusinessCountryRelation(businesses);

            conn.commit();
        } catch(SQLException e) {
            throw new DBException("Error while uploading businesses : " + e.getMessage());
        } finally {
            safelySetAutoCommit();
        }
    }

    private void uploadBusinessesEntity(List<Business> businesses) throws DBException {
        try (PreparedStatement pstmt = conn
                .prepareStatement("INSERT INTO business(name, creation_date, number_of_employees) " +
                                "VALUES(?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {

            List<Entity> batchingList = new LinkedList<>();
            for (Business business : businesses) {
                pstmt.setString(1, business.getName());
                pstmt.setDate(2, Utils.localDateToDate(business.getCreationDate()));
                pstmt.setLong(3, business.getNumberOfEmployees());

                addBatchAndExecuteIfNeeded(pstmt, batchingList, business, true);
            }
            executeBatch(pstmt, batchingList);
        } catch (SQLException e) {
            throw new DBException("Error while uploading creators : " + e.getMessage());
        }
    }

    private void uploadBusinessCityRelation(List<Business> businesses) throws DBException {
        try (PreparedStatement pstmt = conn
                .prepareStatement("INSERT INTO Business_City_Relation(business_id, city_id) " +
                        "VALUES(?, ?)")) {

            List<Entity> batchingList = new LinkedList<>();
            for (Business business : businesses) {
                for (City city : business.getCities()) {
                    createRelation(pstmt, business, city);
                    addBatchAndExecuteIfNeeded(pstmt, batchingList, city, false);
                }
            }
            executeBatch(pstmt, null);
        } catch (SQLException e) {
            throw new DBException("Error while uploading businesses : " + e.getMessage());
        }
    }

    private void uploadBusinessCountryRelation(List<Business> businesses) throws DBException {
        try (PreparedStatement pstmt = conn
                .prepareStatement("INSERT INTO Business_Country_Relation(country_id, business_id) " +
                        "VALUES(?, ?)")) {

            List<Entity> batchingList = new LinkedList<>();
            for (Business business : businesses) {
                for (Country country : business.getCountries()) {
                    createRelation(pstmt, country, business);
                    addBatchAndExecuteIfNeeded(pstmt, batchingList, country, false);
                }
            }
            executeBatch(pstmt, null);
        } catch (SQLException e) {
            throw new DBException("Error while uploading creators : " + e.getMessage());
        }
    }

    public void uploadArtifacts(List<Artifact> artifacts) throws DBException {
        try {
            conn.setAutoCommit(false);

            uploadArtifactsEntity(artifacts);
            uploadArtifactPersonRelation(artifacts);
            uploadBusinessArtifactRelation(artifacts);

            conn.commit();
        } catch(SQLException e) {
            throw new DBException("Error while uploading artifacts : " + e.getMessage());
        } finally {
            safelySetAutoCommit();
        }
    }

    private void uploadBusinessArtifactRelation(List<Artifact> artifacts) throws DBException {
        try (PreparedStatement pstmt = conn
                .prepareStatement("INSERT INTO Business_Artifact_Relation(business_id, artifact_id) " +
                        "VALUES(?, ?)")) {

            List<Entity> batchingList = new LinkedList<>();
            for (Artifact artifact : artifacts) {
                for (Business business : artifact.getBusinesses()) {
                    createRelation(pstmt, business, artifact);
                    addBatchAndExecuteIfNeeded(pstmt, batchingList, artifact, false);
                }
            }
            executeBatch(pstmt, null);
        } catch (SQLException e) {
            throw new DBException("Error while uploading businesses : " + e.getMessage());
        }
    }

    private void uploadArtifactsEntity(List<Artifact> artifacts) throws DBException {
        try (PreparedStatement pstmt = conn
                .prepareStatement("INSERT INTO artifact(name, creation_date) " +
                                "VALUES(?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {

            List<Entity> batchingList = new LinkedList<>();
            for (Artifact artifact : artifacts) {
                pstmt.setString(1, artifact.getName());
                pstmt.setDate(2, Utils.localDateToDate(artifact.getCreationDate()));
                addBatchAndExecuteIfNeeded(pstmt, batchingList, artifact, true);
            }
            executeBatch(pstmt, batchingList);
        } catch (SQLException e) {
            throw new DBException("Error while uploading artifacts : " + e.getMessage());
        }
    }

    /**
     * @return How many countries there are in the DB
     */
    public int getCountOfCountries() throws DBException {
        try (Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM country")) {
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new DBException("Error while counting countries : " + e.getMessage());
        }
    }

    public int getCountOfCities() throws DBException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM city")) {
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new DBException("Error while counting countries : " + e.getMessage());
        }
    }

//    public Map<Integer, Country> getAllCountriesData() throws DBException {
//        Map<Integer, Country> countries = new HashMap<>();
//
//        try (Statement stmt = conn.createStatement()) {
//            ResultSet rs = stmt.executeQuery("SELECT * FROM COUNTRY");
//
//            while (rs.next()) {
//                Country country = new Country();
//
//                country.id = rs.getInt("id");
//                country.name = rs.getString("name");
//
//                Date creationDate = rs.getDate("creation_date");
//                if (creationDate != null) {
//                    country.creationDate = creationDate.toLocalDate();
//                }
//                country.economicGrowth = rs.getFloat("economic_growth");
//                country.poverty = rs.getFloat("poverty");
//                country.population = rs.getLong("population");
//                country.unemployment = rs.getFloat("unemployment");
//                country.gini = rs.getFloat("gini");
//                country.inflation = rs.getFloat("inflation");
//                country.populationDensity = rs.getFloat("population_density");
//
//                countries.put(country.id, country);
//            }
//
//        } catch (SQLException e) {
//            throw new DBException("Error while fetching countries collect_data : " + e.getMessage());
//        }
//        return countries;
//    }

//    public Map<Integer, City> getAllCitiesData(Map<Integer, Country> countries) throws DBException {
//        Map<Integer, City> cities = new HashMap<>();
//
//        try (Statement stmt = conn.createStatement()) {
//            ResultSet rs = stmt.executeQuery("SELECT * FROM CITY");
//
//            while (rs.next()) {
//                City city = new City();
//
//                city.id = rs.getInt("id");
//                city.name = rs.getString("name");
//                city.country = countries.get(rs.getInt("country_id"));
//                Date creationDate = rs.getDate("creation_date");
//                if (creationDate != null) {
//                    city.creationDate = creationDate.toLocalDate();
//                }
//                city.economicGrowth = rs.getFloat("economic_growth");
//                city.poverty = rs.getFloat("poverty");
//                city.population = rs.getLong("population");
//                city.unemployment = rs.getFloat("unemployment");
//                city.gini = rs.getFloat("gini");
//                city.inflation = rs.getFloat("inflation");
//                city.populationDensity = rs.getFloat("population_density");
//
//                cities.put(city.id, city);
//            }
//
//        } catch (SQLException e) {
//            throw new DBException("Error while fetching cities collect_data : " + e.getMessage());
//        }
//        return cities;
//    }

    /**
     * Clears collect_data from DB.
     */
    public void deleteData() throws DBException {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM UNIVERSITY");
            stmt.executeUpdate("DELETE FROM CITY");
            stmt.executeUpdate("DELETE FROM BUSINESS");
            stmt.executeUpdate("DELETE FROM ARTIFACT");
            stmt.executeUpdate("DELETE FROM PERSON");
            stmt.executeUpdate("DELETE FROM country");
        } catch (SQLException e) {
            throw new DBException("Error while deleting collect_data from country : " + e.getMessage());
        }
    }

    private void createRelation(PreparedStatement pstmt, Entity entity1, Entity entity2) throws SQLException {
        pstmt.setInt(1, entity1.getId());
        pstmt.setInt(2, entity2.getId());
    }

    /**
     * Reads the generated keys and set the entity accordingly.
     */
    private void setIDsToEntities(PreparedStatement pstmt, List<? extends Entity> entities) throws DBException {
        try(ResultSet rs = pstmt.getGeneratedKeys()) {
            Iterator<? extends Entity> entityIterator = entities.iterator();
            while (rs.next()) {
                entityIterator.next().setId(rs.getInt(1));
            }
        } catch(SQLException e) {
            throw new DBException("Error while getting generated keys : " + e.getMessage());
        }
    }

    private void addBatchAndExecuteIfNeeded(PreparedStatement pstmt, List<Entity> batchingList, Entity entity, boolean setIDs) throws DBException {
        try {
            pstmt.addBatch();

            batchingList.add(entity);

            if (batchingList.size() % 10000 == 0) { // Execute once in 100000 adds
                if (setIDs) {
                    executeBatch(pstmt, batchingList);
                } else {
                    executeBatch(pstmt, null);
                }

                batchingList.clear();
            }
        } catch (SQLException e) {
            throw new DBException("Error while adding to batch : " + e.getMessage());
        }
    }

    private void executeBatch(PreparedStatement pstmt, List<? extends  Entity> entities) throws DBException {
        try {
            pstmt.executeBatch();

            if (entities != null) {
                setIDsToEntities(pstmt, entities);
            }
        } catch (SQLException e) {
            throw new DBException("Error while executing batch : " + e.getMessage());
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
