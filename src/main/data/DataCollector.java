package main.data;

import main.util.Callback;
import main.util.Row;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DataCollector {

    private Map<String, Country> countries = new HashMap<>();

    private Map<String, City> cities = new HashMap<>();

    public void collectData() throws IOException {
        collectTypes();
        collectLabels();
//        collectFacts();
    }

    public Map<String, Country> getCountries() {
        return countries;
    }

    public Map<String, City> getCities() {
        return cities;
    }

    private void collectTypes() throws IOException {

        Callback collectCountries = new Callback() {
            @Override
            public void reduce(Row row) {
                countries.put(row.entity, new Country());
            }

            @Override
            public boolean map(Row row) {
                return row.superEntity.equals(COUNTRY_TYPE);
            }
        };

        Callback collectCities = new Callback() {
            @Override
            public void reduce(Row row) {
                cities.put(row.entity, new City());
            }

            @Override
            public boolean map(Row row) {
                return row.superEntity.equals(CITY_TYPE);
            }
        };

        reduceEntitiesByAttributeFromCollectionWithMatcher(YAGO_TYPES_FILE, collectCountries, collectCities);
    }

    private void collectLabels() throws IOException {
        Callback collectCountryLabels = new Callback() {
            @Override
            public void reduce(Row row) {
                countries.get(row.entity).name = parseNameFromPrefLabel(row.superEntity);
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals(PREF_LABEL) && countries.keySet().contains(row.entity);
            }
        };

        Callback collectCityLabels = new Callback() {
            @Override
            public void reduce(Row row) {
                cities.get(row.entity).name = parseNameFromPrefLabel(row.superEntity);
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals(PREF_LABEL) && cities.keySet().contains(row.entity);
            }
        };

        reduceEntitiesByAttributeFromCollectionWithMatcher(YAGO_LABELS_FILE, collectCountryLabels, collectCityLabels);
    }

    private void collectFacts() throws IOException {
        String factFiles[] = new String[]{YAGO_DATE_FACTS_FILE, YAGO_FACTS_FILE, YAGO_LITERAL_FACTS_FILE};

        Callback collectCreationDate = new Callback() {
            @Override
            public void reduce(Row row) {
                String dateString = row.superEntity.substring(1, row.superEntity.indexOf("^") - 1);
                countries.get(row.entity).creationDate =
                        LocalDate.parse(dateString, formatter);
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<wasCreatedOnDate>") && countries.keySet().contains(row.entity)
                        && !row.superEntity.contains("#") &&
                        row.superEntity.substring(1, row.superEntity.indexOf("^") - 1).length() == 10;
            }
        };

        Callback collectEconomicGrowth = new Callback() {
            @Override
            public void reduce(Row row) {
                countries.get(row.entity).economicGrowth = parseFloatFromString(row.superEntity);
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasEconomicGrowth>") && countries.keySet().contains(row.entity);
            }
        };

        Callback collectPoverty = new Callback() {
            @Override
            public void reduce(Row row) {
                countries.get(row.entity).poverty = parseFloatFromString(row.superEntity);
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasPoverty>") && countries.keySet().contains(row.entity);
            }
        };

        Callback collectPopulation = new Callback() {
            @Override
            public void reduce(Row row) {
                countries.get(row.entity).population = parseIntFromString(row.superEntity);
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasNumberOfPeople>") && countries.keySet().contains(row.entity);
            }
        };

        Callback collectUnemployment = new Callback() {
            @Override
            public void reduce(Row row) {
                countries.get(row.entity).unemployment = parseFloatFromString(row.superEntity);
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasUnemployment>") && countries.keySet().contains(row.entity);
            }
        };

        Callback collectGini = new Callback() {
            @Override
            public void reduce(Row row) {
                countries.get(row.entity).gini = parseFloatFromString(row.superEntity);
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasGini>") && countries.keySet().contains(row.entity);
            }
        };

        Callback collectInflation = new Callback() {
            @Override
            public void reduce(Row row) {
                countries.get(row.entity).inflation = parseFloatFromString(row.superEntity);
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasInflation>") && countries.keySet().contains(row.entity);
            }
        };

        Callback collectPopulationDensity = new Callback() {
            @Override
            public void reduce(Row row) {
                countries.get(row.entity).populationDensity = parseFloatFromString(row.superEntity);
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals("<hasPopulationDensity>") && countries.keySet().contains(row.entity);
            }
        };

        for (String factFile : factFiles) {
            reduceEntitiesByAttributeFromCollectionWithMatcher(factFile, collectCreationDate, collectEconomicGrowth,
                    collectPoverty, collectPopulation, collectUnemployment, collectGini, collectInflation,
                    collectPopulationDensity);
        }
    }

    private void reduceEntitiesByAttributeFromCollectionWithMatcher(String filePath, Callback... callbacks) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
        String line = reader.readLine();
        while (line != null) {
            String[] split = line.split("\t");
            Row row = new Row(split);
            for (Callback callback : callbacks) {
                if (callback.map(row)) {
                    callback.reduce(row);
                }
            }
            line = reader.readLine();
        }
    }

    private float parseFloatFromString(String s) {
        return Float.parseFloat(s.substring(1, s.indexOf("^") - 1));
    }

    private int parseIntFromString(String s) {
        return Integer.parseInt(s.substring(1, s.indexOf("^") - 1));
    }

    private String parseNameFromPrefLabel(String s) {
        return s.substring(1, s.lastIndexOf("@") - 1);
    }

    public static String COUNTRY_TYPE = "<wikicat_Countries>";

    private static String CITY_TYPE = "<wikicat_Port_cities>";

    public static String PREF_LABEL = "skos:prefLabel";

    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);

    public static final String YAGO_LABELS_FILE = "yago\\yagoLabels.tsv";

    public static final String YAGO_TYPES_FILE = "yago\\yagoTypes.tsv";

    public static final String YAGO_DATE_FACTS_FILE = "yago\\yagoDateFacts.tsv";

    public static final String YAGO_FACTS_FILE = "yago\\yagoFacts.tsv";

    public static final String YAGO_LITERAL_FACTS_FILE = "yago\\yagoLiteralFacts.tsv";
}
