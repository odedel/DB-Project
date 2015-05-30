package main.data.entities;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Business extends Entity {

    public int id;
    
    public Business(String entity) {
        super(entity);
    }

    public LocalDate creationDate;

    public long numberOfEmployees;

    public Set<City> cities = new HashSet<>();

    public Set<Country> countries = new HashSet<>();

}
