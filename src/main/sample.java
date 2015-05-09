package main;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.System;
import java.util.*;


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

    public abstract void reduce(Row row);

    public abstract boolean map(Row row);
}

abstract class NCallback extends Callback {

    @Override
    public boolean map(Row row) {
        return false;
    }
}

class Country {
    public String name;
    public List<Row> facts = new LinkedList<>();
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
        getCountryNames();
        getCountryFacts();
        int i = 0;
    }

    private static void getCountryNames() throws IOException {
        FileInputStream fis = new FileInputStream("yago\\yagoLabels.tsv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
        reduceEntitiesByAttributeFromCollectionWithMatcher(reader, new Callback() {
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
        FileInputStream fis = new FileInputStream("yago\\yagoTypes.tsv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
        reduceEntitiesByAttributeFromCollectionWithMatcher(reader, new Callback() {
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
        String factFiles[] = new String[]{"yago\\yagoDateFacts.tsv", "yago\\yagoFacts.tsv", "yago\\yagoLiteralFacts.tsv", };
        for(String factFile : factFiles) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(factFile)));
            reduceEntitiesByAttributeFromCollectionWithMatcher(reader, new Callback() {
                @Override
                public void reduce(Row row) {
                    if(map.containsKey(row.entity)) {
                        map.get(row.entity).facts.add(row);
                    }
                    if(map.containsKey(row.superEntity)) {
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

    private static void reduceEntitiesByAttributeFromCollectionWithMatcher(BufferedReader reader, Callback callback) throws IOException {
        String line;
        line = reader.readLine();
        while (line != null) {
            String[] split = line.split("\t");
            Row row = new Row(split);
            if (callback.map(row)) {
                callback.reduce(row);
            }
            line = reader.readLine();
        }
    }

//    private static Collection<String> collectEntitiesByRelation(BufferedReader reader, String relationType) throws IOException {
//        return collectEntitiesByAttribute(reader, PREF_LABEL, Attribute.RELATION_TYPE, Attribute.FIRST_ENTITY);
//    }


}