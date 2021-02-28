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
	private Map<String, Integer> dataPerSupplier;

	private Map<String, List<Long>> dataVaccinatedPeople;
	private Map<String, Integer> dataVaccinatedPerAge;
	private Map<String, Integer> dataShotNumber;

	public Map<String, List<Integer>> getDataPerRegion() {
		return dataPerRegion;
	}

	public void setDataPerRegion(Map<String, List<Integer>> dataPerRegion) {
		this.dataPerRegion = dataPerRegion;
	}

	public Map<String, Integer> getDataPerSupplier() {
		return dataPerSupplier;
	}

	public void setDataPerSupplier(Map<String, Integer> dataPerSupplier) {
		this.dataPerSupplier = dataPerSupplier;
	}

	public Map<String, List<Long>> getDataVaccinatedPeople() {
		return dataVaccinatedPeople;
	}

	public void setDataVaccinatedPeople(Map<String, List<Long>> dataVaccinatedPeople) {
		this.dataVaccinatedPeople = dataVaccinatedPeople;
	}

	public Map<String, Integer> getDataVaccinatedPerAge() {
		return dataVaccinatedPerAge;
	}

	public void setDataVaccinatedPerAge(Map<String, Integer> dataVaccinatedPerAge) {
		this.dataVaccinatedPerAge = dataVaccinatedPerAge;
	}

	public Map<String, Integer> getDataShotNumber() {
		return dataShotNumber;
	}

	public void setDataShotNumber(Map<String, Integer> dataShotNumber) {
		this.dataShotNumber = dataShotNumber;
	}

}
