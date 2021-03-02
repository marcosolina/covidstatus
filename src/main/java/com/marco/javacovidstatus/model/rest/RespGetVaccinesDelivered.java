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
	private Map<String, List<Long>> deliveredPerRegion;
	private Map<String, Long> deliveredPerSupplier;

	private Map<String, List<Long>> dataVaccinatedPeople;
	private Map<String, Long> dataVaccinatedPerAge;
	private Map<String, Long> dataShotNumber;

	public Map<String, List<Long>> getDeliveredPerRegion() {
		return deliveredPerRegion;
	}

	public void setDeliveredPerRegion(Map<String, List<Long>> deliveredPerRegion) {
		this.deliveredPerRegion = deliveredPerRegion;
	}

	public Map<String, Long> getDeliveredPerSupplier() {
		return deliveredPerSupplier;
	}

	public void setDeliveredPerSupplier(Map<String, Long> deliveredPerSupplier) {
		this.deliveredPerSupplier = deliveredPerSupplier;
	}

	public Map<String, List<Long>> getDataVaccinatedPeople() {
		return dataVaccinatedPeople;
	}

	public void setDataVaccinatedPeople(Map<String, List<Long>> dataVaccinatedPeople) {
		this.dataVaccinatedPeople = dataVaccinatedPeople;
	}

	public Map<String, Long> getDataVaccinatedPerAge() {
		return dataVaccinatedPerAge;
	}

	public void setDataVaccinatedPerAge(Map<String, Long> dataVaccinatedPerAge) {
		this.dataVaccinatedPerAge = dataVaccinatedPerAge;
	}

	public Map<String, Long> getDataShotNumber() {
		return dataShotNumber;
	}

	public void setDataShotNumber(Map<String, Long> dataShotNumber) {
		this.dataShotNumber = dataShotNumber;
	}

}
