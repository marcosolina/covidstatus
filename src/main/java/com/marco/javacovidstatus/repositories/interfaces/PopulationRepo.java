package com.marco.javacovidstatus.repositories.interfaces;

import com.marco.javacovidstatus.enums.Gender;
import com.marco.javacovidstatus.model.entitites.italianpopulation.EntityItalianPopulation;

/**
 * Contract to perform CRUD operation on the Population DB
 * 
 * @author Marco
 *
 */
public interface PopulationRepo {
    /**
     * It persists the entity
     * 
     * @param entity
     * @return
     */
    public boolean insert(EntityItalianPopulation entity);

    /**
     * It clears the table
     * 
     * @return
     */
    public boolean deleteAll();

    /**
     * It returns the SUM of people matching the specified parameters. For People
     * with age >= 100 are all stored with value "100"
     * 
     * @param ageFrom
     * @param ageTo
     * @param gender
     * @param year
     * @return
     */
    public Long getSumForAgesAndYear(int ageFrom, int ageTo, Gender gender, int year);
}
