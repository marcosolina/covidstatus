package com.marco.javacovidstatus.model.dto;

public class AdditionalDosePopulationDto {
    private String regionCode;
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
