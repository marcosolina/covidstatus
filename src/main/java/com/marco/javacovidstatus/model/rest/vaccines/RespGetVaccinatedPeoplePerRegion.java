package com.marco.javacovidstatus.model.rest.vaccines;

import java.util.Map;

import com.marco.javacovidstatus.model.dto.PeopleVaccinatedPerRegion;
import com.marco.javacovidstatus.model.rest.RespDataSets;

public class RespGetVaccinatedPeoplePerRegion extends RespDataSets {
	private Map<String, PeopleVaccinatedPerRegion> vaccinatedPeople;

	public Map<String, PeopleVaccinatedPerRegion> getVaccinatedPeople() {
		return vaccinatedPeople;
	}

	public void setVaccinatedPeople(Map<String, PeopleVaccinatedPerRegion> vaccinatedPeople) {
		this.vaccinatedPeople = vaccinatedPeople;
	}

}
