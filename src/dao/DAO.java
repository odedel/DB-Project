package dao;

import collect_data.DataCollector;
import db.DBConnection;
import db.DBException;
import utils.DBUser;
import utils.DataNotFoundException;
import utils.EntityNotFound;
import utils.IDName;

import javax.xml.crypto.Data;
import java.sql.Date;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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

    /* --- Entity existence validation --- */

    public Boolean checkIfEntityExists(String entityType, int id) throws DAOException {
        try {
            int answer = connection.getCountOf(entityType, id);
            return answer != 0;
        } catch (DBException e) {
            throw new DAOException("Could not fetch data: " + e.getMessage());
        }
    }

    public Boolean checkIfEntityExists(String entityType, List<Integer> id) throws DAOException {
        try {
            List<Integer> answer = connection.getCountOf(entityType, id);
            return (answer.size() == id.size());
        } catch (DBException e) {
            throw new DAOException("Could not fetch data: " + e.getMessage());
        }
    }

    private void validateUserExists(int userID) throws EntityNotFound, DAOException {
        if (!checkIfEntityExists("user", userID))
            throw new EntityNotFound(String.format("User ID %s does not exists", userID));
    }

    private void validateCountryExists(int countryID) throws EntityNotFound, DAOException {
        if (!checkIfEntityExists("country", countryID)) {
            throw new EntityNotFound(String.format("Country ID %s does not exists", countryID));
        }
    }
    private void validateCountryExists(List<Integer> countryIDList) throws EntityNotFound, DAOException {
        if (!checkIfEntityExists("country", countryIDList)) {
            throw new EntityNotFound("One of the countries does not exists");
        }
    }

    private void validateCityExists(int cityID) throws EntityNotFound, DAOException {
        if (!checkIfEntityExists("city", cityID)) {
            throw new EntityNotFound(String.format("City ID %s does not exists", cityID));
        }
    }

    private void validatePersonExists(int personID) throws EntityNotFound, DAOException {
        if (!checkIfEntityExists("person", personID)) {
            throw new EntityNotFound(String.format("Person ID %s does not exists", personID));
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

    public int getUserID(String name) throws DAOException, DataNotFoundException {
        try {
            Collection<String> result = connection.getUserID(name);
            if (result.isEmpty()) {
                throw new DataNotFoundException("Could not find user " + name);
            }
            return Integer.parseInt(result.iterator().next());
        } catch (DBException e) {
            throw new DAOException("Could not fetch user ID: " + e.getMessage());
        }
    }

    public String getUserName(int id) throws DAOException, EntityNotFound {
        try {
            validateUserExists(id);
            return connection.getUserName(id).iterator().next();
        } catch (DBException e) {
            throw new DAOException("Could not fetch data: " + e.getMessage());
        }
    }


    public void setUserAnsweredCorrectly(int userID) throws DAOException, EntityNotFound {
        try {
            validateUserExists(userID);
            connection.setUserAnsweredCorrectly(userID,
                    connection.getUserAnsweredCorrectly(userID) + 1);
        } catch (DBException e) {
            throw new DAOException("Could not add answer to user");
        }
    }

    public void setUserAnsweredWrong(int userID) throws DAOException, EntityNotFound {
        try {
            validateUserExists(userID);
            connection.setUserAnsweredWrong(userID,
                    connection.getUserAnsweredWrong(userID) + 1);
        } catch (DBException e) {
            throw new DAOException("Could not add answer to user");
        }
    }

    public void setUserStartedNewGame(int userID) throws DAOException, EntityNotFound {
        try {
            validateUserExists(userID);
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
    public int getID(String entityType, String name) throws DAOException, DataNotFoundException {
        try {
            if (connection.getCountOf(entityType, name) > 0) {
                return connection.getEntityID(entityType, name);
            }
            throw new DataNotFoundException(String.format("%s with name %s does not Exists", entityType, name));
        } catch (DBException e) {
            throw new DAOException("Could not fetch country ID: " + e.getMessage());
        }
    }

    /* --- Countries --- */

    public Collection<IDName> getAllCountries() throws DAOException {
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
    public int getNumberOfPeopleInCountry(int countryID) throws DAOException, DataNotFoundException, EntityNotFound {
        try {
            validateCountryExists(countryID);
            int numberOfPeople = connection.getNumberOfPeopleInCountryOrderDescResult(countryID);
            if (numberOfPeople > 0)
                return numberOfPeople;
            throw new DataNotFoundException("Data is not found in DB");
        } catch (DBException e) {
            throw new DAOException("Could not fetch data: " + e.getMessage());
        }
    }

    /* Which country is the most populated */
    /**
     * Returns the id of the most populated country among the list.
     *
     * NOTE: You should check first that the countries has a populated data. do this with "getNumberOfPeopleInCountry".
     */
    public int getMostPopulatedCountry(List<Integer> countryIDList) throws DAOException, DataNotFoundException, EntityNotFound {
        try {
            validateCountryExists(countryIDList);
            return connection.getMostPopulatedCountry(countryIDList);
        } catch (DBException e) {
            throw new DAOException("Could not fetch data: " + e.getMessage());
        }
    }

    /* Which country is the least populated */
    /**
     * Returns the id of the least populated country among the list.
     *
     * NOTE: You should check first that the country has a populated data. do this with "getNumberOfPeopleInCountry".
     */
    public int getLeastPopulatedCountry(List<Integer> countryIDList) throws DAOException, DataNotFoundException, EntityNotFound {
        try {
            validateCountryExists(countryIDList);
            return connection.getLeastPopulatedCountry(countryIDList);
        } catch (DBException e) {
            throw new DAOException("Could not fetch data: " + e.getMessage());
        }
    }

    /* Which country is more populated than X? */
    /* Which country is less populated than X? */
    /**
     * NOTE: you should check first that the country has a population data.
     */
    public List<Integer> getCountryThatIsMorePopulatedThan(int countryID, int count) throws DAOException, EntityNotFound, DataNotFoundException {
        validateCountryExists(countryID);
        try {
            List<Integer> answer = connection.getCountryThatIsMorePopulatedThan(countryID, count);
            if(answer.size() == count) {
                return answer;
            }
            throw new DataNotFoundException(String.format("Can not find %s countries that are more populated than %s", count, countryID));
        } catch (DBException e) {
            throw new DAOException("Could not fetch data: " + e.getMessage());
        }
    }

    /**
     * NOTE: you should check first that the country has a population data.
     */
    public List<Integer> getCountryThatIsLessPopulatedThan(int countryID, int count) throws DAOException, EntityNotFound, DataNotFoundException {
        validateCountryExists(countryID);
        try {
            List<Integer> answer = connection.getCountryThatIsLessPopulatedThan(countryID, count);
            if(answer.size() == count) {
                return answer;
            }
            throw new DataNotFoundException(String.format("Can not find %s countries that are mote populated than %s", count, countryID));
        } catch (DBException e) {
            throw new DAOException("Could not fetch data: " + e.getMessage());
        }
    }

    /* When does X created? */
    public Date getCreationDate(int countryID) throws DAOException, DataNotFoundException, EntityNotFound {
        validateCountryExists(countryID);

        try {
            Date creation_date = connection.getCountryCreationDate(countryID);
            if (creation_date != null) {
                return creation_date;
            }
            throw new DataNotFoundException(String.format("Creation date of %s does not in DB", countryID));
        } catch (DBException e) {
            throw new DAOException("Could not fetch data: " + e.getMessage());
        }
    }

    /* Which country is the oldest? */
    /** Note: You should validate that the country has a creation date */
    public int getOldestCountry(List<Integer> countriesList) throws DAOException, EntityNotFound {
        validateCountryExists(countriesList);

        try {
            return connection.getTheOldestCountry(countriesList);
        } catch (DBException e) {
            throw new DAOException("Could not fetch data: " + e.getMessage());
        }
    }

    /* Which country Created before X but after Y?      Note: should first get the answers and than ask the question */
    public int getCountryCreatedBetween(int afterCountry, int beforeCountry) throws DAOException, EntityNotFound, DataNotFoundException {
        validateCountryExists(afterCountry);
        validateCountryExists(beforeCountry);

        try {
            Integer result = connection.getCountryCreatedBetween(afterCountry, beforeCountry);
            if (result == null) {
                throw new DataNotFoundException("Can not find entity between countries");
            }
            return result;
        } catch (DBException e) {
            throw new DAOException("Could not fetch data: " + e.getMessage());
        }
    }

    /* --- Cities --- */

    /* Which city is in X? */
    /* Which city is not in X? */
    /* Which city is different? */
    public Collection<IDName> getRandomCitiesByCountry(int country_id, int count) throws DAOException, DataNotFoundException, EntityNotFound {
        validateCountryExists(country_id);
        try {
            Collection<IDName> answer = connection.getCities(country_id, count);
            if(answer.size() != count) {
                throw new DataNotFoundException(String.format("Can not find %s cities in %s", count, country_id));
            }
            return answer;
        } catch (DBException e) {
            throw new DAOException("Could not get random cities: " + e.getMessage());
        }
    }
    public Collection<IDName> getRansomCitiesNotInCountry(int country_id, int count) throws DAOException, DataNotFoundException, EntityNotFound {
        validateCountryExists(country_id);
        try {
            Collection<IDName> answer = connection.getCitiesNotIn(country_id, count);
            if (answer.size() != count) {
                throw new DataNotFoundException(String.format("Can not find %s cities that are not in %s", count, country_id));
            }
            return answer;
        } catch (DBException e) {
            throw new DAOException("Could not fetch data: " + e.getMessage());
        }
    }

    /* What is the oldest city in X? */
    public IDName getOldestCity(int country_id) throws DAOException, DataNotFoundException, EntityNotFound {
        validateCountryExists(country_id);
        try {
            IDName answer = connection.getOldestCity(country_id);
            if (answer == null) {
                throw new DataNotFoundException("Can not find the oldest city in " + country_id);
            }
            return answer;
        } catch (DBException e) {
            throw new DAOException("Could not fetch data: " + e.getMessage());
        }
    }

    /* Which city is older then X? */
    /* Which city is newer then X? */
    public Collection<IDName> getOlderCityThan(int city_id, int count) throws DAOException, EntityNotFound, DataNotFoundException {
        validateCityExists(city_id);
        try {
            Collection<IDName> answer = connection.getOlderCityThan(city_id, count);
            if (answer.size() != count) {
                throw new DataNotFoundException(String.format("Can not find %s cities that are older than %s", count, city_id));
            }
            return answer;
        } catch (DBException e) {
            throw new DAOException("Could not fetch data: " + e.getMessage());
        }
    }

    /* Which city is older then X but in the same country? */
    public Collection<IDName> getOlderCityThanInTheSameCountry(int city_id, int count) throws DAOException, EntityNotFound, DataNotFoundException {
        validateCityExists(city_id);
        try {
            Collection<IDName> answer = connection.getOlderCityThanInTheSameCountry(city_id, count);
            if (answer.size() != count) {
                throw new DataNotFoundException(String.format("Can not find %s cities that are older than %s", count, city_id));
            }
            return answer;
        } catch (DBException e) {
            throw new DAOException("Could not fetch data: " + e.getMessage());
        }
    }


    /* --- Persons --- */

    /* Which person born in COUNTRY_ID? */
    /* Which person lives in other country than the other three? */
    public Collection<IDName> getRandomPersonsBornInCountry(int country_id, int count) throws DAOException, EntityNotFound, DataNotFoundException {
        validateCountryExists(country_id);
        try {
            Collection<IDName> result = connection.getPersonsByBirthCountry(country_id, count);
            if (result.size() != count) {
                throw new DataNotFoundException(String.format("Could not find %s persons that lives in %s", count, country_id));
            }
            return result;
        } catch (DBException e) {
            throw new DAOException("Could not get random persons: " + e.getMessage());
        }
    }
    public Collection<IDName> getRandomPersonsNotBornInCountry(int country_id, int count) throws DAOException, EntityNotFound, DataNotFoundException {
        validateCountryExists(country_id);
        try {
            Collection<IDName> result = connection.getPersonsByNotBirthCountry(country_id, count);
            if (result.size() != count) {
                throw new DataNotFoundException(String.format("Could not find %s persons that lives in %s", count, country_id));
            }
            return result;
        } catch (DBException e) {
            throw new DAOException("Could not get random persons: " + e.getMessage());
        }
    }

    /* Where does X born? (CITIES) */
    public IDName getBirthPlace(int person_id) throws DAOException, DataNotFoundException, EntityNotFound {
        validatePersonExists(person_id);
        try {
            IDName result = connection.getBirthPlace(person_id);
            if (result == null) {
                throw new DataNotFoundException(String.format("%s does not have birth place", person_id));
            }
            return result;
        } catch (DBException e) {
            throw new DAOException("Could not fetch birth place: " + e.getMessage());
        }
    }
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
