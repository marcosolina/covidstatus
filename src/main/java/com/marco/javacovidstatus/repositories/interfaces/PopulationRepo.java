package com.marco.javacovidstatus.repositories.interfaces;

import com.marco.javacovidstatus.enums.Gender;
import com.marco.javacovidstatus.model.entitites.italianpopulation.EntityItalianPopulation;

public interface PopulationRepo {
    public boolean insert(EntityItalianPopulation entity);
    public boolean deleteAll();
    public Long getSumForAgesAndYear(int ageFrom, int ageTo, Gender gender, int year);
}
