package com.marco.javacovidstatus.model.rest;

import java.io.Serializable;
import java.time.LocalDate;

import com.marco.javacovidstatus.model.dto.CovidDataType;

public class ReqGetRegionData implements Serializable {
    private static final long serialVersionUID = 1L;
    private LocalDate from;
    private LocalDate to;
    private CovidDataType covidData;

    public CovidDataType getCovidData() {
        return covidData;
    }

    public void setCovidData(CovidDataType covidData) {
        this.covidData = covidData;
    }

    public LocalDate getFrom() {
        return from;
    }

    public void setFrom(LocalDate from) {
        this.from = from;
    }

    public LocalDate getTo() {
        return to;
    }

    public void setTo(LocalDate to) {
        this.to = to;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}
