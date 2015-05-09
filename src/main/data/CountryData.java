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
    public static Map<String, Country> map = new HashMap<>();
    static int i = 0;
    static String COUNTRY_TYPE = "<wikicat_Countries>";
    static String PREF_LABEL = "skos:prefLabel";

    public static void collectCountries() throws IOException {
        getCountryIDs();
        getCountryNames();
        getCountryFacts();
        int i = 0;
    }

    private static void getCountryNames() throws IOException {
        Utils.reduceEntitiesByAttributeFromCollectionWithMatcher("yago\\yagoLabels.tsv", new Callback() {
            @Override
            public void reduce(Row row) {
                map.get(row.entity).name = row.superEntity;
                System.out.println("Collected country " + i++);
            }

            @Override
            public boolean map(Row row) {
                return row.relationType.equals(PREF_LABEL) && map.keySet().contains(row.entity);
            }
        });
    }

    private static void getCountryIDs() throws IOException {
        Utils.reduceEntitiesByAttributeFromCollectionWithMatcher("yago\\yagoTypes.tsv", new Callback() {
            @Override
            public void reduce(Row row) {
                map.put(row.entity, new Country());
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
                    if (map.containsKey(row.entity)) {
                        map.get(row.entity).facts.add(row);
                    }
                    if (map.containsKey(row.superEntity)) {
                        map.get(row.superEntity).facts.add(row);
                    }
                }

                @Override
                public boolean map(Row row) {
                    return map.keySet().contains(row.entity) || map.keySet().contains(row.superEntity);
                }
            });
        }
    }
}
