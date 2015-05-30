package main.data.entities;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Business extends Entity {

    /** ---  Ctors--- */

    public Business(String entity) {
        super(entity);
        this.cities = new HashSet<>();
        this.countries = new HashSet<>();
    }

    /** --- Getters and Setters --- */

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public long getNumberOfEmployees() {
        return numberOfEmployees;
    }

    public void setNumberOfEmployees(long numberOfEmployees) {
        this.numberOfEmployees = numberOfEmployees;
    }

    public Set<City> getCities() {
        return cities;
    }

    public void addCity(City city) {
        this.cities.add(city);
    }

    public Set<Country> getCountries() {
        return countries;
    }

    public void addCountry(Country country) {
        this.countries.add(country);
    }

    /** --- Data Members --- */

    public LocalDate creationDate;

    public long numberOfEmployees;

    public Set<City> cities;

    public Set<Country> countries;

}
