package com.marco.javacovidstatus.model.rest.vaccines;

import java.util.Map;

import com.marco.javacovidstatus.model.rest.RespDataSets;

/**
 * Response Model used to return the vaccines deliverd per supplier
 * 
 * @author Marco
 *
 */
public class RespGetVaccinesDeliveredPerSupplier extends RespDataSets {
	/**
	 * The key is Supplier Name
	 */
	private Map<String, Long> deliveredPerSupplier;

	public Map<String, Long> getDeliveredPerSupplier() {
		return deliveredPerSupplier;
	}

	public void setDeliveredPerSupplier(Map<String, Long> deliveredPerSupplier) {
		this.deliveredPerSupplier = deliveredPerSupplier;
	}

}
