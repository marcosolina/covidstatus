package com.marco.javacovidstatus.repositories.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * This entity is used to store the data retrieved from the national institution
 * 
 * @author Marco
 *
 */
@Entity
@Table(name = "NATIONAL_DATA")
public class EntityNationalData {
    @Id
    @Column(name = "DATE_DATA")
    private LocalDate date;
    @Column(name = "NEW_INFECTIONS")
    private int newInfections;
    @Column(name = "NEW_TESTS")
    private int newTests;
    @Column(name = "NEW_CASUALTIES")
    private int newCasualties;
    @Column(name = "INFECTION_PERC")
    private BigDecimal infectionPercentage;
    @Column(name = "CASUALTIES_PERC")
    private BigDecimal caualtiesPercentage;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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

    public BigDecimal getCaualtiesPercentage() {
        return caualtiesPercentage;
    }

    public void setCasualtiesPercentage(BigDecimal casualtiesPercentage) {
        this.caualtiesPercentage = casualtiesPercentage;
    }

}
