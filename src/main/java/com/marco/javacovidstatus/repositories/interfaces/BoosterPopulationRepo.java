package com.marco.javacovidstatus.repositories.interfaces;

import com.marco.javacovidstatus.model.entitites.italianpopulation.EntityBoosterPopulation;

public interface BoosterPopulationRepo {
    /**
     * It persists the entity
     * 
     * @param entity
     * @return
     */
    public boolean insert(EntityBoosterPopulation entity);

    /**
     * It clears the table
     * 
     * @return
     */
    public boolean deleteAll();
}
