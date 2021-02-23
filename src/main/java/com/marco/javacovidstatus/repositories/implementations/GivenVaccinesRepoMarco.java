package com.marco.javacovidstatus.repositories.implementations;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;

import org.springframework.transaction.annotation.Transactional;

import com.marco.javacovidstatus.model.entitites.EntitySomministrazioneVaccini;
import com.marco.javacovidstatus.repositories.interfaces.GivenVaccinesRepo;

/**
 * My implementation of the interface
 * 
 * @author Marco
 *
 */
@Transactional
public class GivenVaccinesRepoMarco implements GivenVaccinesRepo {
	@PersistenceContext
    private EntityManager em;
	
	@Override
	public boolean saveEntity(EntitySomministrazioneVaccini entity) {
		em.persist(entity);
		return true;
	}

	@Override
	public boolean deleteAll() {
		// Truncate from table
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<EntitySomministrazioneVaccini> query = cb.createCriteriaDelete(EntitySomministrazioneVaccini.class);
		query.from(EntitySomministrazioneVaccini.class);
		em.createQuery(query).executeUpdate();
		return true;
	}

}
