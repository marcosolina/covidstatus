package com.marco.javacovidstatus.services.interfaces;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.marco.javacovidstatus.model.dto.VaccinesDelivered;

/**
 * This interface defines the operations that we can do with the Vaccines data
 * 
 * @author Marco
 *
 */
public interface VaccineDateService {

	/**
	 * It will return the list of delivered vaccines between the provided dates divede per region
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public Map<String, List<VaccinesDelivered>> getDeliveredVaccinesBetweenDatesPerRegion(LocalDate start, LocalDate end);
	
	/**
	 * It will return the list of delivered vaccines between the provided dates divede per provider
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public Map<String, Integer> getDeliveredVaccinesBetweenDatesPerSupplier(LocalDate start, LocalDate end);
}
