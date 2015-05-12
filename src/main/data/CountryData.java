package main.data;

import main.util.Callback;
import main.util.Row;
import main.util.Utils;

import java.io.IOException;
import java.util.*;

public class CountryData {
    public static Map<String, Country> countries = new HashMap<>();
    public static Map<String, City> cities = new HashMap<>();
    static String COUNTRY_TYPE = "<wikicat_Countries>";
    static String CITY_TYPE = "<wikicat_Cities>";
    static String PREF_LABEL = "skos:prefLabel";

    public static Collection<Country> collectCountries() throws IOException {
        getCityIDs();
        getCountryIDs();
        getNames(countries, cities);
        getFacts(countries, cities);
        return countries.values();
    }

    private static void getCountryIDs() throws IOException {
        Utils.reduceEntitiesByAttributeFromCollectionWithMatcher("yago\\yagoTypes.tsv", new Callback() {
            @Override
            public void reduce(Row row) {
                countries.put(row.entity, new Country());
            }

            @Override
            public boolean map(Row row) {
                return row.superEntity.equals(COUNTRY_TYPE);
            }
        });
    }

    private static void getCityIDs() throws IOException {
        Utils.reduceEntitiesByAttributeFromCollectionWithMatcher("yago\\yagoTypes.tsv", new Callback() {
            @Override
            public void reduce(Row row) {
                cities.put(row.entity, new City());
            }

            @Override
            public boolean map(Row row) {
                return row.superEntity.startsWith("<wikicat_Cities");
            }
        });
    }

    private static void getNames(final Map<String, ? extends Place>... place_maps) throws IOException {
        List<Callback> callbacks = new LinkedList<>();
        for (final Map<String, ? extends Place> places : place_maps) {
            callbacks.add(new Callback() {
                @Override
                public void reduce(Row row) {
                    places.get(row.entity).name = row.superEntity;
                }

                @Override
                public boolean map(Row row) {
                    return row.relationType.equals(PREF_LABEL) && places.keySet().contains(row.entity);
                }
            });
        }
        Utils.reduceEntitiesByAttributeFromCollectionWithMatcher("yago\\yagoLabels.tsv", callbacks);
    }

    private static void getFacts(final Map<String, ? extends Place>... place_maps) throws IOException {
        String factFiles[] = new String[]{"yago\\yagoDateFacts.tsv", "yago\\yagoFacts.tsv", "yago\\yagoLiteralFacts.tsv",};

        List<Callback> callbacks = new LinkedList<>();
        for (Map<String, ? extends Place> places : place_maps) {
            callbacks.addAll(getCallbacks(places));
        }

        for (String factFile : factFiles) {
            Utils.reduceEntitiesByAttributeFromCollectionWithMatcher(factFile, callbacks);
        }
    }

    private static List<Callback> getCallbacks(final Map<String, ? extends Place> places) {
        Callback creationDate = new Callback() {
            @Override
            public void reduce(Row row) {
                places.get(row.entity).creationDate = row.superEntity;
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<wasCreatedOnDate>") && places.keySet().contains(row.entity);
            }
        };

        Callback _places = new Callback() {
            @Override
            public void reduce(Row row) {
                places.get(row.superEntity).places.add(row.entity);
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<isLocatedIn>") && places.keySet().contains(row.superEntity);
            }
        };

        Callback export = new Callback() {
            @Override
            public void reduce(Row row) {
                places.get(row.entity).export = row.superEntity;
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasExport>") && places.keySet().contains(row.entity);
            }
        };

        Callback expenses = new Callback() {
            @Override
            public void reduce(Row row) {
                places.get(row.entity).expenses = row.superEntity;
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasExpenses>") && places.keySet().contains(row.entity);
            }
        };

        Callback latitude = new Callback() {
            @Override
            public void reduce(Row row) {
                places.get(row.entity).latitude = row.superEntity;
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasLatitude>") && places.keySet().contains(row.entity);
            }
        };

        Callback longitude = new Callback() {
            @Override
            public void reduce(Row row) {
                places.get(row.entity).longitude = row.superEntity;
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasLongitude>") && places.keySet().contains(row.entity);
            }
        };

        Callback economicGrowth = new Callback() {
            @Override
            public void reduce(Row row) {
                places.get(row.entity).economicGrowth = row.superEntity;
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasEconomicGrowth>") && places.keySet().contains(row.entity);
            }
        };

        Callback poverty = new Callback() {
            @Override
            public void reduce(Row row) {
                places.get(row.entity).poverty = row.superEntity;
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasPoverty>") && places.keySet().contains(row.entity);
            }
        };

        Callback population = new Callback() {
            @Override
            public void reduce(Row row) {
                places.get(row.entity).population = row.superEntity;
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasNumberOfPeople>") && places.keySet().contains(row.entity);
            }
        };

        Callback unemployment = new Callback() {
            @Override
            public void reduce(Row row) {
                places.get(row.entity).unemployment = row.superEntity;
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasUnemployment>") && places.keySet().contains(row.entity);
            }
        };

        Callback revenue = new Callback() {
            @Override
            public void reduce(Row row) {
                places.get(row.entity).revenue = row.superEntity;
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasRevenue>") && places.keySet().contains(row.entity);
            }
        };

        Callback gini = new Callback() {
            @Override
            public void reduce(Row row) {
                places.get(row.entity).gini = row.superEntity;
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasGini>") && places.keySet().contains(row.entity);
            }
        };

        Callback _import = new Callback() {
            @Override
            public void reduce(Row row) {
                places.get(row.entity)._import = row.superEntity;
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasImport>") && places.keySet().contains(row.entity);
            }
        };

        Callback gdp = new Callback() {
            @Override
            public void reduce(Row row) {
                places.get(row.entity).gdp = row.superEntity;
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasGDP>") && places.keySet().contains(row.entity);
            }
        };

        Callback inflation = new Callback() {
            @Override
            public void reduce(Row row) {
                places.get(row.entity).inflation = row.superEntity;
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasInflation>") && places.keySet().contains(row.entity);
            }
        };

        Callback tld = new Callback() {
            @Override
            public void reduce(Row row) {
                places.get(row.entity).tld = row.superEntity;
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasTLD>") && places.keySet().contains(row.entity);
            }
        };

        Callback populationDensity = new Callback() {
            @Override
            public void reduce(Row row) {
                places.get(row.entity).populationDensity = row.superEntity;
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasPopulationDensity>") && places.keySet().contains(row.entity);
            }
        };

        List<Callback> callbacks = new LinkedList<>();
        Callback[] c = new Callback[]{creationDate, _places, export, expenses, latitude, longitude, economicGrowth, poverty, population, unemployment, revenue, gini, _import, gdp, inflation, tld, populationDensity};
        for (Callback callback : c) {
            callbacks.add(callback);
        }
        return callbacks;
    }
}
