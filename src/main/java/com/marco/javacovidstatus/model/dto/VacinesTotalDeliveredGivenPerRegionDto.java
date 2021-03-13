package com.marco.javacovidstatus.model.dto;

/**
 * Total vaccines delivered / given to the people per region
 * 
 * @author Marco
 *
 */
public class VacinesTotalDeliveredGivenPerRegionDto {
	private String regionCode;
	private Long deliveredVaccines = Long.valueOf(0);
	private Long givenVaccines = Long.valueOf(0);

	public String getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}

	public Long getDeliveredVaccines() {
		return deliveredVaccines;
	}

	public void setDeliveredVaccines(Long deliveredVaccines) {
		this.deliveredVaccines = deliveredVaccines;
	}

	public Long getGivenVaccines() {
		return givenVaccines;
	}

	public void setGivenVaccines(Long givenVaccines) {
		this.givenVaccines = givenVaccines;
	}

}
