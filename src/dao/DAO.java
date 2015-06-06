package dao;

import collect_data.DataCollector;
import db.DBConnection;
import db.DBException;
import utils.DBUser;

import java.io.IOException;
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

    public void uploadDataCollector(DataCollector dataCollector) throws DBException, IOException {
        connection.uploadCountries(new LinkedList<>(dataCollector.getCountries()));
        connection.uploadCities(new LinkedList<>(dataCollector.getCities()));
        connection.uploadUniversities(new LinkedList<>(dataCollector.getUniversities()));
        connection.uploadBusinesses(new LinkedList<>(dataCollector.getBusinesses()));
        connection.uploadPersons(new LinkedList<>(dataCollector.getPersons()));
        connection.uploadArtifacts(new LinkedList<>(dataCollector.getArtifacts()));
    }
}
