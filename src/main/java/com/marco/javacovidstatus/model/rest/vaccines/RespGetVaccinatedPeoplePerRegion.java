package com.marco.javacovidstatus.model.rest.vaccines;

import java.util.List;

import com.marco.javacovidstatus.model.dto.PeopleVaccinatedPerRegion;
import com.marco.javacovidstatus.model.rest.RespDataSets;

public class RespGetVaccinatedPeoplePerRegion extends RespDataSets {
	private List<PeopleVaccinatedPerRegion> vaccinatedPeople;

	public List<PeopleVaccinatedPerRegion> getVaccinatedPeople() {
		return vaccinatedPeople;
	}

	public void setVaccinatedPeople(List<PeopleVaccinatedPerRegion> vaccinatedPeople) {
		this.vaccinatedPeople = vaccinatedPeople;
	}

}
