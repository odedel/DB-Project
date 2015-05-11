package main;

import db.DBConnection;
import db.DBException;
import main.data.City;
import main.data.CityData;
import main.data.Country;
import main.data.CountryData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class Sample {

    public static void main(String args[]) {
        DBConnection connection = new DBConnection();
        try {
            connection.connect();

            connection.deleteData();
            assert connection.getCountOfCountries() == 0;

            Map<String, Country> countries = CountryData.collectCountries();
            System.out.println(String.format("Collected %d countries", countries.size()));

            Map<String, City> cities = CityData.collectCities(countries);
            System.out.println(String.format("Collected %d cities", cities.size()));

            for (City c : cities.values()) {
                if (c.country == null || c.country.id == 0) {
                    assert false;
                }
            }

            System.out.println("Uploading ...");

            connection.uploadCountries(countries.values());
            connection.uploadCities(cities.values());
            assert countries.size() == connection.getCountOfCountries();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
    }
}