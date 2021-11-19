package com.marco.javacovidstatus.repositories.implementations;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;

import org.springframework.transaction.annotation.Transactional;

import com.marco.javacovidstatus.model.entitites.italianpopulation.EntityBoosterPopulation;
import com.marco.javacovidstatus.repositories.interfaces.BoosterPopulationRepo;

@Transactional
public class BoosterPopulationPostgres implements BoosterPopulationRepo {
    @PersistenceContext
    private EntityManager em;

    @Override
    public boolean insert(EntityBoosterPopulation entity) {
        em.persist(entity);
        return true;
    }

    @Override
    public boolean deleteAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<EntityBoosterPopulation> query = cb.createCriteriaDelete(EntityBoosterPopulation.class);
        query.from(EntityBoosterPopulation.class);
        em.createQuery(query).executeUpdate();
        return true;
    }
}
