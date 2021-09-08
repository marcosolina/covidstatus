package com.marco.javacovidstatus.services.interfaces;

import com.marco.javacovidstatus.enums.Gender;
import com.marco.javacovidstatus.model.dto.PopulationDto;
import com.marco.utils.MarcoException;

public interface PopulationDataService {
	/**
	 * It will store in the system a new entry in the database
	 * 
	 * @param dto
	 * @return
	 * @throws MarcoException
	 */
	public boolean storeNewPopulationDto(PopulationDto dto) throws MarcoException;

	/**
	 * It clear the existing data
	 * 
	 * @return
	 * @throws MarcoException
	 */
	public boolean deleteAll() throws MarcoException;

	/**
	 * It will return the number of population matching the provided parameters
	 * 
	 * @param ageFrom
	 * @param ageTo
	 * @param gender
	 * @param year
	 * @return
	 */
	public Long getTotalPopulationBetweenAgesForSpecificGenderAndYear(int ageFrom, int ageTo, Gender gender, int year);

	/**
	 * It will return the number of population matching the provided parameters
	 * 
	 * @param gender
	 * @param year
	 * @return
	 */
	public Long getTotalPopulationForSpecificGenderAndYear(Gender gender, int year);

	/**
	 * It will return the number of population matching the provided parameters
	 * 
	 * @param gender
	 * @param year
	 * @param regionCode
	 * @return
	 */
	public Long getTotalPopulationForSpecificGenderYearAndRegion(Gender gender, int year, String regionCode);
}
