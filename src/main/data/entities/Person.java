package main.data.entities;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Person extends Entity {

    public Person(String entity) {
        super(entity);
    }

    public int id;

    public City birthCity;

    public LocalDate birthDate;

    public City deathCity;

    public LocalDate deathDate;

    public Set<University> universities = new HashSet<>();

    public Set<Country> politicianOf = new HashSet<>();

    public Set<Business> businesses = new HashSet<>();
}
