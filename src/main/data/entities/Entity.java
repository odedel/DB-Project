package main.data.entities;

public class Entity {

    /* --- Ctors --- */

    public Entity(String entity) {
        this.entity = entity;
    }


    /** --- Getters and Setters --- */

    public String getName() {
        return name;
    }

    public String getEntity() {
        return entity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) { this.id = id; }


    /** --- Date Members --- */

    /** YAGO's entity */
    protected String       entity;

    /** Entity's name */
    protected String       name;

    /** Entity's ID in the DB */
    private int         id;
}
