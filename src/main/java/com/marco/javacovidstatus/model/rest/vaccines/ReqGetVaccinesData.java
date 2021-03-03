package com.marco.javacovidstatus.model.rest.vaccines;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * HTTP Request to retrieve the list of delivered vaccines
 * 
 * @author Marco
 *
 */
public class ReqGetVaccinesData implements Serializable {
	private static final long serialVersionUID = 1L;
	private LocalDate from;
	private LocalDate to;

	public LocalDate getFrom() {
		return from;
	}

	public void setFrom(LocalDate from) {
		this.from = from;
	}

	public LocalDate getTo() {
		return to;
	}

	public void setTo(LocalDate to) {
		this.to = to;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
