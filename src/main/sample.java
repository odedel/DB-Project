package main;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.System;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


class Row {
    public String id;
    public String entity;
    public String relationType;
    public String superEntity;

    public Row(String id, String entity, String relationType, String superEntity) {
        this.id = id;
        this.entity = entity;
        this.relationType = relationType;
        this.superEntity = superEntity;
    }

    public Row(String[] split) {
        this.id = split[0];
        this.entity = split[1];
        this.relationType = split[2];
        this.superEntity = split[3];
    }
}

class Tuple {
    String first;
    String second;

    public Tuple(String first, String second) {
        this.first = first;
        this.second = second;
    }
}

enum Attribute {
    ID,
    FIRST_ENTITY,
    RELATION_TYPE,
    SECOND_ENTITY
}

abstract class Callback {

    public abstract void run(Row row);

    public abstract boolean match(Row row);
}

abstract class NCallback extends Callback {

    @Override
    public boolean match(Row row) {
        return false;
    }
}

class Country {
    public String name;
}

class Sample {

    static int i = 0;

    public static Map<String, Country> map = new HashMap<>();

    static String COUNTRY_TYPE = "<wikicat_Countries>";
    static String PREF_LABEL = "skos:prefLabel";

    enum Type {
        ID, ENTITY, RELATION_TYPE, SUPER_ENTITY
    }

    public static void main(String args[]) throws Exception {
        collectCountries();
    }

    private static void collectCountries() throws IOException {
        getCountryIDs();
        getCountryNames(map.keySet());
    }

    private static void getCountryNames(final Collection<String> countries) throws IOException {
        FileInputStream fis = new FileInputStream("c:\\Users\\Tomer\\Documents\\DB-tau\\DB-Project\\yago\\yagoLabels.tsv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
        reduceEntitiesByAttributeFromCollectionWithMatcher(reader, new Callback() {
            @Override
            public void run(Row row) {
                map.get(row.entity).name = row.superEntity;
                System.out.println("Collected country " + i++);
            }

            @Override
            public boolean match(Row row) {
                return row.relationType.equals(PREF_LABEL) && countries.contains(row.entity);
            }
        });
    }

    private static void getCountryIDs() throws IOException {
        FileInputStream fis = new FileInputStream("c:\\Users\\Tomer\\Documents\\DB-tau\\DB-Project\\yago\\yagoTypes.tsv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
        reduceEntitiesByAttributeFromCollectionWithMatcher(reader, new Callback() {
            @Override
            public void run(Row row) {
                map.put(row.entity, new Country());
            }

            @Override
            public boolean match(Row row) {
                return row.superEntity.equals(COUNTRY_TYPE);
            }
        });
    }

    private static void reduceEntitiesByAttributeFromCollectionWithMatcher(BufferedReader reader, Callback callback) throws IOException {
        String line;
        line = reader.readLine();
        while (line != null) {
            String[] split = line.split("\t");
            Row row = new Row(split);
            if (callback.match(row)) {
                callback.run(row);
            }
            line = reader.readLine();
        }
    }

//    private static Collection<String> collectEntitiesByRelation(BufferedReader reader, String relationType) throws IOException {
//        return collectEntitiesByAttribute(reader, PREF_LABEL, Attribute.RELATION_TYPE, Attribute.FIRST_ENTITY);
//    }


}