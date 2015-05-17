package main.data;

import java.util.HashSet;
import java.util.Set;

public class Politician extends Person {

    public Set<Country> politicianOfCountries = new HashSet<>();

    public Set<City> politicianOfCities = new HashSet<>();

    public City birthCity;

    public City deathCity;

}
