package dao;

import collect_data.DataCollector;
import db.DBConnection;
import db.DBException;
import utils.DBUser;
import utils.DataNotFoundException;
import utils.IDName;
import utils.IntegrityException;

import java.net.IDN;
import java.util.Collection;
import java.util.LinkedList;

public class DAO {

    private DBConnection connection;

    public DAO() {
        connection = new DBConnection();
    }

    public void connect(DBUser user) throws DAOException {
        try {
            connection.connect(user);
        } catch (DBException e) {
            throw new DAOException("Could not connect to DB: " + e.getMessage());
        }
    }

    public void disconnect() {
        try {
            if (connection.isConnected()) {
                connection.disconnect();
            }
        } catch (DBException e) {
        }
    }

    public void deleteDB() throws DAOException {
        try {
            connection.deleteData();
        } catch (DBException e) {
            throw new DAOException("Could not delete data from DB: " + e.getMessage());
        }
    }

    /* --- Upload data from Yago --- */

    public void uploadDataCollector(DataCollector dataCollector) throws DAOException {
        try {
            connection.uploadCountries(new LinkedList<>(dataCollector.getCountries()));
            connection.uploadCities(new LinkedList<>(dataCollector.getCities()));
            connection.uploadUniversities(new LinkedList<>(dataCollector.getUniversities()));
            connection.uploadBusinesses(new LinkedList<>(dataCollector.getBusinesses()));
            connection.uploadPersons(new LinkedList<>(dataCollector.getPersons()));
            connection.uploadArtifacts(new LinkedList<>(dataCollector.getArtifacts()));
        } catch (DBException e) {
            throw new DAOException("Could not upload data to DB: " + e.getMessage());
        }
    }

    /* --- Users --- */

    /**
     * @return user id
     */
    public int createUser(String name) throws DAOException {
        try {
            return connection.createUser(name);
        } catch (DBException e) {
            throw new DAOException("Could not create user: " + e.getMessage());
        }
    }

    public int getUserID(String name) throws DAOException, IntegrityException {
        try {
            return connection.getUserID(name);
        } catch (DBException e) {
            throw new DAOException("Could not fetch user ID: " + e.getMessage());
        }
    }

    public void setUserAnsweredCorrectly(int userID) throws DAOException {
        try {
            connection.setUserAnsweredCorrectly(userID,
                    connection.getUserAnsweredCorrectly(userID) + 1);
        } catch (DBException e) {
            throw new DAOException("Could not add answer to user");
        }
    }

    public void setUserAnsweredWrong(int userID) throws DAOException {
        try {
            connection.setUserAnsweredWrong(userID,
                    connection.getUserAnsweredWrong(userID) + 1);
        } catch (DBException e) {
            throw new DAOException("Could not add answer to user");
        }
    }

    public void setUserStartedNewGame(int userID) throws DAOException {
        try {
            connection.setUserStartedNewGame(userID,
                    connection.setUserStartedNewGame(userID) + 1);
        } catch (DBException e) {
            throw new DAOException("Could not add answer to user");
        }
    }

    /* --- General Entities --- */

    /**
     * Returns entity's ID.
     * Example: getID("country", "Israel")
     */
    public int getID(String entity_type, String name) throws DAOException {
        try {
            return connection.getEntityID(entity_type, name);
        } catch (DBException e) {
            throw new DAOException("Could not fetch country ID: " + e.getMessage());
        }
    }

    /* --- Countries --- */

    public Collection<IDName> getCountries() throws DAOException {
        try {
            return connection.getAllCountries();
        } catch (DBException e) {
            throw new DAOException("Could not get countries: " + e.getMessage());
        }
    }

    /**
     * Return a set of size count with random countries
     */
    public Collection<IDName> getRandomCountries(int count) throws DAOException {
        try {
            return connection.getRandomCountries(count);
        } catch (DBException e) {
            throw new DAOException("Could not get random countries: " + e.getMessage());
        }
    }

