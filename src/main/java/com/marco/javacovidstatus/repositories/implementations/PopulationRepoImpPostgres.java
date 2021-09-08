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
import com.marco.javacovidstatus.model.entitites.italianpopulation.EntityItalianPopulationPk_;
import com.marco.javacovidstatus.repositories.interfaces.PopulationRepo;

/**
 * PostgreSQL implementation of the {@link PopulationRepo}
 * 
 * @author Marco
 *
 */
@Transactional
public class PopulationRepoImpPostgres implements PopulationRepo {
    private static final Logger _LOGGER = LoggerFactory.getLogger(PopulationRepoImpPostgres.class);
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
                    cb.between(root.get(EntityItalianPopulation_.ID).get(EntityItalianPopulationPk_.AGE), ageFrom, ageTo),
                    cb.equal(root.get(EntityItalianPopulation_.ID).get(EntityItalianPopulationPk_.GENDER), gender),
                    cb.equal(root.get(EntityItalianPopulation_.ID).get(EntityItalianPopulationPk_.YEAR), year)
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

    @Override
    public Long getSumForYear(Gender gender, int year) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<EntityItalianPopulation> root = cq.from(EntityItalianPopulation.class);

        /*
         * https://www.postgresql.org/docs/8.1/functions-conditional.html
         * 
         * SELECT sum(counter) FROM italian_population where year = X and gender = 'Y'
         */
        // @formatter:off
        cq.multiselect(
                cb.coalesce(cb.sum(root.get(EntityItalianPopulation_.COUNTER)), 0L)
            )
        .where(
            cb.and(
                    cb.equal(root.get(EntityItalianPopulation_.ID).get(EntityItalianPopulationPk_.GENDER), gender),
                    cb.equal(root.get(EntityItalianPopulation_.ID).get(EntityItalianPopulationPk_.YEAR), year)
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

	@Override
	public Long getSumForYearPerRegion(Gender gender, int year, String regionCode) {
		CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<EntityItalianPopulation> root = cq.from(EntityItalianPopulation.class);

        /*
         * https://www.postgresql.org/docs/8.1/functions-conditional.html
         * 
         * SELECT sum(counter) FROM italian_population where year = X and gender = 'Y' and region code = 'Z'
         */
        // @formatter:off
        cq.multiselect(
                cb.coalesce(cb.sum(root.get(EntityItalianPopulation_.COUNTER)), 0L)
            )
        .where(
            cb.and(
                    cb.equal(root.get(EntityItalianPopulation_.ID).get(EntityItalianPopulationPk_.GENDER), gender),
                    cb.equal(root.get(EntityItalianPopulation_.ID).get(EntityItalianPopulationPk_.YEAR), year),
                    cb.equal(root.get(EntityItalianPopulation_.ID).get(EntityItalianPopulationPk_.REGION_CODE), regionCode)
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
