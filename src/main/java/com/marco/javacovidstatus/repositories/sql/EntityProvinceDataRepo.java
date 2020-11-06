package com.marco.javacovidstatus.repositories.sql;

import org.springframework.data.repository.CrudRepository;

import com.marco.javacovidstatus.repositories.model.EntityProvinceData;
import com.marco.javacovidstatus.repositories.model.EntityProvinceDataPk;

public interface EntityProvinceDataRepo extends CrudRepository<EntityProvinceData, EntityProvinceDataPk>{

}
