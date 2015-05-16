package main;

import db.DBConnection;
import db.DBException;
import db.User;
import main.data.City;
import main.data.Country;
import main.data.DataCollector;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

class Sample {

    public static void main(String args[]) {
        DBConnection connection = new DBConnection();
        try {
            connection.connect(User.PLAYER);

            Map<Integer, Country> countries = connection.getAllCountriesData();
            Map<Integer, City> cities = connection.getAllCitiesData(countries);
            assert countries.size() == connection.getCountOfCountries();
            assert cities.size() == connection.getCountOfCities();
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
        System.out.println(String.format("Collected %d countries", countries.size()));
        System.out.println(String.format("Collected %d cities", cities.size()));

        System.out.println("Uploading ...");
        connection.uploadCountries(new LinkedList<>(countries));
        connection.uploadCities(new LinkedList<>(cities));
        assert countries.size() == connection.getCountOfCountries();
    }
}