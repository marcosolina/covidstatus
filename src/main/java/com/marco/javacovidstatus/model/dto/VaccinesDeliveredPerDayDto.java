package com.marco.javacovidstatus.model.dto;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * This class represent number of vaccines Delivered in a single day
 * 
 * @author Marco
 *
 */
public class VaccinesDeliveredPerDayDto implements Serializable {
	private static final long serialVersionUID = 1L;
	private LocalDate date;
	private Long dosesDelivered;

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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
