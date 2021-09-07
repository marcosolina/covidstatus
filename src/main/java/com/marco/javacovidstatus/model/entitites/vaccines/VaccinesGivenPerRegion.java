package com.marco.javacovidstatus.model.entitites.vaccines;

/**
 * Total number of people vaccinated per region.
 * 
 * @author Marco
 *
 */
public class VaccinesGivenPerRegion {
	private String regionCode;
	private Long firstDose;
	private Long secondDose;
	private Long monoDose;
	private Long doseAfterInfection;

	public VaccinesGivenPerRegion(String regionCode, Long firstDose, Long secondDose, Long monoDose, Long doseAfterInfection) {
		super();
		this.regionCode = regionCode;
		this.firstDose = firstDose;
		this.secondDose = secondDose;
		this.monoDose = monoDose;
		this.doseAfterInfection = doseAfterInfection;
	}

	public String getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
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

	public Long getDoseAfterInfection() {
		return doseAfterInfection;
	}

	public void setDoseAfterInfection(Long doseAfterInfection) {
		this.doseAfterInfection = doseAfterInfection;
	}

}
