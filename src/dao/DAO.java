package dao;

import collect_data.DataCollector;
import db.DBConnection;
import db.DBException;
import utils.DBUser;

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

    public Collection<String> getCountries() throws DAOException {
        try {
            return connection.getAllCountries();
        } catch (DBException e) {
            throw new DAOException("Could not get countries: " + e.getMessage());
        }
    }

    /**
     * Return a set of size count with random countriess
     */
    public Collection<String> getRandomCountries(int count) throws DAOException {
        try {
            return connection.getRandomCountries(count);
        } catch (DBException e) {
            throw new DAOException("Could not get random countries: " + e.getMessage());
        }
    }


//    public Collection<String> getCities() {
//
//    }
}
