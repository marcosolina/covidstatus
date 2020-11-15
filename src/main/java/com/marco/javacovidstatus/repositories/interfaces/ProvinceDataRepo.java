package com.marco.javacovidstatus.repositories.interfaces;

import org.springframework.data.repository.CrudRepository;

import com.marco.javacovidstatus.model.entitites.EntityProvinceData;
import com.marco.javacovidstatus.model.entitites.EntityProvinceDataPk;

/**
 * Standard Spring interface to perform CRUD operation on the
 * {@link EntityProvinceData} entity
 * 
 * @author Marco
 *
 */
public interface ProvinceDataRepo extends CrudRepository<EntityProvinceData, EntityProvinceDataPk> {

}
