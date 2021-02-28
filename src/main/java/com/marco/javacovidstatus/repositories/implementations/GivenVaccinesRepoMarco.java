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

import com.marco.javacovidstatus.model.entitites.DailySumGivenVaccines;
import com.marco.javacovidstatus.model.entitites.EntitySomministrazioneVaccini;
import com.marco.javacovidstatus.model.entitites.EntitySomministrazioneVacciniPk_;
import com.marco.javacovidstatus.model.entitites.EntitySomministrazioneVaccini_;
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

	@Override
	public List<EntitySomministrazioneVaccini> getGivenVaccinesBetween(LocalDate start, LocalDate end) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<EntitySomministrazioneVaccini> cq = cb.createQuery(EntitySomministrazioneVaccini.class);
		Root<EntitySomministrazioneVaccini> root = cq.from(EntitySomministrazioneVaccini.class);
		
		/*
		 * SELECT * FROM SOMMINISTRAZIONI_VACCINI WHERE DATE_DATA BETWEEN X AND Y
		 * ORDER BY DATE_DATA
		 */
		// @formatter:off
		cq.select(root).where(
				cb.between(root.get(EntitySomministrazioneVaccini_.ID).get(EntitySomministrazioneVacciniPk_.DATE), start, end)
			)
			.orderBy(
				cb.asc(root.get(EntitySomministrazioneVaccini_.ID).get(EntitySomministrazioneVacciniPk_.DATE))
			)
		;
		// @formatter:on
		
		TypedQuery<EntitySomministrazioneVaccini> tq = em.createQuery(cq);
		return tq.getResultList();
	}

	@Override
	public List<DailySumGivenVaccines> getDailySumGivenVaccinesBetween(LocalDate start, LocalDate end) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DailySumGivenVaccines> cq = cb.createQuery(DailySumGivenVaccines.class);
		Root<EntitySomministrazioneVaccini> root = cq.from(EntitySomministrazioneVaccini.class);
		
		// @formatter:off
		/*
		 * SELECT DATE_DATA, SUM(MEN_COUNTER), SUM(WOMEN_COUNTER), SUM(NHS_PEOPLE_COUNTER), 
		 * SUM(NON_NHS_PEOPLE_COUNTER), SUM(NURSING_HOME_COUNTER), SUM(OVER_80_COUNTER), 
		 * SUM(PUBLIC_ORDER_COUNTER), SUM(SCHOOL_STAFF_COUNTER), SUM(FIRST_DOSE_COUNTER), SUM(SECOND_DOSE_COUNTER)
		 *  
		 * FROM SOMMINISTRAZIONI_VACCINI WHERE DATE_DATA BETWEEN X AND Y
		 * 
		 * GROUP DATE_DATA
		 * ORDER BY DATE_DATA
		 */
		cq.multiselect(
				root.get(EntitySomministrazioneVaccini_.ID).get(EntitySomministrazioneVacciniPk_.DATE),
				cb.sum(root.get(EntitySomministrazioneVaccini_.MEN_COUNTER)),
				cb.sum(root.get(EntitySomministrazioneVaccini_.WOMEN_COUNTER)),
				cb.sum(root.get(EntitySomministrazioneVaccini_.NHS_PEOPLE_COUNTER)),
				cb.sum(root.get(EntitySomministrazioneVaccini_.NON_NHS_PEOPLE_COUNTER)),
				cb.sum(root.get(EntitySomministrazioneVaccini_.NURSING_HOME_COUNTER)),
				cb.sum(root.get(EntitySomministrazioneVaccini_.OVER80_COUNTER)),
				cb.sum(root.get(EntitySomministrazioneVaccini_.PUBLIC_ORDER_COUNTER)),
				cb.sum(root.get(EntitySomministrazioneVaccini_.SCHOOL_STAFF_COUNTER)),
				cb.sum(root.get(EntitySomministrazioneVaccini_.FIRST_DOSE_COUNTER)),
				cb.sum(root.get(EntitySomministrazioneVaccini_.SECOND_DOSE_COUNTER))
				)
			.where(
				cb.between(root.get(EntitySomministrazioneVaccini_.ID).get(EntitySomministrazioneVacciniPk_.DATE), start, end)
			)
			.groupBy(
					root.get(EntitySomministrazioneVaccini_.ID).get(EntitySomministrazioneVacciniPk_.DATE)
			)
			.orderBy(
				cb.asc(root.get(EntitySomministrazioneVaccini_.ID).get(EntitySomministrazioneVacciniPk_.DATE))
			);
		// @formatter:on
		
		TypedQuery<DailySumGivenVaccines> tq = em.createQuery(cq);
		return tq.getResultList();
	}

}
