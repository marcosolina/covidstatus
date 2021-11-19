package com.marco.javacovidstatus.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;

import com.marco.javacovidstatus.model.dto.AdditionalDosePopulationDto;
import com.marco.javacovidstatus.model.entitites.italianpopulation.EntityAdditionalDosePopulation;
import com.marco.javacovidstatus.repositories.interfaces.AdditionalDosePopulationRepo;
import com.marco.javacovidstatus.services.interfaces.AdditionalDosePopulationService;
import com.marco.utils.MarcoException;

public class AdditionalDosePopulationServiceImp implements AdditionalDosePopulationService {

    @Autowired
    private AdditionalDosePopulationRepo repo;

    @Override
    public boolean insert(AdditionalDosePopulationDto dto) throws MarcoException {
        return repo.insert(this.fromDtoToEntity(dto));
    }

    @Override
    public boolean deleteAll() {
        return repo.deleteAll();
    }
    
    private EntityAdditionalDosePopulation fromDtoToEntity(AdditionalDosePopulationDto dto) {
        EntityAdditionalDosePopulation entity = new EntityAdditionalDosePopulation();
        entity.setCounter(dto.getCounter());
        entity.setRegionCode(dto.getRegionCode());
        return entity;
    }

}
