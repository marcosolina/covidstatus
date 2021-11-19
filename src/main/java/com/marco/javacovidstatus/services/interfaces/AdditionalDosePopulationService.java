package com.marco.javacovidstatus.services.interfaces;

import com.marco.javacovidstatus.model.dto.AdditionalDosePopulationDto;
import com.marco.utils.MarcoException;

public interface AdditionalDosePopulationService {
    /**
     * It persists the data
     * 
     * @param entity
     * @return
     */
    public boolean insert(AdditionalDosePopulationDto dto) throws MarcoException;

    /**
     * It clears the data
     * 
     * @return
     */
    public boolean deleteAll();
}
