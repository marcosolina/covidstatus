package com.marco.javacovidstatus.model.entitites;

/**
 * Entity model used to retrieve the total given vaccines to the people per
 * region
 * 
 * @author Marco
 *
 */
public class TotalVaccineGivenPerRegion {
	private String regionCode;
	private Long givenDoses;

	public TotalVaccineGivenPerRegion(String regionCode, Long givenDoses) {
		super();
		this.regionCode = regionCode;
		this.givenDoses = givenDoses;
	}

	public String getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}

	public Long getGivenDoses() {
		return givenDoses;
	}

	public void setGivenDoses(Long givenDoses) {
		this.givenDoses = givenDoses;
	}

}
