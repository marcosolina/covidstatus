package com.marco.javacovidstatus.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;

import com.marco.javacovidstatus.enums.Gender;
import com.marco.javacovidstatus.model.dto.PopulationDto;
import com.marco.javacovidstatus.model.entitites.italianpopulation.EntityItalianPopulation;
import com.marco.javacovidstatus.model.entitites.italianpopulation.EntityItalianPopulationPk;
import com.marco.javacovidstatus.repositories.interfaces.PopulationRepo;
import com.marco.javacovidstatus.services.interfaces.PopulationDataService;
import com.marco.utils.MarcoException;

/**
 * My implementation of this interface
 * 
 * @author Marco
 *
 */
public class PopulationDataServiceMarco implements PopulationDataService {
    @Autowired
    private PopulationRepo repo;

    @Override
    public boolean storeNewPopulationDto(PopulationDto dto) throws MarcoException {
        return repo.insert(this.fromPopulationDtoToEntityItalianPopulation(dto));
    }

    private EntityItalianPopulation fromPopulationDtoToEntityItalianPopulation(PopulationDto dto) {
        EntityItalianPopulation entity = new EntityItalianPopulation();
        EntityItalianPopulationPk pk = new EntityItalianPopulationPk();
        entity.setId(pk);

        pk.setAge(dto.getAge());
        pk.setGender(dto.getGender());
        pk.setYear(dto.getYear());
        entity.setCounter(dto.getCounter());

        return entity;
    }

    @Override
    public boolean deleteAll() throws MarcoException {
        return repo.deleteAll();
    }

    @Override
    public Long getSumForAgesAndYear(int ageFrom, int ageTo, Gender gender, int year) {
        return repo.getSumForAgesAndYear(ageFrom, ageTo, gender, year);
    }

}
