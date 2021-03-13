package com.marco.javacovidstatus.model.rest.vaccines;

import com.marco.javacovidstatus.model.rest.RespDataSets;

/**
 * Response model used to return the total number of delivered vaccines and
 * total number of used vaccines
 * 
 * @author Marco
 *
 */
public class RespGetTotalDelivereUsedVaccineData extends RespDataSets {
	private Long totalDeliveredVaccines;
	private Long totalUsedVaccines;

	public Long getTotalDeliveredVaccines() {
		return totalDeliveredVaccines;
	}

	public void setTotalDeliveredVaccines(Long totalDeliveredVaccines) {
		this.totalDeliveredVaccines = totalDeliveredVaccines;
	}

	public Long getTotalUsedVaccines() {
		return totalUsedVaccines;
	}

	public void setTotalUsedVaccines(Long totalUsedVaccines) {
		this.totalUsedVaccines = totalUsedVaccines;
	}

}
