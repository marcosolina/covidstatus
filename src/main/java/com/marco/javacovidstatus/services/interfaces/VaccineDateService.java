package com.marco.javacovidstatus.services.interfaces;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.marco.javacovidstatus.model.dto.VaccinatedPeople;
import com.marco.javacovidstatus.model.dto.VaccinesDelivered;
import com.marco.utils.MarcoException;

/**
 * This interface defines the operations that we can do with the Vaccines data
 * 
 * @author Marco
 *
 */
public interface VaccineDateService {

	public Map<String, List<VaccinesDelivered>> getDeliveredVaccinesPerRegionBetweenDatesPerRegion(LocalDate start,
			LocalDate end);

	public Map<String, Long> getDeliveredVaccinesBetweenDatesPerSupplier(LocalDate start, LocalDate end);

	public VaccinatedPeople getVaccinatedPeopleBetweenDates(LocalDate start, LocalDate end);

	public Map<String, Long> getVaccinatedAgeRangeBetweenDates(LocalDate start, LocalDate end);

	public Map<String, Long> getGiveShotNumberBetweenDates(LocalDate start, LocalDate end) throws MarcoException;
}
