package com.marco.javacovidstatus.model.rest;

import java.util.HashMap;
import java.util.Map;

import com.marco.javacovidstatus.model.CharDataType;

public class RespGetRegionData extends RespDataSets {

    private Map<String, RespRegionChartData> regionData = new HashMap<>();

    public RespGetRegionData() {
        super(CharDataType.REGIONAL);
    }

    public Map<String, RespRegionChartData> getRegionData() {
        return regionData;
    }

    public void setRegionData(Map<String, RespRegionChartData> regionData) {
        this.regionData = regionData;
    }

}
