package com.marco.javacovidstatus.repositories.implementations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.marco.javacovidstatus.model.entitites.AgeRangeGivenVaccines;
import com.marco.javacovidstatus.model.entitites.DailySumGivenVaccines;
import com.marco.javacovidstatus.model.entitites.DoseCounter;
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
	private static final Logger _LOGGER = LoggerFactory.getLogger(GivenVaccinesRepoMarco.class);
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

	@Override
	public void addMissingRowsForNoVaccinationDays() {
		_LOGGER.debug("Adding mepty rows");
		
		String tableName = "somministrazioni_vaccini";
		
		List<StringBuilder> sqls = new ArrayList<>();
		sqls.add(new StringBuilder(String.format("create table tmp_dates as select date_data from %s group by date_data with data", tableName)));
		sqls.add(new StringBuilder(String.format("create table tmp_regions as select region_code from %s group by region_code with data", tableName)));
		sqls.add(new StringBuilder(String.format("create table tmp_supplier as select supplier from %s group by supplier with data", tableName)));
		sqls.add(new StringBuilder(String.format("create table tmp_age as select age_range from %s group by age_range with data", tableName)));
		sqls.add(new StringBuilder("create table cartesian_table as select * from tmp_dates , tmp_regions , tmp_supplier, tmp_age order by date_data, region_code, supplier, age_range with data"));
		
		StringBuilder sql = new StringBuilder();
		sql.append("create table filldata2 as select a.region_code, a.date_data, a.supplier, a.age_range, ");
		sql.append("b.men_counter,");
		sql.append("b.women_counter,");
		sql.append("b.nhs_people_counter,");
		sql.append("b.non_nhs_people_counter,");
		sql.append("b.nursing_home_counter,");
		sql.append("b.over_80_counter,");
		sql.append("b.public_order_counter,");
		sql.append("b.school_staff_counter,");
		sql.append("b.first_dose_counter,");
		sql.append("b.second_dose_counter ");
		sql.append("from cartesian_table as a ");
		sql.append(String.format("left join %s as b ", tableName));
		sql.append("on a.date_data = b.date_data and ");
		sql.append("a.region_code = b.region_code and ");
		sql.append("a.supplier = b.supplier and ");
		sql.append("a.age_range = b.age_range with data");
		
		sqls.add(sql);
		
		sql = new StringBuilder();
		sql.append("update filldata2 set ");
		sql.append("men_counter = 0,");
		sql.append("women_counter = 0,");
		sql.append("nhs_people_counter = 0,");
		sql.append("non_nhs_people_counter = 0,");
		sql.append("nursing_home_counter = 0,");
		sql.append("over_80_counter = 0,");
		sql.append("public_order_counter = 0,");
		sql.append("school_staff_counter = 0,");
		sql.append("first_dose_counter = 0,");
		sql.append("second_dose_counter = 0 ");
		sql.append("where men_counter is null");
		
		sqls.add(sql);
		
		sqls.add(new StringBuilder(String.format("truncate %s", tableName)));
		sqls.add(new StringBuilder(String.format("insert into %s select * from filldata2", tableName)));
		sqls.add(new StringBuilder("drop table tmp_dates"));
		sqls.add(new StringBuilder("drop table tmp_regions"));
		sqls.add(new StringBuilder("drop table tmp_supplier"));
		sqls.add(new StringBuilder("drop table tmp_age"));
		sqls.add(new StringBuilder("drop table cartesian_table"));
		sqls.add(new StringBuilder("drop table filldata2"));
		
		sqls.forEach(sb -> {
			Query query = em.createNativeQuery(sb.toString());
			query.executeUpdate();
		});
	}

	@Override
	public List<DoseCounter> getDosesCounterVaccinesBetween(LocalDate start, LocalDate end) {
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<DoseCounter> cq = cb.createQuery(DoseCounter.class);
		Root<EntitySomministrazioneVaccini> root = cq.from(EntitySomministrazioneVaccini.class);

		/*
		 * SELECT sum(first_dose_counter), sum(second_dose_counter) FROM somministrazioni_vaccini
		 * WHERE DATE_DATA BETWEEN X AND Y
		 */
		// @formatter:off
		cq.multiselect(
				cb.sum(root.get(EntitySomministrazioneVaccini_.FIRST_DOSE_COUNTER)),
				cb.sum(root.get(EntitySomministrazioneVaccini_.SECOND_DOSE_COUNTER))
			)
		.where(
				cb.between(root.get(EntitySomministrazioneVaccini_.ID).get(EntitySomministrazioneVacciniPk_.DATE), start, end)
			);
		
		// @formatter:on

		TypedQuery<DoseCounter> tq = em.createQuery(cq);
		return tq.getResultList();
	}

	@Override
	public List<AgeRangeGivenVaccines> getDeliveredVaccinesPerAgeRange(LocalDate start, LocalDate end) {
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<AgeRangeGivenVaccines> cq = cb.createQuery(AgeRangeGivenVaccines.class);
		Root<EntitySomministrazioneVaccini> root = cq.from(EntitySomministrazioneVaccini.class);

		/*
		 * SELECT age_range, sum(men_counter), sum (women_counter) from somministrazioni_vaccini
		 * WHERE DATE_DATA BETWEEN X AND Y
		 * group by age_range
		 */
		
		// @formatter:off
		cq.multiselect(
				root.get(EntitySomministrazioneVaccini_.ID).get(EntitySomministrazioneVacciniPk_.AGE_RANGE),
				cb.sum(root.get(EntitySomministrazioneVaccini_.MEN_COUNTER)),
				cb.sum(root.get(EntitySomministrazioneVaccini_.WOMEN_COUNTER))
			)
		.where(
				cb.between(root.get(EntitySomministrazioneVaccini_.ID).get(EntitySomministrazioneVacciniPk_.DATE), start, end)
			)
		.groupBy(
				root.get(EntitySomministrazioneVaccini_.ID).get(EntitySomministrazioneVacciniPk_.AGE_RANGE)
		);
		// @formatter:on

		TypedQuery<AgeRangeGivenVaccines> tq = em.createQuery(cq);
		return tq.getResultList();
	}

}
