package com.marco.javacovidstatus.model.entitites.italianpopulation;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "ITALIAN_POPULATION")
public class EntityItalianPopulation {
    @EmbeddedId
    private ItalianPopulationPk id;
    @Column(name = "COUNTER")
    private int counter;

    public ItalianPopulationPk getId() {
        return id;
    }

    public void setId(ItalianPopulationPk id) {
        this.id = id;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

}
