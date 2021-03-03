package com.marco.javacovidstatus.model.rest.vaccines;

import java.util.List;
import java.util.Map;

import com.marco.javacovidstatus.model.rest.RespDataSets;

/**
 * Http response model used to return the delivered vaccines data
 * 
 * @author Marco
 *
 */
public class RespGetVaccinesDeliveredPerRegion extends RespDataSets {

	private Map<String, List<Long>> deliveredPerRegion;

	public Map<String, List<Long>> getDeliveredPerRegion() {
		return deliveredPerRegion;
	}

	public void setDeliveredPerRegion(Map<String, List<Long>> deliveredPerRegion) {
		this.deliveredPerRegion = deliveredPerRegion;
	}

}
