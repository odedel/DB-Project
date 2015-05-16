package main;

import db.DBConnection;
import main.data.Country;
import main.data.DataCollector;

import java.util.Collection;

class Sample {

    public static void main(String args[]) {
        DBConnection connection = new DBConnection();
        try {
            connection.connect();

            connection.deleteData();
            assert connection.getCountOfCountries() == 0;

            DataCollector dataCollector = new DataCollector();
            dataCollector.collectData();
            Collection<Country> countries = dataCollector.getCountries();
            System.out.println(String.format("Collected %d countries", countries.size()));

//            System.out.println("Uploading ...");

            //connection.uploadCountries(countries.values());
            //connection.uploadCities(cities.values());
//            assert countries.size() == connection.getCountOfCountries();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
    }
}