package com.marco.javacovidstatus.model.rest.vaccines;

import java.util.Map;

import com.marco.javacovidstatus.model.dto.PeopleVaccinated;
import com.marco.javacovidstatus.model.rest.RespDataSets;

/**
 * Response model used to return the given vaccines group by age range
 * 
 * @author Marco
 *
 */
public class RespGetVaccinatedPeoplePerAgeData extends RespDataSets {
	/**
	 * The key is the "age range" example: 20-29, 30-39 etc
	 */
	private Map<String, PeopleVaccinated> dataVaccinatedPerAge;

	public Map<String, PeopleVaccinated> getDataVaccinatedPerAge() {
		return dataVaccinatedPerAge;
	}

	public void setDataVaccinatedPerAge(Map<String, PeopleVaccinated> dataVaccinatedPerAge) {
		this.dataVaccinatedPerAge = dataVaccinatedPerAge;
	}
}
