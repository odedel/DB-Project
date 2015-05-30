package main.data.entities;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Artifact extends Entity {

    /** --- Ctors --- */
    public Artifact(String entity) {
        super(entity);
        this.businesses = new HashSet<>();
        this.creators = new HashSet<>();
    }

    /** --- Getter and Setter --- */

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public Set<Business> getBusinesses() {
        return businesses;
    }

    public void addBusiness(Business business) {
        this.businesses.add(business);
    }

    public Set<Person> getCreators() {
        return creators;
    }

    public void addCreator(Person creator) {
        this.creators.add(creator);
    }

    /** --- Data Members --- */

    private LocalDate creationDate;

    private Set<Business> businesses;

    private Set<Person> creators;


}
