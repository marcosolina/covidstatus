package com.marco.javacovidstatus.model.rest;

import java.util.HashMap;
import java.util.Map;

import com.marco.javacovidstatus.model.dto.CharDataType;

/**
 * This represents the HTTP response when you try to retrieve the region data.
 * The key of the map is the region code
 * 
 * @author Marco
 *
 */
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
