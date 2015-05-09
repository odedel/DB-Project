package main;

import db.DBConnection;
import db.DBException;
import main.data.CountryData;

import java.util.Collection;

class Sample {

    public static void main(String args[]) {
        DBConnection connection = new DBConnection();
        try {
            connection.connect();

            connection.deleteData();
            assert connection.getCountOfCountries() == 0;

            Collection countries = CountryData.collectCountries();
            System.out.println(String.format("Collected %d countries", countries.size()));

            connection.uploadCountries(countries);
            assert countries.size() == connection.getCountOfCountries();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
    }
}