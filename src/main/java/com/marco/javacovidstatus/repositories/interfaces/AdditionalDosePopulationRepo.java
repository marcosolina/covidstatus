package com.marco.javacovidstatus.repositories.interfaces;

import com.marco.javacovidstatus.model.entitites.italianpopulation.EntityAdditionalDosePopulation;

public interface AdditionalDosePopulationRepo {
    /**
     * It persists the entity
     * 
     * @param entity
     * @return
     */
    public boolean insert(EntityAdditionalDosePopulation entity);

    /**
     * It clears the table
     * 
     * @return
     */
    public boolean deleteAll();
}
