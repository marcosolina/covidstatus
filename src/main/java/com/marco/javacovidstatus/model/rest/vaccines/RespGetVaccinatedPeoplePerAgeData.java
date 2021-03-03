package com.marco.javacovidstatus.model.rest.vaccines;

import java.util.Map;

import com.marco.javacovidstatus.model.rest.RespDataSets;

public class RespGetVaccinatedPeoplePerAgeData  extends RespDataSets {
	private Map<String, Long> dataVaccinatedPerAge;

	public Map<String, Long> getDataVaccinatedPerAge() {
		return dataVaccinatedPerAge;
	}

	public void setDataVaccinatedPerAge(Map<String, Long> dataVaccinatedPerAge) {
		this.dataVaccinatedPerAge = dataVaccinatedPerAge;
	}
}
