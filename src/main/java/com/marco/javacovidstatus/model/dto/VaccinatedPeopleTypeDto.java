package com.marco.javacovidstatus.model.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Model that contains the information of the type of people who has received
 * the vaccine
 * 
 * @author Marco
 *
 */
public class VaccinatedPeopleTypeDto {
	private List<LocalDate> dates;
	private Map<String, List<Long>> dataVaccinatedPeople;

	public List<LocalDate> getDates() {
		return dates;
	}

	public void setDates(List<LocalDate> dates) {
		this.dates = dates;
	}

	public Map<String, List<Long>> getDataVaccinatedPeople() {
		return dataVaccinatedPeople;
	}

	public void setDataVaccinatedPeople(Map<String, List<Long>> dataVaccinatedPeople) {
		this.dataVaccinatedPeople = dataVaccinatedPeople;
	}

}
