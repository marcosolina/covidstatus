package com.marco.javacovidstatus.repositories.interfaces;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.marco.javacovidstatus.model.entitites.infections.EntityNationalData;

/**
 * Standard Spring interface to perform CRUD operation on the
 * {@link EntityNationalData} entity
 * @author Marco
 *
 */
public interface NationalInfectionDataRepository extends CrudRepository<EntityNationalData, LocalDate> {

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
