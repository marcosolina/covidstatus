package com.marco.javacovidstatus.model.entitites.italianpopulation;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "BOOSTER_POPULATION")
public class EntityBoosterPopulation {
    @EmbeddedId
    private EntityBoosterPopulationPk id;
    @Column(name = "COUNTER")
    private int counter;

    public EntityBoosterPopulationPk getId() {
        return id;
    }

    public void setId(EntityBoosterPopulationPk id) {
        this.id = id;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

}
