package com.marco.javacovidstatus.model.rest;

import java.util.HashMap;
import java.util.Map;

import com.marco.javacovidstatus.model.dto.CharDataType;

public class RespGetProvinceData extends RespDataSets {

    private Map<String, RespProvinceChartData> provinceData = new HashMap<>();

    public RespGetProvinceData() {
        super(CharDataType.PROVINCE);
    }

    public Map<String, RespProvinceChartData> getProvinceData() {
        return provinceData;
    }

    public void setProvinceData(Map<String, RespProvinceChartData> provinceData) {
        this.provinceData = provinceData;
    }

}
