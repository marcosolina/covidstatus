package com.marco.javacovidstatus.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;

import com.marco.javacovidstatus.model.dto.BoosterPopulationDto;
import com.marco.javacovidstatus.model.entitites.italianpopulation.EntityBoosterPopulation;
import com.marco.javacovidstatus.model.entitites.italianpopulation.EntityBoosterPopulationPk;
import com.marco.javacovidstatus.repositories.interfaces.BoosterPopulationRepo;
import com.marco.javacovidstatus.services.interfaces.BoosterPopulationService;
import com.marco.utils.MarcoException;

public class BoosterPopulationServiceImp implements BoosterPopulationService {

    @Autowired
    private BoosterPopulationRepo repo;
    
    @Override
    public boolean insert(BoosterPopulationDto dto) throws MarcoException {
        return repo.insert(this.fromEntityToDto(dto));
    }

    @Override
    public boolean deleteAll() {
        return repo.deleteAll();
    }
    
    private EntityBoosterPopulation fromEntityToDto(BoosterPopulationDto dto) {
        EntityBoosterPopulation entity = new EntityBoosterPopulation();
        EntityBoosterPopulationPk pk = new EntityBoosterPopulationPk();
        entity.setId(pk);
        entity.setCounter(dto.getCounter());
        pk.setAge(dto.getAge());
        pk.setRegionCode(dto.getRegionCode());
        return entity;
    }

}
