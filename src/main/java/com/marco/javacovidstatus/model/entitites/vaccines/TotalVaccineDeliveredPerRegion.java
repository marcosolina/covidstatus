package com.marco.javacovidstatus.model.entitites.vaccines;

/**
 * Entity model used to return the totals of delivered vaccines per region
 * 
 * @author Marco
 *
 */
public class TotalVaccineDeliveredPerRegion {
	private String regionCode;
	private Long dosesDelivered;

	public TotalVaccineDeliveredPerRegion(String regionCode, Long dosesDelivered) {
		super();
		this.regionCode = regionCode;
		this.dosesDelivered = dosesDelivered;
	}

	public String getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}

	public Long getDosesDelivered() {
		return dosesDelivered;
	}

	public void setDosesDelivered(Long dosesDelivered) {
		this.dosesDelivered = dosesDelivered;
	}

}
