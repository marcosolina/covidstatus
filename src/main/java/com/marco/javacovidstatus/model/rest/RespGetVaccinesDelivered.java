package com.marco.javacovidstatus.model.rest;

import java.util.List;
import java.util.Map;

import com.marco.javacovidstatus.model.dto.CharDataType;

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

	/**
	 * The first key is the provider, the second key is the region code
	 */
	private Map<String, List<Integer>> dataPerRegion;
	private Map<String, List<Integer>> dataPerSupplier;

	public Map<String, List<Integer>> getDataPerRegion() {
		return dataPerRegion;
	}

	public void setDataPerRegion(Map<String, List<Integer>> dataPerRegion) {
		this.dataPerRegion = dataPerRegion;
	}

	public Map<String, List<Integer>> getDataPerSupplier() {
		return dataPerSupplier;
	}

	public void setDataPerSupplier(Map<String, List<Integer>> dataPerSupplier) {
		this.dataPerSupplier = dataPerSupplier;
	}

}
