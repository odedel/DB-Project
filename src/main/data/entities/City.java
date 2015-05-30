package main.data.entities;

public class City extends PopulatedRegion {

    /** --- Ctors --- */
    public City(String entity) {
        super(entity);
    }

    /** --- Getters and Setters --- */

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    /** --- Data Members --- */
    private Country country;
}
