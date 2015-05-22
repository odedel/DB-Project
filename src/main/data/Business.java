package main.data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Business extends Entity {

    public int id;
    
    public Business(String entity) {
        this.entity = entity;
    }

    public LocalDate creationDate;

    public long numberOfEmployees;

    public Set<City> cities = new HashSet<>();

    public Set<Country> countries = new HashSet<>();

}
