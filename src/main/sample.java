package main;

import db.DBConnection;
import db.DBException;
import db.DBUser;
import main.collect_data.DataCollector;
import main.collect_data.entities.*;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

class Sample {

    public static void main(String args[]) throws IOException {

        DBConnection connection = new DBConnection();
        try {
            insertData(connection);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
    }

    public static void insertData(DBConnection connection) throws DBException, IOException {
        connection.connect(DBUser.MODIFIER);

        connection.deleteData();
        assert connection.getCountOfCountries() == 0;

        DataCollector dataCollector = new DataCollector();
        dataCollector.collectData();
        Collection<Country> countries = dataCollector.getCountries();
        Collection<City> cities = dataCollector.getCities();
        Collection<University> universities = dataCollector.getUniversities();
        Collection<Artifact> artifacts = dataCollector.getArtifacts();
        Collection<Business> businesses = dataCollector.getBusinesses();
        Collection<Person> persons = dataCollector.getPersons();

        System.out.println();
        System.out.println(String.format("Collected %d countries", countries.size()));
        System.out.println(String.format("Collected %d cities", cities.size()));
        System.out.println(String.format("Collected %d universities", universities.size()));
        System.out.println(String.format("Collected %d persons", persons.size()));
        System.out.println(String.format("Collected %d artifacts", artifacts.size()));
        System.out.println(String.format("Collected %d businesses", businesses.size()));

        System.out.println("Uploading ...");

        connection.uploadCountries(new LinkedList<>(countries));
        connection.uploadCities(new LinkedList<>(cities));
        connection.uploadUniversities(new LinkedList<>(universities));
        connection.uploadBusinesses(new LinkedList<>(businesses));
        connection.uploadPersons(new LinkedList<>(persons));
        connection.uploadArtifacts(new LinkedList<>(artifacts));
    }

//    public static void queryData(DBConnection connection) throws DBException {
//        connection.connect(DBUser.PLAYER);
//
//        Map<Integer, Country> countries = connection.getAllCountriesData();
//        Map<Integer, City> cities = connection.getAllCitiesData(countries);
//        assert countries.size() == connection.getCountOfCountries();
//        assert cities.size() == connection.getCountOfCities();
//    }
}