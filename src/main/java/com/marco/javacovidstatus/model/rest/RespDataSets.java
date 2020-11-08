package com.marco.javacovidstatus.model.rest;

import java.time.LocalDate;
import java.util.List;

import com.marco.javacovidstatus.model.CharDataType;
import com.marco.utils.http.MarcoResponse;

public abstract class RespDataSets extends MarcoResponse {
    private List<LocalDate> arrDates;
    private CharDataType dataType;
    
    public RespDataSets(CharDataType dataType) {
        this.dataType = dataType;
    }

    public List<LocalDate> getArrDates() {
        return arrDates;
    }

    public void setArrDates(List<LocalDate> arrDates) {
        this.arrDates = arrDates;
    }

    public CharDataType getDataType() {
        return this.dataType;
    }

}