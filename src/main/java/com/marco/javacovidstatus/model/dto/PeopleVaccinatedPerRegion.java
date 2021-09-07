package com.marco.javacovidstatus.model.dto;

import java.math.BigDecimal;

public class PeopleVaccinatedPerRegion {
	private String regionCode;
	private Long population;
	private Long firstDose;
	private Long secondDose;
	private Long monoDose;
	private Long doseAfterInfection;
	private BigDecimal firstDosePerc;
	private BigDecimal vaccinatedPerc;

	public String getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}

	public Long getPopulation() {
		return population;
	}

	public void setPopulation(Long population) {
		this.population = population;
	}

	public Long getFirstDose() {
		return firstDose;
	}

	public void setFirstDose(Long firstDose) {
		this.firstDose = firstDose;
	}

	public Long getSecondDose() {
		return secondDose;
	}

	public void setSecondDose(Long secondDose) {
		this.secondDose = secondDose;
	}

	public Long getMonoDose() {
		return monoDose;
	}

	public void setMonoDose(Long monoDose) {
		this.monoDose = monoDose;
	}

	public BigDecimal getFirstDosePerc() {
		return firstDosePerc;
	}

	public void setFirstDosePerc(BigDecimal firstDosePerc) {
		this.firstDosePerc = firstDosePerc;
	}

	public BigDecimal getVaccinatedPerc() {
		return vaccinatedPerc;
	}

	public void setVaccinatedPerc(BigDecimal vaccinatedPerc) {
		this.vaccinatedPerc = vaccinatedPerc;
	}

	public Long getDoseAfterInfection() {
		return doseAfterInfection;
	}

	public void setDoseAfterInfection(Long doseAfterInfection) {
		this.doseAfterInfection = doseAfterInfection;
	}

}
