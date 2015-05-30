package main.data.entities;

public class Entity {

    public Entity(String entity) {
        this.entity = entity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /** YAGO's entity */
    private String       entity;

    /** Entity's name */
    private String       name;

    /** Entity's ID in the DB */
    private int         id;
}
