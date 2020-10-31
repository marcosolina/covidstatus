package com.marco.javacovidstatus.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * This class represents the data provided by the national institution
 * 
 * @author Marco
 *
 */
public class DailyData implements Serializable {
    private static final long serialVersionUID = 1L;
    private LocalDate date;
    private int newInfections;
    private int newTests;
    private int newCasualties;
    private BigDecimal infectionPercentage;
    private BigDecimal casualtiesPercentage;

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

    public BigDecimal getCasualtiesPercentage() {
        return casualtiesPercentage;
    }

    public void setCaualtiesPercentage(BigDecimal casualtiesPercentage) {
        this.casualtiesPercentage = casualtiesPercentage;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DailyData other = (DailyData) obj;
        if (date == null) {
            if (other.date != null)
                return false;
        } else if (!date.equals(other.date))
            return false;
        return true;
    }

}
