package com.marco.javacovidstatus.model.rest;

import java.util.List;
import java.util.Map;

import com.marco.javacovidstatus.model.dto.CharDataType;
import com.marco.javacovidstatus.model.dto.VaccinesDelivered;

/**
 * Http response model used to return the delivered vaccines data
 * 
 * @author Marco
 *
 */
public class RespGetVaccinesDelivered extends RespDataSets {

	public RespGetVaccinesDelivered() {
		super(CharDataType.GIVEN);
	}

	private Map<String, List<VaccinesDelivered>> dataMap;

	public Map<String, List<VaccinesDelivered>> getDataMap() {
		return dataMap;
	}

	public void setDataMap(Map<String, List<VaccinesDelivered>> dataMap) {
		this.dataMap = dataMap;
	}

}
