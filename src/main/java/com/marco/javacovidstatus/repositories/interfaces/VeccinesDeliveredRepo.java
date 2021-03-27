package com.marco.javacovidstatus.repositories.interfaces;

import java.time.LocalDate;
import java.util.List;

import com.marco.javacovidstatus.model.entitites.vaccines.EntityVacciniConsegne;
import com.marco.javacovidstatus.model.entitites.vaccines.TotalVaccineDeliveredPerRegion;
import com.marco.javacovidstatus.model.entitites.vaccines.VacciniConsegne;
import com.marco.javacovidstatus.model.entitites.vaccines.VeccinesDeliveredPerSupplier;

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
	 * It returns the total number of delivered vaccines group by Supplier
	 * @return
	 */
	public List<VeccinesDeliveredPerSupplier> getTotalDeliveredVaccinesPerSupplier();

	/**
	 * It adds the default rows for the days where data are missing
	 */
	public void addMissingRowsForNoDeliveryDays();

	/**
	 * It returns the last date of available data, null if none are available
	 * 
	 * @return
	 */
	public LocalDate getDataAvailableLastDate();

	/**
	 * It returns the total number of vaccines delivered
	 * 
	 * @return
	 */
	public Long getTotalNumberDeliveedVaccines();

	/**
	 * It returns the total of delivered vaccines per region
	 * 
	 * @return
	 */
	public List<TotalVaccineDeliveredPerRegion> getTotalVaccineDeliveredPerRegion();

	/**
	 * It removes the informations stored for the specific date
	 * 
	 * @param date
	 */
	public void deleteInformationForDate(LocalDate date);
}
