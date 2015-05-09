import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.System;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;


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

class Sample {

    static String COUNTRY_TYPE = "<wikicat_Countries>";
    static String PREF_LABEL = "skos:prefLabel";

    enum Type {
        ID, ENTITY, RELATION_TYPE, SUPER_ENTITY
    }

    public static void main(String args[]) throws Exception {
        Collection<String> countries = collectCountries();
        System.out.println(countries);


    }

    private static Collection<String> collectCountries() throws IOException {
        Collection<String> countries = getCountryIDs();
        //Collection<String> countries = Collections.singletonList("<id_1lqi1ft_88c_1ihryd7>");
        System.out.println(1);
        Collection<String> countryNames = getCountryNames(countries);
        System.out.println(2);
        int i = 2;
        return countryNames;
    }

    private static Collection<String> getCountryNames(Collection<String> countries) throws IOException {
        FileInputStream fis = new FileInputStream("c:\\Users\\Tomer\\Documents\\DB-tau\\DB-Project\\yago\\yagoLabels.tsv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
        return reduceEntitiesByAttributeFromCollection(reader, PREF_LABEL, Attribute.RELATION_TYPE, countries);
    }

    private static Collection<String> getCountryIDs() throws IOException {
        FileInputStream fis = new FileInputStream("c:\\Users\\Tomer\\Documents\\DB-tau\\DB-Project\\yago\\yagoTypes.tsv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
        return collectEntitiesByAttribute(reader, COUNTRY_TYPE, Attribute.SECOND_ENTITY, Attribute.FIRST_ENTITY);
    }

    private static Collection<String> collectEntitiesByAttribute(BufferedReader reader, String entityType, Attribute attribute, Attribute collectedAttribute) throws IOException {
        String line;
        Collection<String> collection = new LinkedList<>();
        line = reader.readLine();
        while (line != null) {
            String[] split = line.split("\t");
            if (split[attribute.ordinal()].equals(entityType)) {
                collection.add(split[collectedAttribute.ordinal()]);
            }
            line = reader.readLine();
        }
        return collection;
    }

    private static Collection<String> reduceEntitiesByAttributeFromCollection(BufferedReader reader, String entityType, Attribute attribute, Collection<String> ids) throws IOException {
        String line;
        Collection<String> collection = new LinkedList<>();
        line = reader.readLine();
        Collection<String> iids = new LinkedList<>();
        while (line != null) {
            String[] split = line.split("\t");
            if (split[attribute.ordinal()].equals(entityType)) {
                if (ids.contains(split[1])) {
                    collection.add(split[Attribute.ID.ordinal()]);
                }
            }
            line = reader.readLine();
        }
        return collection;
    }

    private static Collection<String> collectEntitiesByRelation(BufferedReader reader, String relationType) throws IOException {
        return collectEntitiesByAttribute(reader, PREF_LABEL, Attribute.RELATION_TYPE, Attribute.FIRST_ENTITY);
    }


}