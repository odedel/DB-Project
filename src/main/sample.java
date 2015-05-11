package main;

import db.DBConnection;
import db.DBException;
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

//            Map<String, Country> countries = CountryData.collectCountries();

            Map<String, Country> countries = new HashMap<>();
            Country c = new Country();
            c.name = "A";
            countries.put("a", c);
            c = new Country();
            c.name = "B";
            countries.put("b", c);

            System.out.println(String.format("Collected %d countries", countries.size()));

            connection.uploadCountries(countries.values());
            assert countries.size() == connection.getCountOfCountries();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
    }
}