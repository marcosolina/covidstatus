package com.marco.javacovidstatus.model.rest.infections;

import java.util.List;

/**
 * This represents the list of data to return when an HTTP request is performed
 * to retrieve the region data
 * 
 * @author Marco
 *
 */
public class RespRegionChartData {
    private String label;
    private List<Object> data;

    public RespRegionChartData() {
    }

    public RespRegionChartData(String label, List<Object> data) {
        super();
        this.label = label;
        this.data = data;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }

}
