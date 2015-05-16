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

            Collection<Country> countries = DataCollector.collectData();
            System.out.println(String.format("Collected %d countries", countries.size()));

//            for (Country c : countries.values()) {
//                if (c.name.startsWith("Jin dyn")) {
//                    System.out.println(c.name);
//                }
//            }

            //Map<String, City> cities = CityData.collectCities(countries);
            //System.out.println(String.format("Collected %d cities", cities.size()));

//            for (City c : cities.values()) {
//                if (c.country == null || c.country.id == 0) {
//                    assert false;
//                }
//            }

            System.out.println("Uploading ...");

            //connection.uploadCountries(countries.values());
            //connection.uploadCities(cities.values());
            assert countries.size() == connection.getCountOfCountries();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
    }
}