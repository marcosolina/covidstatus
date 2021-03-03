package com.marco.javacovidstatus.model.rest.vaccines;

import java.util.List;
import java.util.Map;

import com.marco.javacovidstatus.model.rest.RespDataSets;

public class RespGetVaccinatedPeopleData extends RespDataSets {
	private Map<String, List<Long>> dataVaccinatedPeople;

	public Map<String, List<Long>> getDataVaccinatedPeople() {
		return dataVaccinatedPeople;
	}

	public void setDataVaccinatedPeople(Map<String, List<Long>> dataVaccinatedPeople) {
		this.dataVaccinatedPeople = dataVaccinatedPeople;
	}
}
