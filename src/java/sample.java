import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.System;


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
class Sample {

    static String COUNTRY_TYPE = "<wikicat_Countries>";

    enum Type {
        ID, ENTITY, RELATION_TYPE, SUPER_ENTITY
    }

    public static void main(String args[]) throws Exception{
        FileInputStream fis = new FileInputStream("c:\\Users\\Tomer\\Documents\\DB-tau\\DB-Project\\yago\\yagoTypes.tsv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
        String line = reader.readLine();
        while(line != null){
            System.out.println(line);
            line = reader.readLine();
            String[] split = line.split("\t");
            Row row = new Row(split);
            if (row.relationType.equals(COUNTRY_TYPE)) {
                int i =0;
            }
        }


        System.out.println("hello");
    }
}