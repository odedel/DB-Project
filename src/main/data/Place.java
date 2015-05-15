package main.data;

import main.util.Row;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public class Place {

    public String name;
    public List<Row> facts = new LinkedList<>();
    public LocalDate creationDate;
    public List<String> places = new LinkedList<>();
    public float export;            // Dollar
    public float expenses;          // Dollar
    public float latitude;          // Degress
    public float longitude;         // Degress
    public float economicGrowth;    // %
    public float poverty;           // %
    public long population;
    public float unemployment;      // %
    public float revenue;           // Dollar
    public float gini;
    public float _import;           // Dollar
    public float gdp;               // Dollar
    public float inflation;         // %
    public float populationDensity; // 1/km^2
    public String tld;
}
