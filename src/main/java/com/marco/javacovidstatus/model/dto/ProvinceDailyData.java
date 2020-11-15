package com.marco.javacovidstatus.model.dto;

import java.io.Serializable;
import java.time.LocalDate;

public class ProvinceDailyData implements Serializable {

    private static final long serialVersionUID = 1L;
    private LocalDate date;
    private String regionCode;
    private String provinceCode;
    private String regionDesc;
    private String description;
    private String shortName;
    private int newInfections;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public int getNewInfections() {
        return newInfections;
    }

    public void setNewInfections(int newInfections) {
        this.newInfections = newInfections;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getRegionDesc() {
        return regionDesc;
    }

    public void setRegionDesc(String regionDesc) {
        this.regionDesc = regionDesc;
    }

}
