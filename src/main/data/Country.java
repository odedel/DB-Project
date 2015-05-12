package main.data;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public class Country {
    public int id;
    public String name;
//    public List<Row> facts = new LinkedList<>();
    public LocalDate creationDate;
    public List<String> places = new LinkedList<>();
//    public String export;
//    public String expenses;
//    public String latitude;
//    public String longitude;
    public float economicGrowth;  // This is %
    public float poverty;  // This is %
    public int population;
    public float unemployment;    // This is %
//    public String revenue;
    public float gini;
//    public String _import;
//    public String gdp;
    public float inflation;   // This is %
    public float populationDensity;    // This is /km\u005e2
//    public String tld;
}
