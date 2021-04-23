package com.marco.javacovidstatus.services.interfaces;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.marco.javacovidstatus.model.dto.TotalPeopleVaccinated;
import com.marco.javacovidstatus.model.dto.VaccinatedPeopleDto;
import com.marco.javacovidstatus.model.dto.VaccinatedPeopleTypeDto;
import com.marco.javacovidstatus.model.dto.VaccinesDeliveredDto;
import com.marco.javacovidstatus.model.dto.VaccinesDeliveredPerDayDto;
import com.marco.javacovidstatus.model.dto.VaccinesReceivedUsedDto;
import com.marco.javacovidstatus.model.dto.VacinesTotalDeliveredGivenPerRegionDto;
import com.marco.utils.MarcoException;

/**
 * This interface defines the operations that we can do with the Vaccines data
 * 
 * @author Marco
 *
 */
public interface VaccineDataService {

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
	public Map<String, List<VaccinesDeliveredPerDayDto>> getDeliveredVaccinesPerRegionBetweenDatesPerRegion(
			LocalDate start, LocalDate end) throws MarcoException;

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
	public Map<String, Long> getDeliveredVaccinesBetweenDatesPerSupplier(LocalDate start, LocalDate end) throws MarcoException;
	
	/**
	 * It returns the total number of vaccines deliverd group by supplier
	 * 
	 * @return A map which contains:
	 *         <ul>
	 *         <li>Key -> Supplpier Name</li>
	 *         <li>Value -> Total number of vaccines deliver in the date range</li>
	 *         </ul>
	 */
	public Map<String, Long> getTotalDeliveredVaccinesPerSupplier();

	/**
	 * It returns the numbers of vaccinated people in the date range.
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public VaccinatedPeopleTypeDto getVaccinatedPeopleBetweenDates(LocalDate start, LocalDate end) throws MarcoException;

	/**
	 * It returns the numbers of vaccinated people grouped by age range
	 * 
	 * @param start
	 * @param end
	 * @return A map which contains:
	 *         <ul>
	 *         <li>Key -> Age Range</li>
	 *         <li>Value -> Number of people vaccinated in that age range</li>
	 *         </ul>
	 */
	public Map<String, TotalPeopleVaccinated> getVaccinatedAgeRangeBetweenDates(LocalDate start, LocalDate end) throws MarcoException;

	/**
	 * It returns the numbers of vaccinated people grouped by age range
	 * 
	 * @return
	 */
	public Map<String, TotalPeopleVaccinated> getVaccinatedAgeRangeTotals();

	/**
	 * It returns the number of people who has received the vaccine shot
	 * 
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

	/**
	 * It removes all the data related to the vaccines delivered
	 */
	public void deleteAllVaccineDeliveredData();

	/**
	 * It stores the data in the database
	 * 
	 * @param data
	 * @return
	 */
	public boolean saveVaccinesDeliveredData(VaccinesDeliveredDto data);

	/**
	 * Add default empty rows for the region who has not provided data for some days
	 */
	public void addMissingRowsForNoDeliveryDays();

	/**
	 * It returns the last date for which the Delivered Vaccines data are available
	 * 
	 * @return
	 */
	public LocalDate getVaccineDeliveredLastUpdateDate();

	/**
	 * It removes all the data related to the given vaccines
	 */
	public void deleteAllGivenVaccineData();

	/**
	 * It stores the data in the database
	 * 
	 * @param data
	 * @return
	 */
	public boolean saveGivenVaccinesData(VaccinatedPeopleDto data);

	/**
	 * Add default empty rows for the region who has not provided data for some days
	 */
	public void addMissingRowsForNoVaccinationDays();

	/**
	 * It returns the last date for which the Given Vaccines data are available
	 * 
	 * @return
	 */
	public LocalDate getGivenVaccinesLastUpdateDate();

	/**
	 * It returns the total number of vaccines delivered, and total number of used
	 * vaccines
	 * 
	 * @return
	 */
	public VaccinesReceivedUsedDto getTotlalVaccinesDeliveredUsed();

	/**
	 * It returns the total data of the number of delivered and injected vaccines
	 * per region
	 * 
	 * @return
	 */
	public Map<String, VacinesTotalDeliveredGivenPerRegionDto> getVacinesTotalDeliveredGivenPerRegion();

	/**
	 * It deletes the data from the given vaccines repo for the specified date
	 * 
	 * @param date
	 */
	public void deleteGivenVaccineInformation(LocalDate date);

	/**
	 * It deletes the data from the delivered vaccine repo for the specified date
	 * 
	 * @param date
	 */
	public void deleteDeliveredVaccineInformation(LocalDate date);
}
