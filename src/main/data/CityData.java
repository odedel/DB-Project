//package main.data;
//
//import main.util.Callback;
//import main.util.Row;
//import main.util.Utils;
//
//import java.io.IOException;
//import java.time.format.DateTimeFormatter;
//import java.util.HashMap;
//import java.util.Locale;
//import java.util.Map;
//
//public class CityData {
//
//    public static Map<String, City> cities = new HashMap<>();
//    private static String CITY_TYPE = "<wikicat_Port_cities>";  // Change this
//    private static String PREF_LABEL = "skos:prefLabel";
//    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
//
//    public static Map<String, City> collectCities(Map<String, Country> countries) throws IOException {
//        getCities();
//        getCityNames();
//        getRelatedCountry(countries);
//        return cities;
//    }
//
//    private static void getCities() throws IOException {
//        Utils.reduceEntitiesByAttributeFromCollectionWithMatcher(Consts.YAGO_TYPES_FILE, new Callback() {
//            @Override
//            public void reduce(Row row) {
//                cities.put(row.entity, new City());
//            }
//
//            @Override
//            public boolean map(Row row) {
//                return row.superEntity.equals(CITY_TYPE);
//            }
//        });
//    }
//
//    private static void getCityNames() throws IOException {
//        Utils.reduceEntitiesByAttributeFromCollectionWithMatcher(Consts.YAGO_LABELS_FILE, new Callback() {
//            @Override
//            public void reduce(Row row) {
//                cities.get(row.entity).name = Utils.parseNameFromPrefLabel(row.superEntity);
//            }
//
//            @Override
//            public boolean map(Row row) {
//                return row.relationType.equals(PREF_LABEL) && cities.keySet().contains(row.entity);
//            }
//        });
//    }
//
//    private static void getRelatedCountry(Map<String, Country> countries) throws IOException {
//        Utils.reduceEntitiesByAttributeFromCollectionWithMatcher(Consts.YAGO_FACTS_FILE, new Callback() {
//            @Override
//            public void reduce(Row row) {
//                cities.get(row.entity).country = countries.get(row.superEntity);
//            }
//
//            @Override
//            public boolean map(Row row) {
//                return row.relationType.equals("<isLocatedIn>") && cities.keySet().contains(row.entity)
//                        && countries.containsKey(row.superEntity);
//            }
//        });
//    }
//}
