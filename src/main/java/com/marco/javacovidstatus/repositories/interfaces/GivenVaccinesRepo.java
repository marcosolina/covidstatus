package com.marco.javacovidstatus.repositories.interfaces;

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
}
