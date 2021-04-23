package com.marco.javacovidstatus.repositories.implementations;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.marco.javacovidstatus.enums.Gender;
import com.marco.javacovidstatus.model.entitites.italianpopulation.EntityItalianPopulation;
import com.marco.javacovidstatus.model.entitites.italianpopulation.EntityItalianPopulation_;
import com.marco.javacovidstatus.model.entitites.italianpopulation.ItalianPopulationPk_;
import com.marco.javacovidstatus.repositories.interfaces.PopulationRepo;

@Transactional
public class PopulationRepoPostgres implements PopulationRepo {
    private static final Logger _LOGGER = LoggerFactory.getLogger(PopulationRepoPostgres.class);
    @PersistenceContext
    private EntityManager em;

    @Override
    public boolean insert(EntityItalianPopulation entity) {
        _LOGGER.trace("Inside: PopulationRepoPostgres.insert");
        em.persist(entity);
        return true;
    }

    @Override
    public boolean deleteAll() {
        // Truncate from table
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<EntityItalianPopulation> query = cb.createCriteriaDelete(EntityItalianPopulation.class);
        query.from(EntityItalianPopulation.class);
        em.createQuery(query).executeUpdate();
        return true;
    }

    @Override
    public Long getSumForAgesAndYear(int ageFrom, int ageTo, Gender gender, int year) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<EntityItalianPopulation> root = cq.from(EntityItalianPopulation.class);

        /*
         * https://www.postgresql.org/docs/8.1/functions-conditional.html
         * 
         * SELECT sum(counter) FROM italian_population where year = X and gender = 'Y'
         * and age between A and B;
         */
        // @formatter:off
        cq.multiselect(
                cb.coalesce(cb.sum(root.get(EntityItalianPopulation_.COUNTER)), 0L)
            )
        .where(
            cb.and(
                    cb.between(root.get(EntityItalianPopulation_.ID).get(ItalianPopulationPk_.AGE), ageFrom, ageTo),
                    cb.equal(root.get(EntityItalianPopulation_.ID).get(ItalianPopulationPk_.GENDER), gender),
                    cb.equal(root.get(EntityItalianPopulation_.ID).get(ItalianPopulationPk_.YEAR), year)
                )
            );

        // @formatter:on

        TypedQuery<Long> tq = em.createQuery(cq);
        List<Long> list = tq.getResultList();
        if (list.size() == 1) {
            return list.get(0);
        }
        return 0L;
    }

}