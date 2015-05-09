package main;

import main.data.Country;
import main.data.CountryData;

import java.util.Collection;
import java.util.Map;

class Sample {

    public static void main(String args[]) throws Exception {
        Collection countries = CountryData.collectCountries();
        System.out.println(String.format("Collected %d countries", countries.size()));
    }

}