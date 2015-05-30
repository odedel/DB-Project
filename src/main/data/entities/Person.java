package main.data.entities;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Person extends Entity {

    /** --- Ctors --- */

    public Person(String entity) {

        super(entity);
        this.universities = new HashSet<>();
        this.politicianOf = new HashSet<>();
        this.businesses = new HashSet<>();
    }


    /** --- Getters and Setters --- */

    public City getBirthCity() {
        return birthCity;
    }

    public void setBirthCity(City city) { this.birthCity = city; }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public City getDeathCity() {
        return deathCity;
    }

    public LocalDate getDeathDate() {
        return deathDate;
    }

    public void setDeathCity(City city) { this.deathCity = city; }

    public Set<University> getUniversities() {
        return universities;
    }

    public Set<Country> getPoliticianOf() {
        return politicianOf;
    }

    public Set<Business> getBusinesses() {
        return businesses;
    }


    /** --- Data Members --- */

    protected City birthCity;

    protected LocalDate birthDate;

    protected City deathCity;

    protected LocalDate deathDate;

    protected Set<University> universities;

    protected Set<Country> politicianOf;

    protected Set<Business> businesses;
}
