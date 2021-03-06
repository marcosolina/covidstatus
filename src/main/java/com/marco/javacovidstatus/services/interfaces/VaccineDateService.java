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

	/**
	 * It returns the number of vaccines delivered at the regions
	 * 
	 * @param start
	 * @param end
	 * @return A map which contains:
	 *         <ul>
	 *         <li>Key -> Region Code</li>
	 *         <li>Value -> Delivered Vaccines to the region grouped by date</li>
	 *         </ul>
	 */
	public Map<String, List<VaccinesDelivered>> getDeliveredVaccinesPerRegionBetweenDatesPerRegion(LocalDate start,
			LocalDate end);

	/**
	 * It returns the number of vaccines deliverd by the different suppliers
	 * 
	 * @param start
	 * @param end
	 * @return A map which contains:
	 *         <ul>
	 *         <li>Key -> Supplpier Name</li>
	 *         <li>Value -> Total number of vaccines deliver in the date range</li>
	 *         </ul>
	 */
	public Map<String, Long> getDeliveredVaccinesBetweenDatesPerSupplier(LocalDate start, LocalDate end);

	/**
	 * It returns the numbers of vaccinated people in the date range.
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public VaccinatedPeople getVaccinatedPeopleBetweenDates(LocalDate start, LocalDate end);

	/**
	 * It returns the numbers of vaccinated people grouped by age range
	 * @param start
	 * @param end
	 * @return A map which contains:
	 *         <ul>
	 *         <li>Key -> Age Range</li>
	 *         <li>Value -> Number of people vaccinated in that age range</li>
	 *         </ul>
	 */
	public Map<String, Long> getVaccinatedAgeRangeBetweenDates(LocalDate start, LocalDate end);

	/**
	 * It returns the number of people who has received the vaccine shot
	 * @param start
	 * @param end
	 * @return A map which contains:
	 *         <ul>
	 *         <li>Key -> Shot</li>
	 *         <li>Value -> Total number of vaccinated persons</li>
	 *         </ul>
	 * @throws MarcoException
	 */
	public Map<String, Long> getGiveShotNumberBetweenDates(LocalDate start, LocalDate end) throws MarcoException;
}
