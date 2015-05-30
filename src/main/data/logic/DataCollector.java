package main.data.logic;

import main.data.entities.*;
import main.util.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class DataCollector {

    private Map<String, Country> countries = new HashMap<>();
    private Map<String, City> cities = new HashMap<>();
    private Map<String, University> universities = new HashMap<>();
    private Map<String, Business> businesses = new HashMap<>();
    private Map<String, Artifact> artifacts = new HashMap<>();
    private Map<String, Person> persons = new HashMap<>();

    public void collectData() throws IOException {
        getIDs();
        //noinspection unchecked
        getNames(persons, artifacts, businesses, countries, cities, universities);
        //noinspection unchecked
        getFacts(countries, cities);
        postProcessData();
    }

    /* -------------------- Get Methods -------------------- */

    public Collection<Country> getCountries() {
        return countries.values();
    }

    public Collection<City> getCities() {
        return cities.values();
    }

    public Collection<University> getUniversities() {
        return universities.values();
    }

    public Collection<Business> getBusinesses() {
        return businesses.values();
    }

    public Collection<Artifact> getArtifacts() {
        return artifacts.values();
    }

    public Collection<Person> getPersons() { return persons.values(); }

    /* -------------------- Data Collection Callbacks -------------------- */

    private void getIDs() throws IOException {
        List<Callback> callbacks = new LinkedList<>();
        Callback[] c = new Callback[]{
                new GenericEntityCallback<>(artifacts,      Artifact.class,     "<wordnet_artifact"),
                new GenericEntityCallback<>(persons,       Person.class,      "<wordnet_creator"),
                new GenericEntityCallback<>(businesses,     Business.class,     "<wordnet_business"),
                new GenericEntityCallback<>(countries,      Country.class,      "<wikicat_Countries"),
                new GenericEntityCallback<>(cities,         City.class,         "<wikicat_Cities"),
                new GenericEntityCallback<>(persons,    Person.class,   "<wordnet_politician"),
                new GenericEntityCallback<>(universities,   University.class,   "<wordnet_university"),
        };
        Collections.addAll(callbacks, c);
        //FIXME: do we need to use the other attributes file?
        Utils.reduceEntitiesByAttributeFromCollectionWithMatcher(YAGOFilesLocation.YAGO_TYPES_FILE, callbacks);
    }


    @SafeVarargs
    private final void getNames(final Map<String, ? extends Entity>... entities_maps) throws IOException {
        List<Callback> callbacks = new LinkedList<>();
        for (final Map<String, ? extends Entity> entities : entities_maps) {
            callbacks.add(new GenericCallback(entities, ValueType.NAME, "skos:prefLabel", "name"));
        }
        Utils.reduceEntitiesByAttributeFromCollectionWithMatcher(YAGOFilesLocation.YAGO_LABELS_FILE, callbacks);
    }

    @SafeVarargs
    private final void getFacts(final Map<String, ? extends PopulatedRegion>... place_maps) throws IOException {
        String factFiles[] = new String[]{YAGOFilesLocation.YAGO_DATE_FACTS_FILE, YAGOFilesLocation.YAGO_FACTS_FILE, YAGOFilesLocation.YAGO_LITERAL_FACTS_FILE,};

        List<Callback> callbacks = new LinkedList<>();

        //Populated place callbacks
        for (Map<String, ? extends PopulatedRegion> places : place_maps) {
            callbacks.add(new GenericCallback(places, ValueType.STRING, "<isLocatedIn>",            "places", true, true));
            callbacks.add(new GenericCallback(places, ValueType.DATE,   "<wasCreatedOnDate>",       "creationDate"));
            callbacks.add(new GenericCallback(places, ValueType.FLOAT,  "<hasExport>",              "export"));
            callbacks.add(new GenericCallback(places, ValueType.FLOAT,  "<hasExpenses>",            "expenses"));
            callbacks.add(new GenericCallback(places, ValueType.FLOAT,  "<hasLatitude>",            "latitude"));
            callbacks.add(new GenericCallback(places, ValueType.FLOAT,  "<hasLongitude>",           "longitude"));
            callbacks.add(new GenericCallback(places, ValueType.FLOAT,  "<hasEconomicGrowth>",      "economicGrowth"));
            callbacks.add(new GenericCallback(places, ValueType.FLOAT,  "<hasPoverty>",             "poverty"));
            callbacks.add(new GenericCallback(places, ValueType.LONG,   "<hasNumberOfPeople>",      "population"));
            callbacks.add(new GenericCallback(places, ValueType.FLOAT,  "<hasUnemployment>",        "unemployment"));
            callbacks.add(new GenericCallback(places, ValueType.FLOAT,  "<hasRevenue>",             "revenue"));
            callbacks.add(new GenericCallback(places, ValueType.FLOAT,  "<hasGini>",                "gini"));
            callbacks.add(new GenericCallback(places, ValueType.FLOAT,  "<hasImport>",              "_import"));
            callbacks.add(new GenericCallback(places, ValueType.FLOAT,  "<hasGDP>",                 "gdp"));
            callbacks.add(new GenericCallback(places, ValueType.FLOAT,  "<hasInflation>",           "inflation"));
            callbacks.add(new GenericCallback(places, ValueType.FLOAT,  "<hasPopulationDensity>",   "populationDensity"));
        }
        //Country callbacks
        callbacks.add(new GenericCallback(countries,    ValueType.STRING,   "<hasTLD>",             "tld"));
        //City callbacks
        callbacks.add(new GenericObjectLinkCallback<>(cities,       countries,      City.class,         "<isLocatedIn>",    "country",      false, false));
        //University callbacks
        callbacks.add(new GenericObjectLinkCallback<>(universities, countries,      University.class,   "<isLocatedIn>",    "countries"));
        callbacks.add(new GenericObjectLinkCallback<>(universities, cities,         University.class,   "<isLocatedIn>",    "cities"));
        callbacks.add(new GenericCallback(universities, ValueType.DATE,     "<wasCreatedOnDate>",   "creationDate"));
        //Person callbacks
        callbacks.add(new GenericObjectLinkCallback<>(persons,  cities,         Person.class,   "<wasBornIn>",      "birthCity",    false, false));
        callbacks.add(new GenericObjectLinkCallback<>(persons,  cities,         Person.class,   "<diedIn>",         "deathCity",    false, false));
        callbacks.add(new GenericObjectLinkCallback<>(persons,  universities,   Person.class,   "<graduatedFrom>",  "universities"));
        callbacks.add(new GenericCallback(persons,  ValueType.DATE,     "<diedOnDate>",         "deathDate"));
        callbacks.add(new GenericCallback(persons,  ValueType.DATE,     "<wasBornOnDate>",      "birthDate"));
        //Business callbacks
        callbacks.add(new GenericObjectLinkCallback<>(businesses,   countries,      Business.class,     "<isLocatedIn>",    "countries"));
        callbacks.add(new GenericObjectLinkCallback<>(businesses,   cities,         Business.class,     "<isLocatedIn>",    "cities"));
        callbacks.add(new GenericCallback(businesses,   ValueType.DATE,     "<wasCreatedOnDate>",   "creationDate"));
        callbacks.add(new GenericCallback(businesses,   ValueType.LONG,     "<hasNumberOfPeople>",  "numberOfEmployees"));
        //Politician callbacks
        callbacks.add(new GenericObjectLinkCallback<>(persons,  countries,      Person.class,   "<isPoliticianOf>", "politicianOf"));
        //Creator callbacks
        callbacks.add(new GenericObjectLinkCallback<>(persons,     businesses,     Person.class,      "<created>",        "businesses"));
        //Artifact callbacks
        callbacks.add(new GenericObjectLinkCallback<>(artifacts,    businesses,     Artifact.class,     "<created>",        "businesses",   true, true));
        callbacks.add(new GenericObjectLinkCallback<>(artifacts,    persons,       Artifact.class,     "<created>",        "creators",     true, true));
        callbacks.add(new GenericCallback(artifacts,    ValueType.DATE,     "<wasCreatedOnDate>",   "creationDate"));

        for (String factFile : factFiles) {
            Utils.reduceEntitiesByAttributeFromCollectionWithMatcher(factFile, callbacks);
        }
    }

    /* -------------------- Data Post Processing -------------------- */

    private void postProcessData() {
        postCitiesProcessor();
        postUniversitiesProcessor();
        postBusinessesProcessor();
        postPersonProcessor();
        postArtifactProcessor();
    }

    private void postCitiesProcessor() {
        List<String> citiesToRemove = new ArrayList<>();

        citiesToRemove.addAll(cities.entrySet().stream().filter(entry -> entry.getValue().country == null).map(Map.Entry::getKey).collect(Collectors.toList()));
        citiesToRemove.forEach(cities::remove);

        System.out.println(String.format("Deleted %d cities", citiesToRemove.size()));
    }

    private void postUniversitiesProcessor() {
        List<String> universitiesToRemove = new ArrayList<>();

        for (Map.Entry<String, University> universitiesEntry : universities.entrySet()) {
            List<City> citiesToBeRemoved = universitiesEntry.getValue().cities.stream().
                    filter(city -> !cities.containsKey(city.getEntity())).
                    collect(Collectors.toCollection(LinkedList::new));
            citiesToBeRemoved.forEach(universitiesEntry.getValue().cities::remove);
            if (universitiesEntry.getValue().cities.isEmpty() && universitiesEntry.getValue().countries.isEmpty()) {
                universitiesToRemove.add(universitiesEntry.getKey());
            }
        }
        universitiesToRemove.forEach(universities::remove);
        System.out.println(String.format("Deleted %d universities", universitiesToRemove.size()));
    }

    private void postPersonProcessor() {
        List<String> personsToRemove = new ArrayList<>();

        for (Map.Entry<String, ? extends Person> personEntry : persons.entrySet()) {
            if (personEntry.getValue().birthCity != null &&
                    !cities.containsKey(personEntry.getValue().birthCity.getEntity())) {
                personEntry.getValue().birthCity = null;
            }
            if (personEntry.getValue().deathCity != null &&
                    !cities.containsKey(personEntry.getValue().deathCity.getEntity())) {
                personEntry.getValue().deathCity = null;
            }

            List<University> universitiesToBeRemoved = personEntry.getValue().universities.stream().
                    filter(university -> !universities.containsKey(university.getEntity())).
                    collect(Collectors.toCollection(LinkedList::new));
            universitiesToBeRemoved.forEach(personEntry.getValue().universities::remove);

            List<Business> businessesToBeRemoved = personEntry.getValue().businesses.stream().
                    filter(business -> !businesses.containsKey(business.getEntity())).
                    collect(Collectors.toCollection(LinkedList::new));
            businessesToBeRemoved.forEach(personEntry.getValue().businesses::remove);

            if (personEntry.getValue().birthCity == null && personEntry.getValue().deathCity == null &&
                    personEntry.getValue().politicianOf.isEmpty() && personEntry.getValue().businesses.isEmpty() &&
                    personEntry.getValue().universities.isEmpty()) {
                personsToRemove.add(personEntry.getKey());
            }
        }

        personsToRemove.forEach(persons::remove);
        System.out.println(String.format("Deleted %d persons", personsToRemove.size()));
    }

    private void postBusinessesProcessor() {
        List<String> businessesToRemove = new ArrayList<>();

        for (Map.Entry<String, Business> businessEntry : businesses.entrySet()) {
            List<City> citiesToBeRemoved = businessEntry.getValue().cities.stream().
                    filter(city -> !cities.containsKey(city.getEntity())).
                    collect(Collectors.toCollection(LinkedList::new));
            citiesToBeRemoved.forEach(businessEntry.getValue().cities::remove);
            if (businessEntry.getValue().countries.isEmpty() && businessEntry.getValue().cities.isEmpty()) {
                businessesToRemove.add(businessEntry.getKey());
            }
        }
        businessesToRemove.forEach(businesses::remove);
        System.out.println(String.format("Deleted %d businesses", businessesToRemove.size()));
    }

    private void postArtifactProcessor() {
        List<String> artifactsToRemove = new ArrayList<>();

        for (Map.Entry<String, Artifact> artifactEntry : artifacts.entrySet()) {
            List<Business> businessesToBeRemoves = artifactEntry.getValue().getBusinesses().stream().
                    filter(business -> !businesses.containsKey(business.getEntity())).
                    collect(Collectors.toCollection(LinkedList::new));
            businessesToBeRemoves.forEach(artifactEntry.getValue().getBusinesses()::remove);

            List<Person> creatorsToBeRemoved = artifactEntry.getValue().getCreators().stream().
                    filter(creator -> !persons.containsKey(creator.getEntity())).
                    collect(Collectors.toList());
            creatorsToBeRemoved.forEach(artifactEntry.getValue().getCreators()::remove);

            if (artifactEntry.getValue().getCreators().isEmpty() && artifactEntry.getValue().getBusinesses().isEmpty()) {
                artifactsToRemove.add(artifactEntry.getKey());
            }
        }
        artifactsToRemove.forEach(artifacts::remove);
        System.out.println(String.format("Deleted %d artifacts", artifactsToRemove.size()));
    }
}
