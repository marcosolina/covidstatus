package com.marco.javacovidstatus.repositories.interfaces;

import java.time.LocalDate;
import java.util.List;

import com.marco.javacovidstatus.model.entitites.EntityVacciniConsegne;

/**
 * This interface provides the contract to query the database
 * 
 * @see {@link EntityVacciniConsegne}
 * @author Marco
 *
 */
public interface VeccinesDeliveredRepo {

	/**
	 * It stores the data in the database
	 * 
	 * @param {@link EntityVacciniConsegne}
	 * @return the operation status
	 */
	public boolean saveEntity(EntityVacciniConsegne entity);

	/**
	 * It clears the table
	 * 
	 * @return
	 */
	public boolean deleteAll();

	/**
	 * It returns the list of delivered vaccines between the provided data
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public List<EntityVacciniConsegne> getDeliveredVaccinesBetween(LocalDate start, LocalDate end);
}
