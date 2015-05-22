package main;

import db.DBConnection;
import db.DBException;
import db.User;
import main.data.*;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

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
        connection.connect(User.MODIFIER);

        connection.deleteData();
        assert connection.getCountOfCountries() == 0;

        DataCollector dataCollector = new DataCollector();
        dataCollector.collectData();
        Collection<Country> countries = dataCollector.getCountries();
        Collection<City> cities = dataCollector.getCities();
        Collection<University> universities = dataCollector.getUniversities();
        Collection<Politician> politicians = dataCollector.getPoliticians();
        Collection<Artifact> artifacts = dataCollector.getArtifacts();
        Collection<Business> businesses = dataCollector.getBusinesses();
        Collection<Creator> creators = dataCollector.getCreators();

        System.out.println();
        System.out.println(String.format("Collected %d countries", countries.size()));
        System.out.println(String.format("Collected %d cities", cities.size()));
        System.out.println(String.format("Collected %d universities", universities.size()));
        System.out.println(String.format("Collected %d politicians", politicians.size()));
        System.out.println(String.format("Collected %d artifacts", artifacts.size()));
        System.out.println(String.format("Collected %d businesses", businesses.size()));
        System.out.println(String.format("Collected %d creators", creators.size()));

        System.out.println("Uploading ...");

        connection.uploadCountries(new LinkedList<>(countries));
        connection.uploadCities(new LinkedList<>(cities));
        connection.uploadUniversities(new LinkedList<>(universities));
        connection.uploadPoliticians(new LinkedList<>(politicians));
        connection.uploadBusinesses(new LinkedList<>(businesses));
        connection.uploadCreators(new LinkedList<>(creators));
        connection.uploadArtifacts(new LinkedList<>(artifacts));
    }

    public static void queryData(DBConnection connection) throws DBException {
        connection.connect(User.PLAYER);

        Map<Integer, Country> countries = connection.getAllCountriesData();
        Map<Integer, City> cities = connection.getAllCitiesData(countries);
        assert countries.size() == connection.getCountOfCountries();
        assert cities.size() == connection.getCountOfCities();
    }
}