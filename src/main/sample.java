package main;

import db.DBConnection;
import main.data.City;
import main.data.Country;
import main.data.DataCollector;

import java.util.Map;

class Sample {

    public static void main(String args[]) {
        DBConnection connection = new DBConnection();
        try {
            connection.connect();

            connection.deleteData();
            assert connection.getCountOfCountries() == 0;

            DataCollector dc = new DataCollector();

            dc.collectData();
            Map<String, Country> countries = dc.getCountries();
            System.out.println(String.format("Collected %d countries", countries.size()));

            Map<String, City> cities = dc.getCities();
            System.out.println(String.format("Collected %d cities", cities.size()));


            System.out.println("Uploading ...");

//            connection.uploadCountries(countries.values());
//            connection.uploadCities(cities.values());
//            assert countries.size() == connection.getCountOfCountries();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
    }
}