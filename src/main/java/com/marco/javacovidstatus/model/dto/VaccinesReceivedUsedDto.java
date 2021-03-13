package com.marco.javacovidstatus.model.dto;

import java.io.Serializable;

/**
 * This Dto provides the information related to the total number of vaccines
 * received and how many were used
 * 
 * @author Marco
 *
 */
public class VaccinesReceivedUsedDto implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long totalVaccinesReceived = Long.valueOf(0);
	private Long totalVaccinesUsed = Long.valueOf(0);

	public Long getTotalVaccinesReceived() {
		return totalVaccinesReceived;
	}

	public void setTotalVaccinesReceived(Long totalVaccinesReceived) {
		this.totalVaccinesReceived = totalVaccinesReceived;
	}

	public Long getTotalVaccinesUsed() {
		return totalVaccinesUsed;
	}

	public void setTotalVaccinesUsed(Long totalVaccinesUsed) {
		this.totalVaccinesUsed = totalVaccinesUsed;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
