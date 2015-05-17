package main.data;

import main.util.Callback;
import main.util.Row;
import main.util.Utils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.*;
import static main.util.Utils.*;

public class DataCollector {
    private Map<String, Country> countries = new HashMap<>();
    private Map<String, City> cities = new HashMap<>();
    private Map<String, Politician> politicians = new HashMap<>();

    public void collectData() throws IOException {
        getIDs();
        getNames(countries, cities, politicians);
        getFacts(politicians, countries, cities);

        deleteCitiesWithoutCountries();
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
        callbacks.add(getPoliticianIDsCallback());
        Utils.reduceEntitiesByAttributeFromCollectionWithMatcher(Consts.YAGO_TYPES_FILE, callbacks);
    }

    private Callback getCountryIDsCallback() {
        return new Callback() {
            @Override
            public void reduce(Row row) {
                countries.put((row.entity), new Country());
            }

            @Override
            public boolean map(Row row) {
                return row.superEntity.equals("<wikicat_Countries>");
            }
        };
    }

    private Callback getCityIDsCallback() {
        return new Callback() {
            @Override
            public void reduce(Row row) {
                cities.put((row.entity), new City());
            }

            @Override
            public boolean map(Row row) {
                return row.superEntity.equals("<wordnet_city_108524735>");
            }
        };
    }

    private Callback getPoliticianIDsCallback() {
        return new Callback() {
            @Override
            public void reduce(Row row) {
                politicians.put((row.entity), new Politician());
            }

            @Override
            public boolean map(Row row) {
                return row.superEntity.equals("<wordnet_politician_110450303>");
            }
        };
    }

    private void getNames(final Map<String, ? extends Entity>... entities_maps) throws IOException {
        List<Callback> callbacks = new LinkedList<>();
        for (final Map<String, ? extends Entity> entities : entities_maps) {
            callbacks.add(new GenericCallback(entities, ValueType.NAME, "skos:prefLabel", "name"));
        }
        Utils.reduceEntitiesByAttributeFromCollectionWithMatcher(Consts.YAGO_LABELS_FILE, callbacks);
    }

    private void getFacts(Map<String, Politician> politicians, final Map<String, ? extends PopulatedRegion>... place_maps) throws IOException {
        String factFiles[] = new String[]{Consts.YAGO_DATE_FACTS_FILE, Consts.YAGO_FACTS_FILE, Consts.YAGO_LITERAL_FACTS_FILE,};

        List<Callback> callbacks = new LinkedList<>();
        for (Map<String, ? extends PopulatedRegion> places : place_maps) {
            callbacks.addAll(getCallbacks(places));
        }

        callbacks.addAll(getCountryCallbacks(countries));
        callbacks.addAll(getCityCallbacks(cities, countries));
        callbacks.addAll(getPoliticianCallback(politicians, cities, countries));

        for (String factFile : factFiles) {
            Utils.reduceEntitiesByAttributeFromCollectionWithMatcher(factFile, callbacks);
        }
    }

    private List<Callback> getCountryCallbacks(final Map<String, ? extends Country> places) {
        return singletonList(new GenericCallback(places, ValueType.STRING, "<hasTLD>", "tld"));
    }

    private List<Callback> getPoliticianCallback(Map<String, Politician> politicians, Map<String, City> cities, Map<String, Country> countries) {
        Callback bornInCallback = new Callback() {
            @Override
            public void reduce(Row row) {
                politicians.get(row.entity).birthCity = cities.get(row.superEntity);
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<wasBornIn>") && politicians.containsKey(row.entity) && cities.containsKey(row.superEntity);
            }
        };

        Callback diedInCallback = new Callback() {
            @Override
            public void reduce(Row row) {
                politicians.get(row.entity).deathCity = cities.get(row.superEntity);
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<diedIn>") && politicians.containsKey(row.entity) && cities.containsKey(row.superEntity);
            }
        };

        Callback politicianOfCountriesCallback = new Callback() {
            @Override
            public void reduce(Row row) {
                politicians.get(row.entity).politicianOfCountries.add(countries.get(row.superEntity));
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<isPoliticianOf>") && politicians.containsKey(row.entity) && countries.containsKey(row.superEntity);
            }
        };

        Callback politicianOfCitiesCallback = new Callback() {
            @Override
            public void reduce(Row row) {
                politicians.get(row.entity).politicianOfCities.add(cities.get(row.superEntity));
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<isPoliticianOf>") && politicians.containsKey(row.entity) && cities.containsKey(row.superEntity);
            }
        };

        List<Callback> callbackList = new LinkedList<>();
        Callback[] callbacks = new Callback[] {bornInCallback, diedInCallback,
                politicianOfCountriesCallback, politicianOfCitiesCallback};
        Collections.addAll(callbackList, callbacks);
        return callbackList;
    }

    private List<Callback> getCityCallbacks(final Map<String, ? extends City> cities, Map<String, Country> countries) {
        return singletonList(new Callback() {
            @Override
            public void reduce(Row row) {
                cities.get(row.entity).country = countries.get(row.superEntity);
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<isLocatedIn>") && cities.containsKey(row.entity) &&
                        countries.containsKey(row.superEntity);
            }
        });
    }

    private List<Callback> getCallbacks(final Map<String, ? extends PopulatedRegion> places) {
        Callback _places = new Callback() {
            @Override
            public void reduce(Row row) {
                places.get(parseName(row.superEntity)).places.add(row.entity);
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<isLocatedIn>") && places.containsKey(row.superEntity);
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

    private void deleteCitiesWithoutCountries() {
        List<String> citiesToRemove = new ArrayList<>();

        citiesToRemove.addAll(cities.entrySet().stream().filter(entry -> entry.getValue().country == null).map(Map.Entry<String, City>::getKey).collect(Collectors.toList()));
        citiesToRemove.forEach(cities::remove);

        System.out.println(String.format("Deleted %d cities", citiesToRemove.size()));
    }
}
