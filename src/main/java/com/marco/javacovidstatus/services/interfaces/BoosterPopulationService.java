package com.marco.javacovidstatus.services.interfaces;

import com.marco.javacovidstatus.model.dto.BoosterPopulationDto;
import com.marco.utils.MarcoException;

public interface BoosterPopulationService {
    /**
     * It persists the dto
     * 
     * @param entity
     * @return
     */
    public boolean insert(BoosterPopulationDto dto) throws MarcoException;

    /**
     * It clears the data
     * 
     * @return
     */
    public boolean deleteAll();
}
