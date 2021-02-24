package com.marco.javacovidstatus.services.interfaces;

import java.time.LocalDate;
import java.util.List;

import com.marco.javacovidstatus.model.dto.VaccinesDelivered;

/**
 * This interface defines the operations that we can do with the Vaccines data
 * 
 * @author Marco
 *
 */
public interface VaccineDateService {

	/**
	 * It will return the list of delivered vaccined between the provided dates
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public List<VaccinesDelivered> getDeliveredVaccinesBetweenDates(LocalDate start, LocalDate end);
}
