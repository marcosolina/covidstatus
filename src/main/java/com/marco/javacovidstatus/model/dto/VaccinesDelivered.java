package com.marco.javacovidstatus.model.dto;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * This class represent a simple POJO to pass the info related to the number of
 * vaccines delivered to a specific region
 * 
 * @author Marco
 *
 */
public class VaccinesDelivered implements Serializable {
	private static final long serialVersionUID = 1L;
	private LocalDate date;
	private String regionCode;
	private String supplier;
	private int dosesDelivered;

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public int getDosesDelivered() {
		return dosesDelivered;
	}

	public void setDosesDelivered(int dosesDelivered) {
		this.dosesDelivered = dosesDelivered;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
