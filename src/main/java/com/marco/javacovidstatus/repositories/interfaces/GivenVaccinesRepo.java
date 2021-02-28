package com.marco.javacovidstatus.repositories.interfaces;

import java.time.LocalDate;
import java.util.List;

import com.marco.javacovidstatus.model.entitites.DailySumGivenVaccines;
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
	 * It returns the data of the vaccines given during the specified period
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public List<EntitySomministrazioneVaccini> getGivenVaccinesBetween(LocalDate start, LocalDate end);
	
	public List<DailySumGivenVaccines> getDailySumGivenVaccinesBetween(LocalDate start, LocalDate end);
}
