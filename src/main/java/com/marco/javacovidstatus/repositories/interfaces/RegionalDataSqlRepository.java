package com.marco.javacovidstatus.repositories.interfaces;

import org.springframework.data.repository.CrudRepository;

import com.marco.javacovidstatus.model.entitites.EntityRegionalData;
import com.marco.javacovidstatus.model.entitites.EntityRegionalDataPk;

public interface RegionalDataSqlRepository extends CrudRepository<EntityRegionalData, EntityRegionalDataPk>{

}
