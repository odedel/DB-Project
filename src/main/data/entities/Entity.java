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

    /** YAGO's entity */
    private String       entity;

    /** Entity's name */
    private String       name;
}
