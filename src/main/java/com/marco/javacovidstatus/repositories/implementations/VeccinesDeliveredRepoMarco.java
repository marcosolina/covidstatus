package com.marco.javacovidstatus.repositories.implementations;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.transaction.annotation.Transactional;

import com.marco.javacovidstatus.model.entitites.EntityVacciniConsegne;
import com.marco.javacovidstatus.model.entitites.EntityVacciniConsegnePk_;
import com.marco.javacovidstatus.model.entitites.EntityVacciniConsegne_;
import com.marco.javacovidstatus.repositories.interfaces.VeccinesDeliveredRepo;

/**
 * My implementation of the interface
 * 
 * @author Marco
 *
 */
@Transactional
public class VeccinesDeliveredRepoMarco implements VeccinesDeliveredRepo {
	@PersistenceContext
	private EntityManager em;

	@Override
	public boolean saveEntity(EntityVacciniConsegne entity) {
		em.persist(entity);
		return true;
	}

	@Override
	public boolean deleteAll() {
		// Truncate from table
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<EntityVacciniConsegne> query = cb.createCriteriaDelete(EntityVacciniConsegne.class);
		query.from(EntityVacciniConsegne.class);
		em.createQuery(query).executeUpdate();
		return true;
	}

	@Override
	public List<EntityVacciniConsegne> getDeliveredVaccinesBetween(LocalDate start, LocalDate end) {
		CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<EntityVacciniConsegne> cq = cb.createQuery(EntityVacciniConsegne.class);
        Root<EntityVacciniConsegne> root = cq.from(EntityVacciniConsegne.class);
        
        
        /*
         * SELECT * FROM VACCINI_CONSEGNE WHERE DATE_DATA BETWEEN X AND Y 
         * ORDER BY DATE_DATA
         */
        // @formatter:off
        cq.select(root).where(
                cb.and(
                    cb.between(root.get(EntityVacciniConsegne_.ID).get(EntityVacciniConsegnePk_.DATE), start, end)
                    )
                )
            .orderBy(
                cb.asc(root.get(EntityVacciniConsegne_.ID).get(EntityVacciniConsegnePk_.DATE))
            );
        // @formatter:on

        TypedQuery<EntityVacciniConsegne> tq = em.createQuery(cq);
        return tq.getResultList();
	}

}
