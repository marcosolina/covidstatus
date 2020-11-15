package com.marco.javacovidstatus.repositories.interfaces;

import org.springframework.data.repository.CrudRepository;

import com.marco.javacovidstatus.model.entitites.EntityRegionalData;
import com.marco.javacovidstatus.model.entitites.EntityRegionalDataPk;

/**
 * Standard Spring interface to perform CRUD operation on the
 * {@link EntityRegionalData} entity
 * 
 * @author Marco
 *
 */
public interface RegionalDataRepository extends CrudRepository<EntityRegionalData, EntityRegionalDataPk> {

}
