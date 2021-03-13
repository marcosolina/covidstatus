package com.marco.javacovidstatus.model.rest.vaccines;

import java.util.Map;

import com.marco.javacovidstatus.model.dto.VacinesTotalDeliveredGivenPerRegionDto;
import com.marco.javacovidstatus.model.rest.RespDataSets;

/**
 * This represents the response model return when the client requests the totals
 * per region
 * 
 * @author Marco
 *
 */
public class RespGetTotalDelivereUsedVaccineDataPerRegion extends RespDataSets {
	private Map<String, VacinesTotalDeliveredGivenPerRegionDto> data;

	public Map<String, VacinesTotalDeliveredGivenPerRegionDto> getData() {
		return data;
	}

	public void setData(Map<String, VacinesTotalDeliveredGivenPerRegionDto> data) {
		this.data = data;
	}

}
