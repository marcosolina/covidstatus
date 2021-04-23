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
    
    public boolean deleteAll() throws MarcoException;
    public Long getSumForAgesAndYear(int ageFrom, int ageTo, Gender gender, int year);
}
