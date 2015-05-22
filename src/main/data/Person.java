package main.data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Person extends Entity {

    public int id;

    public City birthCity;

    public LocalDate birthDate;

    public City deathCity;

    public LocalDate deathDate;

    public Set<University> universities = new HashSet<>();
}
