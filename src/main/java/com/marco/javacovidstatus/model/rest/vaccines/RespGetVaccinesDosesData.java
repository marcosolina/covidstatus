package com.marco.javacovidstatus.model.rest.vaccines;

import java.util.Map;

import com.marco.javacovidstatus.model.rest.RespDataSets;

public class RespGetVaccinesDosesData  extends RespDataSets {
	private Map<String, Long> dataShotNumber;

	public Map<String, Long> getDataShotNumber() {
		return dataShotNumber;
	}

	public void setDataShotNumber(Map<String, Long> dataShotNumber) {
		this.dataShotNumber = dataShotNumber;
	}
}
