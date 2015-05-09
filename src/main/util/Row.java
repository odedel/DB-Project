package main.util;

/**
 * Created by Tomer on 09/05/2015.
 */
public class Row {
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
