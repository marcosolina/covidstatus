package com.marco.javacovidstatus.repositories.interfaces;

import org.springframework.data.repository.CrudRepository;

import com.marco.javacovidstatus.model.entitites.infections.EntityRegionalData;
import com.marco.javacovidstatus.model.entitites.infections.EntityRegionalDataPk;

/**
 * Standard Spring interface to perform CRUD operation on the
 * {@link EntityRegionalData} entity
 * 
 * @author Marco
 *
 */
public interface RegionalInfectionDataRepository extends CrudRepository<EntityRegionalData, EntityRegionalDataPk> {

}
