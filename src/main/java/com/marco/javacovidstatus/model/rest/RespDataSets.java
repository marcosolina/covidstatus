package com.marco.javacovidstatus.model.rest;

import java.time.LocalDate;
import java.util.List;

import com.marco.utils.http.MarcoResponse;

/**
 * This represents the range of dates requested by the HTTP request, and the
 * type of data
 * 
 * @author Marco
 *
 */
public abstract class RespDataSets extends MarcoResponse {
	private List<LocalDate> arrDates;

	public List<LocalDate> getArrDates() {
		return arrDates;
	}

	public void setArrDates(List<LocalDate> arrDates) {
		this.arrDates = arrDates;
	}

}
