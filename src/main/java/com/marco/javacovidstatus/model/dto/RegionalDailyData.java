package com.marco.javacovidstatus.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * This class represents the data at the region level
 * 
 * @author Marco
 *
 */
public class RegionalDailyData implements Serializable {
    private static final long serialVersionUID = 1L;
    private LocalDate date;
    private String regionCode;
    private int newInfections;
    private int newTests;
    private int newCasualties;
    private int newHospitalized;
    private int newIntensiveTherapy;
    private int newRecovered;
    private BigDecimal infectionPercentage = BigDecimal.ZERO;
    private BigDecimal casualtiesPercentage = BigDecimal.ZERO;

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

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

    public static long getSerialversionuid() {
        return serialVersionUID;
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

    public void setCasualtiesPercentage(BigDecimal casualtiesPercentage) {
        this.casualtiesPercentage = casualtiesPercentage;
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
        RegionalDailyData other = (RegionalDailyData) obj;
        if (date == null) {
            if (other.date != null)
                return false;
        } else if (!date.equals(other.date))
            return false;
        return true;
    }

}
