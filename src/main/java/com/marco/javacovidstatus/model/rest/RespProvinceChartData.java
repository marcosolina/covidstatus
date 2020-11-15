package com.marco.javacovidstatus.model.rest;

import java.util.List;

/**
 * This represents the list of data to return when an HTTP request is performed
 * to retrieve the province data
 * 
 * @author Marco
 *
 */
public class RespProvinceChartData {
    private String label;
    private List<Integer> newInfections;

    public RespProvinceChartData() {
    }

    public RespProvinceChartData(String label, List<Integer> newInfections) {
        super();
        this.label = label;
        this.newInfections = newInfections;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<Integer> getNewInfections() {
        return newInfections;
    }

    public void setNewInfections(List<Integer> newInfections) {
        this.newInfections = newInfections;
    }

}
