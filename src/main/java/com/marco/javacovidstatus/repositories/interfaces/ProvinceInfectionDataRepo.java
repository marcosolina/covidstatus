package com.marco.javacovidstatus.repositories.interfaces;

import org.springframework.data.repository.CrudRepository;

import com.marco.javacovidstatus.model.entitites.infections.EntityProvinceData;
import com.marco.javacovidstatus.model.entitites.infections.EntityProvinceDataPk;

/**
 * Standard Spring interface to perform CRUD operation on the
 * {@link EntityProvinceData} entity
 * 
 * @author Marco
 *
 */
public interface ProvinceInfectionDataRepo extends CrudRepository<EntityProvinceData, EntityProvinceDataPk> {

}
