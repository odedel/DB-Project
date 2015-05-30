package main.data.entities;

import java.time.LocalDate;

public class PopulatedRegion extends Entity {

    public PopulatedRegion(String entity) {
        super(entity);
    }

    public int          id;
    public LocalDate    creationDate;
    public float        export;                         // Dollar
    public float        expenses;                       // Dollar
    public float        latitude;                       // Degress
    public float        longitude;                      // Degress
    public float        economicGrowth;                 // %
    public float        poverty;                        // %
    public long         population;
    public float        unemployment;                   // %
    public float        revenue;                        // Dollar
    public float        gini;
    public float        _import;                        // Dollar
    public float        gdp;                            // Dollar
    public float        inflation;                      // %
    public float        populationDensity;              // 1/km^2
}
