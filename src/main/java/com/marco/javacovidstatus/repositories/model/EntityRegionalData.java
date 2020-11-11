package com.marco.javacovidstatus.repositories.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * This entity is used to store the data retrieved from the national institution
 * 
 * @author Marco
 *
 */
@Entity
@Table(name = "REGIONAL_DATA")
public class EntityRegionalData {

    @EmbeddedId
    private EntityRegionalDataPk id;
    @Column(name = "NEW_INFECTIONS")
    private int newInfections;
    @Column(name = "NEW_TESTS")
    private int newTests;
    @Column(name = "NEW_CASUALTIES")
    private int newCasualties;
    @Column(name = "NEW_HOSPITALIZED")
    private int newHospitalized;
    @Column(name = "NEW_INTENSIVE_THERAPY")
    private int newIntensiveTherapy;
    @Column(name = "NEW_RECOVERED")
    private int newRecovered;
    @Column(name = "INFECTION_PERC")
    private BigDecimal infectionPercentage;
    @Column(name = "CASUALTIES_PERC")
    private BigDecimal casualtiesPercentage;

    public EntityRegionalDataPk getId() {
        return id;
    }

    public void setId(EntityRegionalDataPk id) {
        this.id = id;
    }

    public int getNewInfections() {
        return newInfections;
    }

    public void setNewInfections(int newInfections) {
        this.newInfections = newInfections;
    }

    public int getNewTests() {
        return newTests;
    }

    public void setNewTests(int newTests) {
        this.newTests = newTests;
    }

    public int getNewCasualties() {
        return newCasualties;
    }

    public void setNewCasualties(int newCasualties) {
        this.newCasualties = newCasualties;
    }

    public BigDecimal getInfectionPercentage() {
        return infectionPercentage;
    }

    public void setInfectionPercentage(BigDecimal infectionPercentage) {
        this.infectionPercentage = infectionPercentage;
    }

    public int getNewHospitalized() {
        return newHospitalized;
    }

    public void setNewHospitalized(int newHospitalized) {
        this.newHospitalized = newHospitalized;
    }

    public int getNewIntensiveTherapy() {
        return newIntensiveTherapy;
    }

    public void setNewIntensiveTherapy(int newIntensiveTherapy) {
        this.newIntensiveTherapy = newIntensiveTherapy;
    }

    public int getNewRecovered() {
        return newRecovered;
    }

    public void setNewRecovered(int newRecovered) {
        this.newRecovered = newRecovered;
    }

    public BigDecimal getCasualtiesPercentage() {
        return casualtiesPercentage;
    }

    public void setCasualtiesPercentage(BigDecimal casualtiesPercentage) {
        this.casualtiesPercentage = casualtiesPercentage;
    }

}
