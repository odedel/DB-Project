package main;

import collect_data.DataCollector;
import collect_data.entities.*;
import db.DBConnection;
import db.DBException;
import db.DBUser;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

public class UploadDataFromYagoMain {

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
        int universityCity = 0;
        int universityCountry = 0;
        for (University u: universities) {
            universityCity += u.cities.size();
            universityCountry += u.countries.size();
        }

        Collection<Artifact> artifacts = dataCollector.getArtifacts();
        int artifactBusiness = 0;
        int artifcatCreator = 0;
        for (Artifact a : artifacts) {
            artifactBusiness += a.businesses.size();
            artifcatCreator += a.creators.size();
        }

        Collection<Business> businesses = dataCollector.getBusinesses();
        int businessCity = 0;
        int businessCountry = 0;
        for(Business b : businesses) {
            businessCity += b.cities.size();
            businessCountry += b.countries.size();
        }

        Collection<Person> persons = dataCollector.getPersons();
        int personCountry = 0;
        int personUniversity = 0;
        int personBusiness = 0;
        for (Person p : persons) {
            personCountry += p.politicianOf.size();
            personUniversity += p.universities.size();
            personBusiness += p.businesses.size();
        }

        System.out.println();
        System.out.println(String.format("Collected %d countries", countries.size()));
        System.out.println(String.format("Collected %d cities", cities.size()));
        System.out.println(String.format("Collected %d universities: %d countries, %d cities", universities.size(), universityCountry, universityCity));
        System.out.println(String.format("Collected %d persons: %d countries, %d universities, %d businesses", persons.size(), personCountry, personUniversity, personBusiness));
        System.out.println(String.format("Collected %d artifacts: %d businesses, %d creators,", artifacts.size(), artifactBusiness, artifcatCreator));
        System.out.println(String.format("Collected %d businesses: %d countries, %d cities", businesses.size(), businessCountry, businessCity));

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