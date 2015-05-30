package main.data.entities;

import java.time.LocalDate;

public class PopulatedRegion extends Entity {

    /** --- Ctors ---*/
    public PopulatedRegion(String entity) {
        super(entity);
    }

    /** --- Gettes and Setters --- */

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public float getEconomicGrowth() {
        return economicGrowth;
    }

    public void setEconomicGrowth(float economicGrowth) {
        this.economicGrowth = economicGrowth;
    }

    public float getPoverty() {
        return poverty;
    }

    public void setPoverty(float poverty) {
        this.poverty = poverty;
    }

    public long getPopulation() {
        return population;
    }

    public void setPopulation(long population) {
        this.population = population;
    }

    public float getUnemployment() {
        return unemployment;
    }

    public void setUnemployment(float unemployment) {
        this.unemployment = unemployment;
    }

    public float getGini() {
        return gini;
    }

    public void setGini(float gini) {
        this.gini = gini;
    }

    public float getInflation() {
        return inflation;
    }

    public void setInflation(float inflation) {
        this.inflation = inflation;
    }

    public float getPopulationDensity() {
        return populationDensity;
    }

    public void setPopulationDensity(float populationDensity) {
        this.populationDensity = populationDensity;
    }

    /** --- Data Members --- */

    private LocalDate    creationDate;
    private float        economicGrowth;                 // %
    private float        poverty;                        // %
    private long         population;
    private float        unemployment;                   // %
    private float        gini;
    private float        inflation;                      // %
    private float        populationDensity;              // 1/km^2
}
