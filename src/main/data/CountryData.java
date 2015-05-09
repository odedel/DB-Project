package main.data;

import main.util.Callback;
import main.util.Row;
import main.util.Utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tomer on 09/05/2015.
 */
public class CountryData {
    public static Map<String, Country> countries = new HashMap<>();
    static String COUNTRY_TYPE = "<wikicat_Countries>";
    static String PREF_LABEL = "skos:prefLabel";

    public static Map<String, Country> collectCountries() throws IOException {
        getCountryIDs();
        getCountryNames();
        getCountryFacts();
        return countries;
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
        for (String factFile : factFiles) {
            Utils.reduceEntitiesByAttributeFromCollectionWithMatcher(factFile, new Callback() {
                @Override
                public void reduce(Row row) {
                    if (countries.containsKey(row.entity)) {
                        countries.get(row.entity).facts.add(row);
                    }
                    if (countries.containsKey(row.superEntity)) {
                        countries.get(row.superEntity).facts.add(row);
                    }
                }

                @Override
                public boolean map(Row row) {
                    return countries.keySet().contains(row.entity) || countries.keySet().contains(row.superEntity);
                }
            });
        }
    }
}
