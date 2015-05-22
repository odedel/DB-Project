package main.data;

import java.util.HashSet;
import java.util.Set;

public class Creator extends Person {

    public Creator(String entity) {
        this.entity = entity;
    }

    public Set<Business> businesses = new HashSet<>();

}
