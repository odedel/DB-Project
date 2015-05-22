package main.data;

import main.util.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
        //noinspection unchecked
        getNames(artifacts, creators, businesses, countries, cities, politicians, universities);
        //noinspection unchecked
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

    public Collection<University> getUniversities() {
        return universities.values();
    }

    public Collection<Business> getBusinesses() {
        return businesses.values();
    }

    public Collection<Creator> getCreators() {
        return creators.values();
    }

    public Collection<Artifact> getArtifacts() {
        return artifacts.values();
    }

    private void getIDs() throws IOException {
        List<Callback> callbacks = new LinkedList<>();
        Callback[] c = new Callback[]{
                new GenericEntityCallback<>(artifacts, Artifact.class, "<wordnet_artifact"),
                new GenericEntityCallback<>(creators, Creator.class, "<wordnet_creator"),
                new GenericEntityCallback<>(businesses, Business.class, "<wordnet_business"),
                new GenericEntityCallback<>(countries, Country.class, "<wikicat_Countries"),
                new GenericEntityCallback<>(cities, City.class, "<wikicat_Cities"),
                new GenericEntityCallback<>(politicians, Politician.class, "<wordnet_politician"),
                new GenericEntityCallback<>(universities, University.class, "<wordnet_university"),
        };
        Collections.addAll(callbacks, c);
        //FIXME: do we need to use the other attributes file?
        Utils.reduceEntitiesByAttributeFromCollectionWithMatcher(Consts.YAGO_TYPES_FILE, callbacks);
    }


    @SafeVarargs
    private final void getNames(final Map<String, ? extends Entity>... entities_maps) throws IOException {
        List<Callback> callbacks = new LinkedList<>();
        for (final Map<String, ? extends Entity> entities : entities_maps) {
            callbacks.add(new GenericCallback(entities, ValueType.NAME, "skos:prefLabel", "name"));
        }
        Utils.reduceEntitiesByAttributeFromCollectionWithMatcher(Consts.YAGO_LABELS_FILE, callbacks);
    }

    @SafeVarargs
    private final void getFacts(final Map<String, ? extends PopulatedRegion>... place_maps) throws IOException {
        String factFiles[] = new String[]{Consts.YAGO_DATE_FACTS_FILE, Consts.YAGO_FACTS_FILE, Consts.YAGO_LITERAL_FACTS_FILE,};

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
        callbacks.add(new GenericObjectLinkCallback<>(politicians,  cities,         Politician.class,   "<wasBornIn>",      "birthCity",    false, false));
        callbacks.add(new GenericObjectLinkCallback<>(politicians,  cities,         Politician.class,   "<diedIn>",         "deathCity",    false, false));
        callbacks.add(new GenericObjectLinkCallback<>(politicians,  universities,   Politician.class,   "<graduatedFrom>",  "universities"));
        callbacks.add(new GenericCallback(politicians,  ValueType.DATE,     "<diedOnDate>",         "deathDate"));
        callbacks.add(new GenericCallback(politicians,  ValueType.DATE,     "<wasBornOnDate>",      "birthDate"));
        callbacks.add(new GenericObjectLinkCallback<>(creators,     cities,         Creator.class,      "<wasBornIn>",      "birthCity",    false, false));
        callbacks.add(new GenericObjectLinkCallback<>(creators,     cities,         Creator.class,      "<diedIn>",         "deathCity",    false, false));
        callbacks.add(new GenericObjectLinkCallback<>(creators,     universities,   Creator.class,      "<graduatedFrom>",  "universities"));
        callbacks.add(new GenericCallback(creators,     ValueType.DATE,     "<diedOnDate>",         "deathDate"));
        callbacks.add(new GenericCallback(creators,     ValueType.DATE,     "<wasBornOnDate>",      "birthDate"));
        //Business callbacks
        callbacks.add(new GenericObjectLinkCallback<>(businesses,   countries,      Business.class,     "<isLocatedIn>",    "countries"));
        callbacks.add(new GenericObjectLinkCallback<>(businesses,   cities,         Business.class,     "<isLocatedIn>",    "cities"));
        callbacks.add(new GenericCallback(businesses,   ValueType.DATE,     "<wasCreatedOnDate>",   "creationDate"));
        callbacks.add(new GenericCallback(businesses,   ValueType.LONG,     "<hasNumberOfPeople>",  "numberOfEmployees"));
        //Politician callbacks
        callbacks.add(new GenericObjectLinkCallback<>(politicians,  countries,      Politician.class,   "<isPoliticianOf>", "countries"));
        //Creator callbacks
        callbacks.add(new GenericObjectLinkCallback<>(creators,     businesses,     Creator.class,      "<created>",        "businesses"));
        //Artifact callbacks
        callbacks.add(new GenericObjectLinkCallback<>(artifacts,    businesses,     Artifact.class,     "<created>",        "businesses",   true, true));
        callbacks.add(new GenericObjectLinkCallback<>(artifacts,    creators,       Artifact.class,     "<created>",        "creators",     true, true));
        callbacks.add(new GenericCallback(artifacts,    ValueType.DATE,     "<wasCreatedOnDate>",   "creationDate"));

        for (String factFile : factFiles) {
            Utils.reduceEntitiesByAttributeFromCollectionWithMatcher(factFile, callbacks);
        }
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
                    filter(city -> !cities.containsKey(city.entity)).
                    collect(Collectors.toCollection(LinkedList::new));
            citiesToBeRemoved.forEach(universitiesEntry.getValue().cities::remove);
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
            List<University> universitiesToBeRemoved = personEntry.getValue().universities.stream().
                    filter(university -> !universities.containsKey(university.entity)).
                    collect(Collectors.toCollection(LinkedList::new));
            universitiesToBeRemoved.forEach(personEntry.getValue().universities::remove);
        }
    }

    private void postPoliticiansProcessor() {
        List<String> politiciansToRemove = politicians.entrySet().stream().
                filter(politicianEntry -> politicianEntry.getValue().countries.isEmpty()).
                map(Map.Entry::getKey).collect(Collectors.toList());
        politiciansToRemove.forEach(politicians::remove);
        postPersonProcessor(politicians);
        System.out.println(String.format("Deleted %d politicians", politiciansToRemove.size()));
    }

    private void postCreatorsProcessor() {
        postPersonProcessor(creators);
        List<String> creatorsToRemove = new ArrayList<>();
        for (Map.Entry<String, Creator> creatorEntry : creators.entrySet()) {
            if (creatorEntry.getValue().birthCity == null && creatorEntry.getValue().deathCity == null) {
                creatorsToRemove.add(creatorEntry.getKey());
            }

            List<Business> businessesToBeRemoved = creatorEntry.getValue().businesses.stream().
                    filter(business -> !businesses.containsKey(business.entity)).
                    collect(Collectors.toCollection(LinkedList::new));
            businessesToBeRemoved.forEach(creatorEntry.getValue().businesses::remove);
        }
        creatorsToRemove.forEach(creators::remove);
        System.out.println(String.format("Deleted %d creators", creatorsToRemove.size()));
    }

    private void postBusinessesProcessor() {
        List<String> businessesToRemove = new ArrayList<>();

        for (Map.Entry<String, Business> businessEntry : businesses.entrySet()) {
            List<City> citiesToBeRemoved = businessEntry.getValue().cities.stream().
                    filter(city -> !cities.containsKey(city.entity)).
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
            List<Business> businessesToBeRemoves = artifactEntry.getValue().businesses.stream().
                    filter(business -> !businesses.containsKey(business.entity)).
                    collect(Collectors.toCollection(LinkedList::new));
            businessesToBeRemoves.forEach(artifactEntry.getValue().businesses::remove);
            List<Creator> creatorsToBeRemoved = artifactEntry.getValue().creators.stream().
                    filter(creator -> !creators.containsKey(creator.entity)).
                    collect(Collectors.toList());
            creatorsToBeRemoved.forEach(artifactEntry.getValue().creators::remove);
            if (artifactEntry.getValue().creators.isEmpty() && artifactEntry.getValue().businesses.isEmpty()) {
                artifactsToRemove.add(artifactEntry.getKey());
            }
        }
        artifactsToRemove.forEach(artifacts::remove);
        System.out.println(String.format("Deleted %d artifacts", artifactsToRemove.size()));
    }
}
