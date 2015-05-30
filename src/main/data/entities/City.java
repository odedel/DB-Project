package main.data.entities;

public class City extends PopulatedRegion {

    public City(String entity) {
        this.entity = entity;
    }

    public City() { }

    public Country country;
}
