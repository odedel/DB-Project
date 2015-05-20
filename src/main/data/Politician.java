package main.data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Politician extends Person {

    public Politician(String entity) {
        this.entity = entity;
    }

    public int id;

    public Set<Country> countries = new HashSet<>();

    public City birthCity;

    public LocalDate birthDate;

    public City deathCity;

    public LocalDate deathDate;

    public Set<University> universities = new HashSet<>();

}
