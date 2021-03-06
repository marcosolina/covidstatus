package com.marco.javacovidstatus.repositories.interfaces;

import java.time.LocalDate;
import java.util.List;

import com.marco.javacovidstatus.model.entitites.AgeRangeGivenVaccines;
import com.marco.javacovidstatus.model.entitites.DailySumGivenVaccines;
import com.marco.javacovidstatus.model.entitites.DoseCounter;
import com.marco.javacovidstatus.model.entitites.EntitySomministrazioneVaccini;

/**
 * This interface provides the contract to query the database
 * 
 * @see {@link EntitySomministrazioneVaccini}
 * @author Marco
 *
 */
public interface GivenVaccinesRepo {
	/**
	 * It stores the data in the database
	 * 
	 * @param {@link EntitySomministrazioneVaccini}
	 * @return the operation status
	 */
	public boolean saveEntity(EntitySomministrazioneVaccini entity);

	/**
	 * It clears the table
	 * 
	 * @return
	 */
	public boolean deleteAll();

	/**
	 * It returns an array with total vaccines given between the provided dates
	 * grouped by "Dose"
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public List<DoseCounter> getDosesCounterVaccinesBetween(LocalDate start, LocalDate end);

	/**
	 * It returns an array with total vaccines given between the provided dates to
	 * the different type of people
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public List<DailySumGivenVaccines> getDailySumGivenVaccinesBetween(LocalDate start, LocalDate end);

	/**
	 * It performs some queries to add empty rows for the days where the data are
	 * missing
	 */
	public void addMissingRowsForNoVaccinationDays();

	/**
	 * It returns an array with the total vaccines given betweeb the provided dates
	 * group by age range
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public List<AgeRangeGivenVaccines> getDeliveredVaccinesPerAgeRange(LocalDate start, LocalDate end);
}
