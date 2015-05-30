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

    public float getEconomicGrowth() {
        return economicGrowth;
    }

    public float getPoverty() {
        return poverty;
    }

    public long getPopulation() {
        return population;
    }

    public float getUnemployment() {
        return unemployment;
    }

    public float getGini() {
        return gini;
    }

    public float getInflation() {
        return inflation;
    }

    public float getPopulationDensity() {
        return populationDensity;
    }


    /** --- Data Members --- */

    protected LocalDate    creationDate;
    protected float        economicGrowth;                 // %
    protected float        poverty;                        // %
    protected long         population;
    protected float        unemployment;                   // %
    protected float        gini;
    protected float        inflation;                      // %
    protected float        populationDensity;              // 1/km^2
}
