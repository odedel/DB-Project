package dao;

import collect_data.DataCollector;
import db.DBConnection;
import db.DBException;
import utils.DBUser;
import utils.IntegrityException;

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

    public Collection<String> getCountries() throws DAOException {
        try {
            return connection.getAllCountries();
        } catch (DBException e) {
            throw new DAOException("Could not get countries: " + e.getMessage());
        }
    }

    /**
     * Return a set of size count with random countries
     */
    public Collection<String> getRandomCountries(int count) throws DAOException {
        try {
            return connection.getRandomCountries(count);
        } catch (DBException e) {
            throw new DAOException("Could not get random countries: " + e.getMessage());
        }
    }

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


    /**
     * Example: getRandomCitiesByCountry(*COUNTRY-ID*, 4, [name, creation_date, economic_growth])
     * Returns collection of 4 elements, each is a list where the first value represents city's name, second is the creation date...
     */
    public Collection<Integer> getRandomCitiesByCountry(int country_id, int count) throws DAOException {
        try {
            return connection.getCities(country_id, count);
        } catch (DBException e) {
            throw new DAOException("Could not get random cities: " + e.getMessage());
        }
    }

}
