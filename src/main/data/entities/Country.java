package main.data.entities;

public class Country extends PopulatedRegion {

    public Country(String entity) {
        this.entity = entity;
    }

    public Country() { }

    public String       tld;
}
