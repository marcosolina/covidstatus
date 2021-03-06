package com.marco.javacovidstatus.model.rest.vaccines;

import java.util.List;
import java.util.Map;

import com.marco.javacovidstatus.model.rest.RespDataSets;

/**
 * Response model used to provide the data of the vaccines delivered to the
 * different "type of people"
 * 
 * @author Marco
 *
 */
public class RespGetVaccinatedPeopleData extends RespDataSets {
	/**
	 * The key is the "People type", nhs, school, over 80 etc.
	 */
	private Map<String, List<Long>> dataVaccinatedPeople;

	public Map<String, List<Long>> getDataVaccinatedPeople() {
		return dataVaccinatedPeople;
	}

	public void setDataVaccinatedPeople(Map<String, List<Long>> dataVaccinatedPeople) {
		this.dataVaccinatedPeople = dataVaccinatedPeople;
	}
}
