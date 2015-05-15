package main.data;

import main.util.Callback;
import main.util.Row;
import main.util.Utils;

import java.io.IOException;
import java.util.*;

import static main.util.Utils.*;

public class CountryData {
    public static Map<String, Country> countries = new HashMap<>();
    public static Map<String, City> cities = new HashMap<>();
    static String COUNTRY_TYPE = "<wikicat_Countries>";
    static String CITY_TYPE = "<wikicat_Cities>";
    static String PREF_LABEL = "skos:prefLabel";

    public static Collection<Country> collectCountries() throws IOException {
        getIDs();
        getNames(countries, cities);
        getFacts(countries, cities);
        return countries.values();
    }


    private static void getIDs() throws IOException {
        List<Callback> callbacks = new LinkedList<>();
        callbacks.add(getCountryIDsCallback());
        callbacks.add(getCityIDsCallback());
        Utils.reduceEntitiesByAttributeFromCollectionWithMatcher(Consts.YAGO_TYPES_FILE, callbacks);
    }

    private static Callback getCountryIDsCallback() throws IOException {
       return new Callback() {
            @Override
            public void reduce(Row row) {
                countries.put(parseName(row.entity), new Country());
            }

            @Override
            public boolean map(Row row) {
                return row.superEntity.equals(COUNTRY_TYPE);
            }
        };
    }

    private static Callback getCityIDsCallback() throws IOException {
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

    private static void getNames(final Map<String, ? extends PopulatedRegion>... place_maps) throws IOException {
        List<Callback> callbacks = new LinkedList<>();
        for (final Map<String, ? extends PopulatedRegion> places : place_maps) {
            callbacks.add(new Callback() {
                @Override
                public void reduce(Row row) {
                    places.get(parseName(row.entity)).name = parseName(row.superEntity);
                }

                @Override
                public boolean map(Row row) {
                    return row.relationType.equals(PREF_LABEL) && places.keySet().contains(parseName(row.entity));
                }
            });
        }
        Utils.reduceEntitiesByAttributeFromCollectionWithMatcher(Consts.YAGO_LABELS_FILE, callbacks);
    }

    private static void getFacts(final Map<String, ? extends PopulatedRegion>... place_maps) throws IOException {
        String factFiles[] = new String[]{Consts.YAGO_DATE_FACTS_FILE, Consts.YAGO_FACTS_FILE, Consts.YAGO_LITERAL_FACTS_FILE,};

        List<Callback> callbacks = new LinkedList<>();
        for (Map<String, ? extends PopulatedRegion> places : place_maps) {
            callbacks.addAll(getCallbacks(places));
        }

        callbacks.addAll(getCountryCallbacks(countries));

        Callback cityLocatedIn = new Callback() {
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
        };
        callbacks.add(cityLocatedIn);

        for (String factFile : factFiles) {
            Utils.reduceEntitiesByAttributeFromCollectionWithMatcher(factFile, callbacks);
        }
    }
    private static List<Callback> getCountryCallbacks(final Map<String, ? extends Country> places) {
        Callback tld = new Callback() {
            @Override
            public void reduce(Row row) {
                places.get(parseName(row.entity)).tld = parseString(row.superEntity);
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasTLD>") && places.keySet().contains(parseName(row.entity));
            }
        };


        List<Callback> callbacks = new LinkedList<>();
        Callback[] c = new Callback[]{tld};
        for (Callback callback : c) {
            callbacks.add(callback);
        }
        return callbacks;
    }
    private static List<Callback> getCallbacks(final Map<String, ? extends PopulatedRegion> places) {

        Callback creationDate = new Callback() {
            @Override
            public void reduce(Row row) {
                places.get(parseName(row.entity)).creationDate = parseDate(row.superEntity);
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<wasCreatedOnDate>") && places.keySet().contains(parseName(row.entity));
            }
        };

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

        Callback export = new Callback() {
            @Override
            public void reduce(Row row) {
                places.get(parseName(row.entity)).export = parseFloat(row.superEntity);
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasExport>") && places.keySet().contains(parseName(row.entity));
            }
        };

        Callback expenses = new Callback() {
            @Override
            public void reduce(Row row) {
                places.get(parseName(row.entity)).expenses = parseFloat(row.superEntity);
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasExpenses>") && places.keySet().contains(parseName(row.entity));
            }
        };

        Callback latitude = new Callback() {
            @Override
            public void reduce(Row row) {
                places.get(parseName(row.entity)).latitude = parseFloat(row.superEntity);
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasLatitude>") && places.keySet().contains(parseName(row.entity));
            }
        };

        Callback longitude = new Callback() {
            @Override
            public void reduce(Row row) {
                places.get(parseName(row.entity)).longitude = parseFloat(row.superEntity);
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasLongitude>") && places.keySet().contains(parseName(row.entity));
            }
        };

        Callback economicGrowth = new Callback() {
            @Override
            public void reduce(Row row) {
                places.get(parseName(row.entity)).economicGrowth = parseFloat(row.superEntity);
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasEconomicGrowth>") && places.keySet().contains(parseName(row.entity));
            }
        };

        Callback poverty = new Callback() {
            @Override
            public void reduce(Row row) {
                places.get(parseName(row.entity)).poverty = parseFloat(row.superEntity);
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasPoverty>") && places.keySet().contains(parseName(row.entity));
            }
        };

        Callback population = new Callback() {
            @Override
            public void reduce(Row row) {
                places.get(parseName(row.entity)).population = parseLong(row.superEntity);
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasNumberOfPeople>") && places.keySet().contains(parseName(row.entity));
            }
        };

        Callback unemployment = new Callback() {
            @Override
            public void reduce(Row row) {
                places.get(parseName(row.entity)).unemployment = parseFloat(row.superEntity);
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasUnemployment>") && places.keySet().contains(parseName(row.entity));
            }
        };

        Callback revenue = new Callback() {
            @Override
            public void reduce(Row row) {
                places.get(parseName(row.entity)).revenue = parseFloat(row.superEntity);
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasRevenue>") && places.keySet().contains(parseName(row.entity));
            }
        };

        Callback gini = new Callback() {
            @Override
            public void reduce(Row row) {
                places.get(parseName(row.entity)).gini = parseFloat(row.superEntity);
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasGini>") && places.keySet().contains(parseName(row.entity));
            }
        };

        Callback _import = new Callback() {
            @Override
            public void reduce(Row row) {
                places.get(parseName(row.entity))._import = parseFloat(row.superEntity);
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasImport>") && places.keySet().contains(parseName(row.entity));
            }
        };

        Callback gdp = new Callback() {
            @Override
            public void reduce(Row row) {
                places.get(parseName(row.entity)).gdp = parseFloat(row.superEntity);
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasGDP>") && places.keySet().contains(parseName(row.entity));
            }
        };

        Callback inflation = new Callback() {
            @Override
            public void reduce(Row row) {
                places.get(parseName(row.entity)).inflation = parseFloat(row.superEntity);
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasInflation>") && places.keySet().contains(parseName(row.entity));
            }
        };

        Callback populationDensity = new Callback() {
            @Override
            public void reduce(Row row) {
                places.get(parseName(row.entity)).populationDensity = parseFloat(row.superEntity);
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasPopulationDensity>") && places.keySet().contains(parseName(row.entity));
            }
        };

        List<Callback> callbacks = new LinkedList<>();
        Callback[] c = new Callback[]{creationDate, _places, export, expenses, latitude, longitude, economicGrowth, poverty, population, unemployment, revenue, gini, _import, gdp, inflation, populationDensity};
        for (Callback callback : c) {
            callbacks.add(callback);
        }
        return callbacks;
    }
}
