package com.marco.javacovidstatus.model.rest.vaccines;

import java.util.Map;

import com.marco.javacovidstatus.model.rest.RespDataSets;

public class RespGetVaccinesDeliveredPerSupplier extends RespDataSets {
	private Map<String, Long> deliveredPerSupplier;

	public Map<String, Long> getDeliveredPerSupplier() {
		return deliveredPerSupplier;
	}

	public void setDeliveredPerSupplier(Map<String, Long> deliveredPerSupplier) {
		this.deliveredPerSupplier = deliveredPerSupplier;
	}

}
