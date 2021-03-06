package com.marco.javacovidstatus.model.entitites;

/**
 * Custom Query Model used to retrieve number of given vaccines grouped by age
 * 
 * @author Marco
 *
 */
public class AgeRangeGivenVaccines {
	private String ageRange;
	private Long men;
	private Long women;

	public AgeRangeGivenVaccines(String ageRange, Long men, Long women) {
		super();
		this.ageRange = ageRange;
		this.men = men;
		this.women = women;
	}

	public String getAgeRange() {
		return ageRange;
	}

	public void setAgeRange(String ageRange) {
		this.ageRange = ageRange;
	}

	public Long getMen() {
		return men;
	}

	public void setMen(Long men) {
		this.men = men;
	}

	public Long getWomen() {
		return women;
	}

	public void setWomen(Long women) {
		this.women = women;
	}

}
