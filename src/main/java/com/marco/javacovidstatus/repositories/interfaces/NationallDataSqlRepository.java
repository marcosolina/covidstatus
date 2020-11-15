package com.marco.javacovidstatus.repositories.interfaces;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.marco.javacovidstatus.model.entitites.EntityNationalData;

/**
 * This interface provides the functionalities required to store the national
 * data into a relational database
 * 
 * @author Marco
 *
 */
public interface NationallDataSqlRepository extends CrudRepository<EntityNationalData, LocalDate> {

    public List<EntityNationalData> findAllByOrderByDateDesc();
    
    public List<EntityNationalData> findAllByOrderByDateAsc();
    
    /**
     * It returns the data in between the range
     * @see <a href="https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation">Spring Documentation</a>
     * @param start
     * @param end
     * @return
     */
    public List<EntityNationalData> findByDateBetweenOrderByDateAsc(LocalDate start, LocalDate end);
}
