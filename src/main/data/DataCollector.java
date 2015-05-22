package main.data;

import main.util.Callback;
import main.util.Row;
import main.util.Utils;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

public class DataCollector {

    private Map<String, Country> countries = new HashMap<>();
    private Map<String, City> cities = new HashMap<>();
    private Map<String, Politician> politicians = new HashMap<>();
    private Map<String, University> universities = new HashMap<>();
    private Map<String, Business> businesses = new HashMap<>();
    private Map<String, Creator> creators = new HashMap<>();
    private Map<String, Artifact> artifacts = new HashMap<>();

    public void collectData() throws IOException {
        getIDs();
        getNames( countries, cities);
        getFacts(countries, cities);

        postCitiesProcessor();
        postUniversitiesProcessor();
        postPoliticiansProcessor();
        postBusinessesProcessor();
        postCreatorsProcessor();
        postArtifactProcessor();
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

    public Collection<Business> getBusinesses() { return businesses.values(); }

    public Collection<Creator> getCreators() { return creators.values(); }

    public Collection<Artifact> getArtifacts() { return artifacts.values(); }

    class GenericEntityCallback<T> extends Callback {
        private final Map<String, T> map;
        private final String prefix;
        Constructor<T> clazzConstructor;

        GenericEntityCallback(Map<String, T> map, Class<T> clazz, String prefix) {
            this.map = map;
            this.prefix = prefix;
            try {
                clazzConstructor = clazz.getConstructor(String.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
    }

            @Override
            public void reduce(Row row) {
            try {
                map.put((row.entity), clazzConstructor.newInstance(row.entity));
            } catch (InstantiationException|IllegalAccessException|InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            }

            @Override
            public boolean map(Row row) {
            return row.superEntity.startsWith(prefix);
    }
            }
    private void getIDs() throws IOException {
        List<Callback> callbacks = new LinkedList<>();
        Callback[] c = new Callback[]{
            new GenericEntityCallback<>(artifacts,      Artifact.class,     "<wordnet_artifact"),
            new GenericEntityCallback<>(creators,       Creator.class,      "<wordnet_creator"),
            new GenericEntityCallback<>(businesses,     Business.class,     "<wordnet_business"),
            new GenericEntityCallback<>(countries,      Country.class,      "<wikicat_Countries"),
            new GenericEntityCallback<>(cities,         City.class,         "<wikicat_Cities"),
            new GenericEntityCallback<>(politicians,    Politician.class,   "<wordnet_politician"),
            new GenericEntityCallback<>(universities,   University.class,   "<wordnet_university"),
        };
        Collections.addAll(callbacks, c);
        //FIXME: do we need to use the other attributes file?
        Utils.reduceEntitiesByAttributeFromCollectionWithMatcher(Consts.YAGO_TYPES_FILE, callbacks);
    }



    private void getNames(final Map<String, ? extends Entity>... entities_maps) throws IOException {
        List<Callback> callbacks = new LinkedList<>();
        for (final Map<String, ? extends Entity> entities : entities_maps) {
            callbacks.add(new GenericCallback(entities, ValueType.NAME, "skos:prefLabel", "name"));
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
        callbacks.addAll(getUniversitiesCallback(universities, cities, countries));
        callbacks.addAll(getPersonCallback(politicians, universities, cities));
        callbacks.addAll(getPersonCallback(creators, universities, cities));
        callbacks.addAll(getBusinessesCallback(businesses, cities, countries));
        callbacks.add(getPoliticianCallback(politicians, countries));
        callbacks.addAll(getCreatorCallback(creators, businesses, artifacts));
        callbacks.addAll(getArtifactCallback(artifacts));

        for (String factFile : factFiles) {
            Utils.reduceEntitiesByAttributeFromCollectionWithMatcher(factFile, callbacks);
        }
    }

    private List<Callback> getArtifactCallback(Map<String, Artifact> artifacts) {
        Callback businessCreatedArtifactCallback = new Callback() {
            @Override
            public void reduce(Row row) {
                artifacts.get(row.superEntity).businesses.add(businesses.get(row.entity));
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<created>") && artifacts.containsKey(row.superEntity) && businesses.containsKey(row.entity);
            }
        };

        Callback creatorsCreatedArtifactCallback = new Callback() {
            @Override
            public void reduce(Row row) {
                artifacts.get(row.superEntity).creators.add(creators.get(row.entity));
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<created>") && artifacts.containsKey(row.superEntity) && creators.containsKey(row.entity);
            }
        };

        Callback[] c = new Callback[]{businessCreatedArtifactCallback, creatorsCreatedArtifactCallback,
                new GenericCallback(artifacts, ValueType.DATE, "<wasCreatedOnDate>", "creationDate")
        };
        List<Callback> callbacks = new LinkedList<>();
        Collections.addAll(callbacks, c);
        return callbacks;
    }

    private List<Callback> getBusinessesCallback(Map<String, Business> businesses, Map<String, City> cities, Map<String, Country> countries) {
        Callback locatedInCountryCallback = new Callback() {
            @Override
            public void reduce(Row row) {
                businesses.get(row.entity).countries.add(countries.get(row.superEntity));
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<isLocatedIn>") && businesses.containsKey(row.entity) &&
                        countries.containsKey(row.superEntity);
            }
        };

        Callback locatedInCityCallback = new Callback() {
            @Override
            public void reduce(Row row) {
                businesses.get(row.entity).cities.add(cities.get(row.superEntity));
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<isLocatedIn>") && businesses.containsKey(row.entity) &&
                        cities.containsKey(row.superEntity);
            }
        };

        Callback[] c = new Callback[]{locatedInCountryCallback, locatedInCityCallback,
                new GenericCallback(businesses, ValueType.DATE, "<wasCreatedOnDate>", "creationDate"),
                new GenericCallback(businesses, ValueType.LONG, "<hasNumberOfPeople>", "numberOfEmployees")
        };
        List<Callback> callbacks = new LinkedList<>();
        Collections.addAll(callbacks, c);
        return callbacks;
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

    private List<Callback> getPersonCallback(Map<String, ? extends Person> persons, Map<String, University> universities,
                                             Map<String, City> cities) {
        Callback bornInCallback = new Callback() {
            @Override
            public void reduce(Row row) {
                persons.get(row.entity).birthCity = cities.get(row.superEntity);
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<wasBornIn>") && persons.containsKey(row.entity) && cities.containsKey(row.superEntity);
            }
        };

        Callback diedInCallback = new Callback() {
            @Override
            public void reduce(Row row) {
                persons.get(row.entity).deathCity = cities.get(row.superEntity);
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<diedIn>") && persons.containsKey(row.entity) && cities.containsKey(row.superEntity);
            }
        };

        Callback universitiesCallback = new Callback() {
            @Override
            public void reduce(Row row) {
                persons.get(row.entity).universities.add(universities.get(row.superEntity));
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<graduatedFrom>") && persons.containsKey(row.entity) && universities.containsKey(row.superEntity);
            }
        };

        List<Callback> callbackList = new LinkedList<>();
        Callback[] callbacks = new Callback[] {bornInCallback, diedInCallback, universitiesCallback,
                new GenericCallback(persons, ValueType.DATE,  "<diedOnDate>",     "deathDate"),
                new GenericCallback(persons, ValueType.DATE,  "<wasBornOnDate>",     "birthDate"),
        };
        Collections.addAll(callbackList, callbacks);
        return callbackList;
    }

    private Callback getPoliticianCallback(Map<String, Politician> politicians, Map<String, Country> countries) {
        return new Callback() {
            @Override
            public void reduce(Row row) {
                politicians.get(row.entity).countries.add(countries.get(row.superEntity));
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<isPoliticianOf>") && politicians.containsKey(row.entity) && countries.containsKey(row.superEntity);
            }
        };
    }

    private List<Callback> getCreatorCallback(Map<String, Creator> creators, Map<String, Business> businesses, Map<String, Artifact> artifacts) {
        Callback createdBusinessCallback = new Callback() {
            @Override
            public void reduce(Row row) {
                creators.get(row.entity).businesses.add(businesses.get(row.superEntity));
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<created>") && creators.containsKey(row.entity) && businesses.containsKey(row.superEntity);
            }
        };

        List<Callback> callbackList = new LinkedList<>();
        Callback[] callbacks = new Callback[] {createdBusinessCallback};
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

        Callback[] c = new Callback[]{
                new GenericCallback(places, ValueType.STRING,"<isLocatedIn>",          "places",        true, true),
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
        List<String> universitiesToRemove = new ArrayList<>();

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
                universitiesToRemove.add(universitiesEntry.getKey());
            }
        }

        universitiesToRemove.forEach(universities::remove);

        System.out.println(String.format("Deleted %d universities", universitiesToRemove.size()));
    }

    private void postPersonProcessor(Map<String, ? extends Person> persons) {
        for (Map.Entry<String, ? extends Person> personEntry : persons.entrySet()) {
            if (personEntry.getValue().birthCity != null &&
                    !cities.containsKey(personEntry.getValue().birthCity.entity)) {
                personEntry.getValue().birthCity = null;
            }
            if (personEntry.getValue().deathCity != null &&
                    !cities.containsKey(personEntry.getValue().deathCity.entity)) {
                personEntry.getValue().deathCity = null;
            }
            List<University> universitiesToBeRemoved = new LinkedList<>();
            for (University university : personEntry.getValue().universities) {
                if (!universities.containsKey(university.entity)) {
                    universitiesToBeRemoved.add(university);
                }
            }
            for (University university : universitiesToBeRemoved) {
                personEntry.getValue().universities.remove(university);
            }
        }
    }

    private void postPoliticiansProcessor() {
        List<String> politiciansToRemove = new ArrayList<>();

        for (Map.Entry<String, Politician> politicianEntry: politicians.entrySet()) {
            if (politicianEntry.getValue().countries.isEmpty()) {
                politiciansToRemove.add(politicianEntry.getKey());
            }
        }
        politiciansToRemove.forEach(politicians::remove);

        postPersonProcessor(politicians);

        System.out.println(String.format("Deleted %d politicians", politiciansToRemove.size()));
    }

    private void postCreatorsProcessor() {
        postPersonProcessor(creators);

        List<String> creatorsToRemove = new ArrayList<>();
        for (Map.Entry<String, Creator> creatorEntry: creators.entrySet()) {
            if (creatorEntry.getValue().birthCity == null && creatorEntry.getValue().deathCity == null) {
                creatorsToRemove.add(creatorEntry.getKey());
            }

            List<Business> businessesToBeRemoved = new LinkedList<>();
            for (Business business : creatorEntry.getValue().businesses) {
                if (!businesses.containsKey(business.entity)) {
                    businessesToBeRemoved.add(business);
                }
            }
            for (Business business : businessesToBeRemoved) {
                creatorEntry.getValue().businesses.remove(business);
            }

        }
        creatorsToRemove.forEach(creators::remove);

        System.out.println(String.format("Deleted %d creators", creatorsToRemove.size()));
    }

    private void postBusinessesProcessor() {
        List<String> businessesToRemove = new ArrayList<>();

        for (Map.Entry<String, Business> businessEntry: businesses.entrySet()) {
            List<City> citiesToBeRemoved = new LinkedList<>();
            for (City city : businessEntry.getValue().cities) {
                if (!cities.containsKey(city.entity)) {
                    citiesToBeRemoved.add(city);
                }
            }
            for (City city : citiesToBeRemoved) {
                businessEntry.getValue().cities.remove(city);
            }

            if (businessEntry.getValue().countries.isEmpty() && businessEntry.getValue().cities.isEmpty()) {
                businessesToRemove.add(businessEntry.getKey());
            }
        }

        businessesToRemove.forEach(businesses::remove);

        System.out.println(String.format("Deleted %d businesses", businessesToRemove.size()));
    }

    private void postArtifactProcessor() {
        List<String> artifactsToRemove = new ArrayList<>();

        for (Map.Entry<String, Artifact> artifactEntry: artifacts.entrySet()) {
            List<Business> businessesToBeRemoves = new LinkedList<>();
            for (Business business : artifactEntry.getValue().businesses) {
                if (!businesses.containsKey(business.entity)) {
                    businessesToBeRemoves.add(business);
                }
            }
            for (Business business : businessesToBeRemoves) {
                artifactEntry.getValue().businesses.remove(business);
            }

            List<Creator> creatorsToBeRemoved = new ArrayList<>();
            for (Creator creator : artifactEntry.getValue().creators) {
                if (!creators.containsKey(creator.entity)) {
                    creatorsToBeRemoved.add(creator);
                }
            }
            for (Creator creator : creatorsToBeRemoved) {
                artifactEntry.getValue().creators.remove(creator);
            }

            if (artifactEntry.getValue().creators.isEmpty() && artifactEntry.getValue().businesses.isEmpty()) {
                artifactsToRemove.add(artifactEntry.getKey());
            }
        }

        artifactsToRemove.forEach(artifacts::remove);

        System.out.println(String.format("Deleted %d artifacts", artifactsToRemove.size()));
    }
}
