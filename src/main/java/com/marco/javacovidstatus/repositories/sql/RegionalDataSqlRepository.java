package com.marco.javacovidstatus.repositories.sql;

import org.springframework.data.repository.CrudRepository;

import com.marco.javacovidstatus.repositories.model.EntityRegionalData;
import com.marco.javacovidstatus.repositories.model.EntityRegionalDataPk;

public interface RegionalDataSqlRepository extends CrudRepository<EntityRegionalData, EntityRegionalDataPk>{

}
