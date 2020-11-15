package com.marco.javacovidstatus.repositories.interfaces;

import org.springframework.data.repository.CrudRepository;

import com.marco.javacovidstatus.model.entitites.EntityProvinceData;
import com.marco.javacovidstatus.model.entitites.EntityProvinceDataPk;

public interface EntityProvinceDataRepo extends CrudRepository<EntityProvinceData, EntityProvinceDataPk>{

}
