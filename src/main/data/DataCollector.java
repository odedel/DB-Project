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
    private Map<String, University> universities = new HashMap<>();

    public void collectData() throws IOException {
        getIDs();
        getNames(countries, cities, politicians, universities);
        getFacts(politicians, universities, countries, cities);

        postCitiesProcessor();
        postUniversitiesProcessor();
        postPoliticiansProcessor();
    }

    public Collection<Country> getCountries() {
        return countries.values();
    }

    public Collection<City> getCities() {
        return cities.values();
    }

    public Collection<Politician> getPoliticians() {
        return politicians.values();
    }

    public Collection<University> getUniversities() { return universities.values(); }

    private void getIDs() throws IOException {
        List<Callback> callbacks = new LinkedList<>();
        callbacks.add(getCountryIDsCallback());
        callbacks.add(getCityIDsCallback());
        callbacks.add(getPoliticianIDsCallback());
        callbacks.add(getUniversitiesIDsCallback());

        Utils.reduceEntitiesByAttributeFromCollectionWithMatcher(Consts.YAGO_TYPES_FILE, callbacks);
    }

    private Callback getCountryIDsCallback() {
        return new Callback() {
            @Override
            public void reduce(Row row) {
                countries.put((row.entity), new Country(row.entity));
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
                cities.put((row.entity), new City(row.entity));
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
                politicians.put((row.entity), new Politician(row.entity));
            }

            @Override
            public boolean map(Row row) {
                return row.superEntity.equals("<wordnet_politician_110450303>");
            }
        };
    }

    private Callback getUniversitiesIDsCallback() {
        return new Callback() {
            @Override
            public void reduce(Row row) {
                universities.put((row.entity), new University(row.entity));
            }

            @Override
            public boolean map(Row row) {
                return row.superEntity.equals("<wordnet_university_108286569>");
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

    private void getFacts(Map<String, Politician> politicians, Map<String, University> universities,
                          final Map<String, ? extends PopulatedRegion>... place_maps) throws IOException {
        String factFiles[] = new String[]{Consts.YAGO_DATE_FACTS_FILE, Consts.YAGO_FACTS_FILE, Consts.YAGO_LITERAL_FACTS_FILE,};

        List<Callback> callbacks = new LinkedList<>();
        for (Map<String, ? extends PopulatedRegion> places : place_maps) {
            callbacks.addAll(getCallbacks(places));
        }

        callbacks.addAll(getCountryCallbacks(countries));
        callbacks.addAll(getCityCallbacks(cities, countries));
        callbacks.addAll(getUniversitiesCallback(universities, cities, countries));
        callbacks.addAll(getPoliticianCallback(politicians, universities, cities, countries));

        for (String factFile : factFiles) {
            Utils.reduceEntitiesByAttributeFromCollectionWithMatcher(factFile, callbacks);
        }
    }

    private List<Callback> getUniversitiesCallback(Map<String, University> universities,
                                                   Map<String, City> cities, Map<String, Country> countries) {
        Callback countriesCallback = new Callback() {
            @Override
            public void reduce(Row row) {
                universities.get(row.entity).countries.add(countries.get(row.superEntity));
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<isLocatedIn>") && universities.containsKey(row.entity) &&
                        countries.containsKey(row.superEntity);
            }
        };

        Callback citiesCallback = new Callback() {
            @Override
            public void reduce(Row row) {
                universities.get(row.entity).cities.add(cities.get(row.superEntity));
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<isLocatedIn>") && universities.containsKey(row.entity) &&
                        cities.containsKey(row.superEntity);
            }
        };

        Callback[] c = new Callback[]{countriesCallback, citiesCallback,
                new GenericCallback(universities, ValueType.DATE,  "<wasCreatedOnDate>",     "creationDate"),
        };
        List<Callback> callbacks = new LinkedList<>();
        Collections.addAll(callbacks, c);
        return callbacks;
    }

    private List<Callback> getCountryCallbacks(final Map<String, ? extends Country> places) {
        return singletonList(new GenericCallback(places, ValueType.STRING, "<hasTLD>", "tld"));
    }

    private List<Callback> getPoliticianCallback(Map<String, Politician> politicians, Map<String, University> universities,
                                                 Map<String, City> cities, Map<String, Country> countries) {
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
                politicians.get(row.entity).countries.add(countries.get(row.superEntity));
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<isPoliticianOf>") && politicians.containsKey(row.entity) && countries.containsKey(row.superEntity);
            }
        };

        Callback universitiesCallback = new Callback() {
            @Override
            public void reduce(Row row) {
                politicians.get(row.entity).universities.add(universities.get(row.superEntity));
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<graduatedFrom>") && politicians.containsKey(row.entity) && universities.containsKey(row.superEntity);
            }
        };

        List<Callback> callbackList = new LinkedList<>();
        Callback[] callbacks = new Callback[] {bornInCallback, diedInCallback,
                politicianOfCountriesCallback, universitiesCallback,
                new GenericCallback(politicians, ValueType.DATE,  "<diedOnDate>",     "deathDate"),
                new GenericCallback(politicians, ValueType.DATE,  "<wasBornOnDate>",     "birthDate"),
        };
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

    private void postCitiesProcessor() {
        List<String> citiesToRemove = new ArrayList<>();

        citiesToRemove.addAll(cities.entrySet().stream().filter(entry -> entry.getValue().country == null).map(Map.Entry<String, City>::getKey).collect(Collectors.toList()));
        citiesToRemove.forEach(cities::remove);

        System.out.println(String.format("Deleted %d cities", citiesToRemove.size()));
    }

    private void postUniversitiesProcessor() {
        List<University> universitiesToRemove = new ArrayList<>();

        for (Map.Entry<String, University> universitiesEntry: universities.entrySet()) {
            List<City> citiesToBeRemoved = new LinkedList<>();

            for (City city : universitiesEntry.getValue().cities) {
                if (!cities.containsKey(city.entity)) {
                    citiesToBeRemoved.add(city);
                }
            }
            for (City city : citiesToBeRemoved) {
                universitiesEntry.getValue().cities.remove(city);
            }

            if (universitiesEntry.getValue().cities.isEmpty() && universitiesEntry.getValue().countries.isEmpty()) {
                universitiesToRemove.add(universitiesEntry.getValue());
            }
        }

        universitiesToRemove.forEach(universities::remove);

        System.out.println(String.format("Deleted %d universities", universitiesToRemove.size()));
    }

    private void postPoliticiansProcessor() {
        List<String> politiciansToRemove = new ArrayList<>();

        for (Map.Entry<String, Politician> politicianEntry: politicians.entrySet()) {
            if (politicianEntry.getValue().countries.isEmpty()) {
                politiciansToRemove.add(politicianEntry.getKey());
            }
            if (politicianEntry.getValue().birthCity != null &&
                    !cities.containsKey(politicianEntry.getValue().birthCity.entity)) {
                politicianEntry.getValue().birthCity = null;
            }
            if (politicianEntry.getValue().deathCity != null &&
                     !cities.containsKey(politicianEntry.getValue().deathCity.entity)) {
                politicianEntry.getValue().deathCity = null;
            }
            List<University> universitiesToBeRemoved = new LinkedList<>();
            for (University university : politicianEntry.getValue().universities) {
                if (!universities.containsKey(university.entity)) {
                    universitiesToBeRemoved.add(university);
                }
            }
            for (University university : universitiesToBeRemoved) {
                politicianEntry.getValue().universities.remove(university);
            }
        }

        politiciansToRemove.forEach(politicians::remove);

        System.out.println(String.format("Deleted %d politicians", politiciansToRemove.size()));
    }
}
