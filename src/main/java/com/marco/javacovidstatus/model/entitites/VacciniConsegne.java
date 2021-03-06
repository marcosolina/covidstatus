package com.marco.javacovidstatus.model.entitites;

import java.time.LocalDate;

/**
 * Custom Query Model to retrieve the number of delivered vaccines per Region
 * 
 * @author Marco
 *
 */
public class VacciniConsegne {

	private String regionCode;
	private LocalDate date;
	private Long dosesDelivered;

	public VacciniConsegne(String regionCode, LocalDate date, Long dosesDelivered) {
		super();
		this.regionCode = regionCode;
		this.date = date;
		this.dosesDelivered = dosesDelivered;
	}

	public String getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Long getDosesDelivered() {
		return dosesDelivered;
	}

	public void setDosesDelivered(Long dosesDelivered) {
		this.dosesDelivered = dosesDelivered;
	}

}
