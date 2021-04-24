package com.marco.javacovidstatus.model.entitites.italianpopulation;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Entity which stores the population info retrieved from the ISTAT
 * 
 * @author Marco
 *
 */
@Entity
@Table(name = "ITALIAN_POPULATION")
public class EntityItalianPopulation {
    @EmbeddedId
    private EntityItalianPopulationPk id;
    @Column(name = "COUNTER")
    private int counter;

    public EntityItalianPopulationPk getId() {
        return id;
    }

    public void setId(EntityItalianPopulationPk id) {
        this.id = id;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

}
