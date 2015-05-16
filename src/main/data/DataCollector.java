package main.data;

import main.util.Callback;
import main.util.Row;
import main.util.Utils;

import java.io.IOException;
import java.util.*;

import static java.util.Collections.*;
import static main.util.Utils.*;

public class DataCollector {
    private Map<String, Country> countries = new HashMap<>();
    private Map<String, City> cities = new HashMap<>();

    public void collectData() throws IOException {
        getIDs();
        getNames(countries, cities);
        getFacts(countries, cities);
    }

    public Collection<Country> getCountries() {
        return countries.values();
    }

    public Collection<City> getCities() {
        return cities.values();
    }

    private void getIDs() throws IOException {
        List<Callback> callbacks = new LinkedList<>();
        callbacks.add(getCountryIDsCallback());
        callbacks.add(getCityIDsCallback());
        Utils.reduceEntitiesByAttributeFromCollectionWithMatcher(Consts.YAGO_TYPES_FILE, callbacks);
    }

    private Callback getCountryIDsCallback() {
        return new Callback() {
            @Override
            public void reduce(Row row) {
                countries.put(parseName(row.entity), new Country());
            }

            @Override
            public boolean map(Row row) {
                return row.superEntity.equals("<wordnet_country_108544813>");
            }
        };
    }

    private Callback getCityIDsCallback() {
        return new Callback() {
            @Override
            public void reduce(Row row) {
                cities.put(parseName(row.entity), new City());
            }

            @Override
            public boolean map(Row row) {
                return row.superEntity.startsWith("<wikicat_Cities");
            }
        };
    }

    private void getNames(final Map<String, ? extends PopulatedRegion>... place_maps) throws IOException {
        List<Callback> callbacks = new LinkedList<>();
        for (final Map<String, ? extends PopulatedRegion> places : place_maps) {
            callbacks.add(new GenericCallback(places, ValueType.NAME, "skos:prefLabel", "name"));
        }
        Utils.reduceEntitiesByAttributeFromCollectionWithMatcher(Consts.YAGO_LABELS_FILE, callbacks);
    }

    private void getFacts(final Map<String, ? extends PopulatedRegion>... place_maps) throws IOException {
        String factFiles[] = new String[]{Consts.YAGO_DATE_FACTS_FILE, Consts.YAGO_FACTS_FILE, Consts.YAGO_LITERAL_FACTS_FILE,};

        List<Callback> callbacks = new LinkedList<>();
        for (Map<String, ? extends PopulatedRegion> places : place_maps) {
            callbacks.addAll(getCallbacks(places));
        }

        callbacks.addAll(getCountryCallbacks(countries));
        callbacks.addAll(getCityCallbacks(cities, countries));

        for (String factFile : factFiles) {
            Utils.reduceEntitiesByAttributeFromCollectionWithMatcher(factFile, callbacks);
        }
    }

    private List<Callback> getCountryCallbacks(final Map<String, ? extends Country> places) {
        return singletonList(new GenericCallback(places, ValueType.STRING, "<hasTLD>", "tld"));
    }

    private List<Callback> getCityCallbacks(final Map<String, ? extends City> cities, Map<String, Country> countries) {
        return singletonList(new Callback() {
            @Override
            public void reduce(Row row) {
                //Assignment is for a String and not an object since it will be put into the DB
                cities.get(parseName(row.entity)).country = parseName(row.superEntity);
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<isLocatedIn>") && cities.keySet().contains(parseName(row.entity)) &&
                        countries.keySet().contains(parseName(row.superEntity));
            }
        });
    }

    private List<Callback> getCallbacks(final Map<String, ? extends PopulatedRegion> places) {
        Callback _places = new Callback() {
            @Override
            public void reduce(Row row) {
                places.get(parseName(row.superEntity)).places.add(parseName(row.entity));
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<isLocatedIn>") && places.keySet().contains(parseName(row.superEntity));
            }
        };

        Callback[] c = new Callback[]{_places,
                new GenericCallback(places, ValueType.DATE,  "<wasCreatedOnDate>",     "creationDate"),
                new GenericCallback(places, ValueType.FLOAT, "<hasExport>",            "export"),
                new GenericCallback(places, ValueType.FLOAT, "<hasExpenses>",          "expenses"),
                new GenericCallback(places, ValueType.FLOAT, "<hasLatitude>",          "latitude"),
                new GenericCallback(places, ValueType.FLOAT, "<hasLongitude>",         "longitude"),
                new GenericCallback(places, ValueType.FLOAT, "<hasEconomicGrowth>",    "economicGrowth"),
                new GenericCallback(places, ValueType.FLOAT, "<hasPoverty>",           "poverty"),
                new GenericCallback(places, ValueType.LONG,  "<hasNumberOfPeople>",    "population"),
                new GenericCallback(places, ValueType.FLOAT, "<hasUnemployment>",      "unemployment"),
                new GenericCallback(places, ValueType.FLOAT, "<hasRevenue>",           "revenue"),
                new GenericCallback(places, ValueType.FLOAT, "<hasGini>",              "gini"),
                new GenericCallback(places, ValueType.FLOAT, "<hasImport>",            "_import"),
                new GenericCallback(places, ValueType.FLOAT, "<hasGDP>",               "gdp"),
                new GenericCallback(places, ValueType.FLOAT, "<hasInflation>",         "inflation"),
                new GenericCallback(places, ValueType.FLOAT, "<hasPopulationDensity>", "populationDensity"),
        };
        List<Callback> callbacks = new LinkedList<>();
        Collections.addAll(callbacks, c);
        return callbacks;
    }
}
