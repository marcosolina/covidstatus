package com.marco.javacovidstatus.model.rest.infections;

import java.util.HashMap;
import java.util.Map;

import com.marco.javacovidstatus.model.rest.RespDataSets;

/**
 * This represents the HTTP response when you try to retrieve the province data.
 * The key of the map is the province code
 * 
 * @author Marco
 *
 */
public class RespGetProvinceData extends RespDataSets {

	private Map<String, RespProvinceChartData> provinceData = new HashMap<>();

	public Map<String, RespProvinceChartData> getProvinceData() {
		return provinceData;
	}

	public void setProvinceData(Map<String, RespProvinceChartData> provinceData) {
		this.provinceData = provinceData;
	}

}
