package com.marco.javacovidstatus.repositories.implementations;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;

import org.springframework.transaction.annotation.Transactional;

import com.marco.javacovidstatus.model.entitites.italianpopulation.EntityAdditionalDosePopulation;
import com.marco.javacovidstatus.repositories.interfaces.AdditionalDosePopulationRepo;

@Transactional
public class AdditionalDosePopulationPostgres implements AdditionalDosePopulationRepo{
    @PersistenceContext
    private EntityManager em;
    
    @Override
    public boolean insert(EntityAdditionalDosePopulation entity) {
        em.persist(entity);
        return true;
    }

    @Override
    public boolean deleteAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<EntityAdditionalDosePopulation> query = cb.createCriteriaDelete(EntityAdditionalDosePopulation.class);
        query.from(EntityAdditionalDosePopulation.class);
        em.createQuery(query).executeUpdate();
        return true;
    }

}
