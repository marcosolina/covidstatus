package com.marco.javacovidstatus.model.rest.vaccines;

import java.util.List;
import java.util.Map;

import com.marco.javacovidstatus.model.rest.RespDataSets;

/**
 * Response model used to return the number of vaccines delivered per region
 * 
 * @author Marco
 *
 */
public class RespGetVaccinesDeliveredPerRegion extends RespDataSets {

	/**
	 * The key is the Region code
	 */
	private Map<String, List<Long>> deliveredPerRegion;

	public Map<String, List<Long>> getDeliveredPerRegion() {
		return deliveredPerRegion;
	}

	public void setDeliveredPerRegion(Map<String, List<Long>> deliveredPerRegion) {
		this.deliveredPerRegion = deliveredPerRegion;
	}

}
