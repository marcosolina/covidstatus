package com.marco.javacovidstatus.model.dto;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * This class is a simple POJO which represents the single row in the DB
 * 
 * @author Marco
 *
 */
public class VaccinesDeliveredDto implements Serializable {
	private static final long serialVersionUID = 1L;
	private String regionCode;
	private LocalDate date;
	private String supplier;
	private int dosesDelivered;

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