    /* How many people lives in X? */
    /* Which country is the most/the least populated? */
    /* Which country is more populated than X?   NOTE: First ask for 4 answers, just after ask the question */
    public long getNumberOfPeopleInCountry(int country_id) throws DAOException, DataNotFoundException {
        try {
            long numberOfPeople = connection.getNumberOfPeopleInCountry(country_id);
            if (numberOfPeople > 0)
                return numberOfPeople;
            throw new DataNotFoundException("Data is not found in the DB");
        } catch (DBException e) {
            throw new DAOException("Could not fetch data: " + e.getMessage());
        }
    }
//
//    /* When does X created? */
//    /* Which country is the oldest/newest? */
//    /* Which country Created before X but after Y?      Note: should first get the answers and than ask the question */
//    public Date getCreationDate(int country_id) throws DAOException {
//        try {
//            connection.getCountryCreationDate(country_id);
//        } catch (DBException e) {
//            throw new DAOException("Could not fetch data: " + e.getMessage());
//        }
//    }
//
//
//    /* --- Cities --- */
//
//    /* Which country is in X? */
//    /* Which country is not in X? */
//    /* Which city is different? */
//    public Collection<IDName> getRandomCitiesByCountry(int country_id, int count) throws DAOException {
//        try {
//            return connection.getCities(country_id, count);
//        } catch (DBException e) {
//            throw new DAOException("Could not get random cities: " + e.getMessage());
//        }
//    }
//    public Collection<IDName> getRansomCitiesNotInCountry(int country_id, int count) throws DAOException {
//        try {
//            return connection.getCitiesNotIn(country_id, count);
//        } catch (DBException e) {
//            throw new DAOException("Could not fetch data: " + e.getMessage());
//        }
//    }
//
//    /* What is the oldest city in X? */
//    public IDName getOldestCity(int country_id) throws DAOException {
//        try {
//            return connection.getOldestCity(country_id);
//        } catch (DBException e) {
//            throw new DAOException("Could not fetch data: " + e.getMessage());
//        }
//    }
//
//    /* Which city is older then X? */
//    /* Which city is newer then X? */
//    public Collection<IDName> getOlderCityThan(int city_id, int count) throws DAOException {
//        try {
//            return connection.getOlderCityThan(city_id, count);
//        } catch (DBException e) {
//            throw new DAOException("Could not fetch data: " + e.getMessage());
//        }
//    }
//    public Collection<IDName> getNewerCityThan(int city_id, int count) throws DAOException {
//        try {
//            return connection.getNewerCityThan(city_id, count);
//        } catch (DBException e) {
//            throw new DAOException("Could not fetch data: " + e.getMessage());
//        }
//    }
//
//
//    /* --- Persons --- */
//
//    /* Which person lives in COUNTRY_ID? */
//    /* Which person lives in other country than the other three? */
//    /* In Which country does person X lives? */
//    public Collection<IDName> getRandomPersonsByCountry(int country_id, int count) throws DAOException {
//        try {
//            return conection.getPersons(country_id, count);
//        } catch (DBException e) {
//            throw new DAOException("Could not get random persons: " + e.getMessage());
//        }
//    }
//    public Collection<IDName> getRandomPersonsNotInCountry(int country_id, int count) throws DAOException {
//        try {
//            return connection.getPersonsNotIN(country_id, count);
//        } catch (DBException e) {
//            throw new DAOException("Cold not get random persons: " + e.getMessage());
//        }
//    }
//
//    /* Where does X born?   NOTE: X may not has birth place, you should ask again and again until there is */
//    public IDName getBirthPlace(int person_id) throws DAOException {
//        try {
//            return connection.getBirthPlace(person_id);
//        } catch (DBException e) {
//            throw new DAOException("Could not fetch birth place: " + e.getMessage());
//        }
//    }
//
//    /* Which person was born in the same place as X? */
//    /* Which person was not born in the same place as X? */
//    public Collection<IDName> getPersonsBornInSamePlace(int person_id, int count) throws DAOException {
//        try {
//            return connection.getPersonsBornInSamePlace(person_id, count);
//        } catch (DBException e) {
//            throw new DAOException("Could not fetch data: " + e.getMessage());
//        }
//    }
//    public Collection<IDName> getPersonsNotBornInSamePlace(int person_id, int count) throws DAOException {
//        try {
//            return connection.getPersonsNotBornInSamePlace(person_id, count);
//        } catch (DBException e) {
//            throw new DAOException("Could not fetch data: " + e.getMessage());
//        }
//    }
//
//    /* When X was born? */
//    /* Who was born first/last? */
//    public Date getPersonBirthDate(int person_id) throws DAOException {
//        try {
//            return connection.getBirthDate(person_id);
//        } catch (DBException e) {
//            throw new DAOException("Could not fetch data: " + e.getMessage());
//        }
//    }
}
