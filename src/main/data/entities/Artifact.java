package main.data.entities;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Artifact extends Entity{

    public int id;

    public Artifact(String entity) {
        this.entity = entity;
    }

    public LocalDate creationDate;

    public Set<Business> businesses = new HashSet<>();

    public Set<Person> creators = new HashSet<>();


}
