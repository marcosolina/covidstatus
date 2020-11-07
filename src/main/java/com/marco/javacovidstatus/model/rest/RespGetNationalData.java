package com.marco.javacovidstatus.model.rest;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.marco.javacovidstatus.model.CharDataType;

public class RespGetNationalData extends RespDataSets implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<BigDecimal> arrPercInfections;
    private List<BigDecimal> arrPercCasualties;
    private List<Integer> arrNewTests;
    private List<Integer> arrNewInfections;
    private List<Integer> arrNewCasualties;
    private List<Integer> arrNewHospitalized;
    private List<Integer> arrNewIntensiveTherapy;
    private List<Integer> arrNewRecovered;

    public RespGetNationalData() {
        super(CharDataType.NATIONAL);
    }

    public List<BigDecimal> getArrPercInfections() {
        return arrPercInfections;
    }

    public void setArrPercInfections(List<BigDecimal> arrPercInfections) {
        this.arrPercInfections = arrPercInfections;
    }

    public List<BigDecimal> getArrPercCasualties() {
        return arrPercCasualties;
    }

    public void setArrPercCasualties(List<BigDecimal> arrPercCasualties) {
        this.arrPercCasualties = arrPercCasualties;
    }

    public List<Integer> getArrNewTests() {
        return arrNewTests;
    }

    public void setArrNewTests(List<Integer> arrNewTests) {
        this.arrNewTests = arrNewTests;
    }

    public List<Integer> getArrNewInfections() {
        return arrNewInfections;
    }

    public void setArrNewInfections(List<Integer> arrNewInfections) {
        this.arrNewInfections = arrNewInfections;
    }

    public List<Integer> getArrNewCasualties() {
        return arrNewCasualties;
    }

    public void setArrNewCasualties(List<Integer> arrNewCasualties) {
        this.arrNewCasualties = arrNewCasualties;
    }

    public List<Integer> getArrNewHospitalized() {
        return arrNewHospitalized;
    }

    public void setArrNewHospitalized(List<Integer> arrNewHospitalized) {
        this.arrNewHospitalized = arrNewHospitalized;
    }

    public List<Integer> getArrNewIntensiveTherapy() {
        return arrNewIntensiveTherapy;
    }

    public void setArrNewIntensiveTherapy(List<Integer> arrNewIntensiveTherapy) {
        this.arrNewIntensiveTherapy = arrNewIntensiveTherapy;
    }

    public List<Integer> getArrNewRecovered() {
        return arrNewRecovered;
    }

    public void setArrNewRecovered(List<Integer> arrNewRecovered) {
        this.arrNewRecovered = arrNewRecovered;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}
