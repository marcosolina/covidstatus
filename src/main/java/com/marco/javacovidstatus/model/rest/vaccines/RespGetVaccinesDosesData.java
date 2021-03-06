package com.marco.javacovidstatus.model.rest.vaccines;

import java.util.Map;

import com.marco.javacovidstatus.model.rest.RespDataSets;

/**
 * Response model used to return the given doses data
 * 
 * @author Marco
 *
 */
public class RespGetVaccinesDosesData extends RespDataSets {
	/**
	 * The key is the "Dose name", First, Second
	 */
	private Map<String, Long> dataShotNumber;

	public Map<String, Long> getDataShotNumber() {
		return dataShotNumber;
	}

	public void setDataShotNumber(Map<String, Long> dataShotNumber) {
		this.dataShotNumber = dataShotNumber;
	}
}
