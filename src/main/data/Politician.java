package main.data;

import java.util.HashSet;
import java.util.Set;

public class Politician extends Person {

    public Politician(String entity) {
        this.entity = entity;
    }

    public Set<Country> countries = new HashSet<>();

}
