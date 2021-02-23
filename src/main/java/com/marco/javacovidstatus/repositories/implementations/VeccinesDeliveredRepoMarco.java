package com.marco.javacovidstatus.repositories.implementations;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;

import org.springframework.transaction.annotation.Transactional;

import com.marco.javacovidstatus.model.entitites.EntityVacciniConsegne;
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

}
