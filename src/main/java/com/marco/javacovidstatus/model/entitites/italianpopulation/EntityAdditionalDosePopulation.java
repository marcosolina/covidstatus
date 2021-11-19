package com.marco.javacovidstatus.model.entitites.italianpopulation;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ADDITIONAL_DOSE_POPULATION")
public class EntityAdditionalDosePopulation {

    @Id
    @Column(name = "REGION_CODE")
    private String regionCode;
    @Column(name = "COUNTER")
    private int counter;

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

}
