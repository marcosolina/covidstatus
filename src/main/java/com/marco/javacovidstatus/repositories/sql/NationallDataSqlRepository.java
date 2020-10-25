package com.marco.javacovidstatus.repositories.sql;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.marco.javacovidstatus.repositories.model.EntityNationalData;

/**
 * This interface provides the functionalities required to store the national
 * data into a relational database
 * 
 * @author Marco
 *
 */
public interface NationallDataSqlRepository extends CrudRepository<EntityNationalData, LocalDate> {

    public List<EntityNationalData> findAllByOrderByDateDesc();
}
