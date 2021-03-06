package com.marco.javacovidstatus.repositories.interfaces;

import java.time.LocalDate;
import java.util.List;

import com.marco.javacovidstatus.model.entitites.EntityVacciniConsegne;
import com.marco.javacovidstatus.model.entitites.VacciniConsegne;
import com.marco.javacovidstatus.model.entitites.VeccinesDeliveredPerSupplier;

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
	 * It returns the list of vaccines delivered to the regions every day in the
	 * date range
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public List<VacciniConsegne> getDeliveredVaccinesBetween(LocalDate start, LocalDate end);

	/**
	 * It returns the list of vaccines delivered by every supplier per every day in
	 * the date range
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public List<VeccinesDeliveredPerSupplier> getDeliveredVaccinesPerSupplierBetween(LocalDate start, LocalDate end);

	/**
	 * It adds the default rows for the days where data are missing
	 */
	public void addMissingRowsForNoDeliveryDays();
}
