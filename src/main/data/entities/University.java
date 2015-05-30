package main.data.entities;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class University extends Entity {

    public University(String entity) {
        this.entity = entity;
    }

    public int id;

    public String name;

    public Set<Country> countries = new HashSet<>();

    public Set<City> cities = new HashSet<>();

    public LocalDate creationDate;

}
