package main.data;

import main.util.Callback;
import main.util.Row;
import main.util.Utils;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CountryData {
    public static Map<String, Country> countries = new HashMap<>();
    static String COUNTRY_TYPE = "<wikicat_Countries>";
    static String PREF_LABEL = "skos:prefLabel";

    public static Collection<Country> collectCountries() throws IOException {
        getCountryIDs();
        getCountryNames();
        getCountryFacts();
        return countries.values();
    }

    private static void getCountryNames() throws IOException {
        Utils.reduceEntitiesByAttributeFromCollectionWithMatcher("yago\\yagoLabels.tsv", new Callback() {
            @Override
            public void reduce(Row row) {
                countries.get(row.entity).name = row.superEntity;
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals(PREF_LABEL) && countries.keySet().contains(row.entity);
            }
        });
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

    private static void getCountryFacts() throws IOException {
        String factFiles[] = new String[]{"yago\\yagoDateFacts.tsv", "yago\\yagoFacts.tsv", "yago\\yagoLiteralFacts.tsv",};

        Callback creationDate = new Callback() {
            @Override
            public void reduce(Row row) {
                countries.get(row.entity).creationDate = row.superEntity;
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<wasCreatedOnDate>") && countries.keySet().contains(row.entity);
            }
        };

        Callback places = new Callback() {
            @Override
            public void reduce(Row row) {
                countries.get(row.superEntity).places.add(row.entity);
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<isLocatedIn>") && countries.keySet().contains(row.superEntity);
            }
        };

        Callback export = new Callback() {
            @Override
            public void reduce(Row row) {
                countries.get(row.entity).export = row.superEntity;
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasExport>") && countries.keySet().contains(row.entity);
            }
        };

        Callback expenses = new Callback() {
            @Override
            public void reduce(Row row) {
                countries.get(row.entity).expenses = row.superEntity;
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasExpenses>") && countries.keySet().contains(row.entity);
            }
        };

        Callback latitude = new Callback() {
            @Override
            public void reduce(Row row) {
                countries.get(row.entity).latitude = row.superEntity;
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasLatitude>") && countries.keySet().contains(row.entity);
            }
        };

        Callback longitude = new Callback() {
            @Override
            public void reduce(Row row) {
                countries.get(row.entity).longitude = row.superEntity;
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasLongitude>") && countries.keySet().contains(row.entity);
            }
        };

        Callback economicGrowth = new Callback() {
            @Override
            public void reduce(Row row) {
                countries.get(row.entity).economicGrowth = row.superEntity;
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasEconomicGrowth>") && countries.keySet().contains(row.entity);
            }
        };

        Callback poverty = new Callback() {
            @Override
            public void reduce(Row row) {
                countries.get(row.entity).poverty = row.superEntity;
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasPoverty>") && countries.keySet().contains(row.entity);
            }
        };

        Callback population = new Callback() {
            @Override
            public void reduce(Row row) {
                countries.get(row.entity).population = row.superEntity;
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasNumberOfPeople>") && countries.keySet().contains(row.entity);
            }
        };

        Callback unemployment = new Callback() {
            @Override
            public void reduce(Row row) {
                countries.get(row.entity).unemployment = row.superEntity;
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasUnemployment>") && countries.keySet().contains(row.entity);
            }
        };

        Callback revenue = new Callback() {
            @Override
            public void reduce(Row row) {
                countries.get(row.entity).revenue = row.superEntity;
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasRevenue>") && countries.keySet().contains(row.entity);
            }
        };

        Callback gini = new Callback() {
            @Override
            public void reduce(Row row) {
                countries.get(row.entity).gini = row.superEntity;
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasGini>") && countries.keySet().contains(row.entity);
            }
        };

        Callback _import = new Callback() {
            @Override
            public void reduce(Row row) {
                countries.get(row.entity)._import = row.superEntity;
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasImport>") && countries.keySet().contains(row.entity);
            }
        };

        Callback gdp = new Callback() {
            @Override
            public void reduce(Row row) {
                countries.get(row.entity).gdp = row.superEntity;
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasGDP>") && countries.keySet().contains(row.entity);
            }
        };

        Callback inflation = new Callback() {
            @Override
            public void reduce(Row row) {
                countries.get(row.entity).inflation = row.superEntity;
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasInflation>") && countries.keySet().contains(row.entity);
            }
        };

        Callback tld = new Callback() {
            @Override
            public void reduce(Row row) {
                countries.get(row.entity).tld = row.superEntity;
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasTLD>") && countries.keySet().contains(row.entity);
            }
        };

        Callback populationDensity = new Callback() {
            @Override
            public void reduce(Row row) {
                countries.get(row.entity).populationDensity = row.superEntity;
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasPopulationDensity>") && countries.keySet().contains(row.entity);
            }
        };

        for (String factFile : factFiles) {
            Utils.reduceEntitiesByAttributeFromCollectionWithMatcher(factFile, creationDate, places, export, expenses, latitude, longitude, economicGrowth, poverty, population, unemployment, revenue, gini, _import, gdp, inflation, tld, populationDensity);
        }

//        for (String factFile : factFiles) {
//            Utils.reduceEntitiesByAttributeFromCollectionWithMatcher(factFile, new Callback() {
//                @Override
//                public void reduce(Row row) {
//                    if (countries.containsKey(row.entity)) {
//                        countries.get(row.entity).facts.add(row);
//                    }
//                    if (countries.containsKey(row.superEntity)) {
//                        countries.get(row.superEntity).facts.add(row);
//                    }
//                }
//
//                @Override
//                public boolean map(Row row) {
//                    return countries.keySet().contains(row.entity) || countries.keySet().contains(row.superEntity);
//                }
//            });
//        }
    }
}
