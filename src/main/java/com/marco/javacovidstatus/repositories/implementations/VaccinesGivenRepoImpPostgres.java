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

import com.marco.javacovidstatus.model.entitites.vaccines.AgeRangeGivenVaccines;
import com.marco.javacovidstatus.model.entitites.vaccines.DailySumGivenVaccines;
import com.marco.javacovidstatus.model.entitites.vaccines.DoseCounter;
import com.marco.javacovidstatus.model.entitites.vaccines.EntitySomministrazioneVaccini;
import com.marco.javacovidstatus.model.entitites.vaccines.EntitySomministrazioneVacciniPk_;
import com.marco.javacovidstatus.model.entitites.vaccines.EntitySomministrazioneVaccini_;
import com.marco.javacovidstatus.model.entitites.vaccines.TotalVaccineGivenPerRegion;
import com.marco.javacovidstatus.model.entitites.vaccines.VaccinesGivenPerRegion;
import com.marco.javacovidstatus.repositories.interfaces.VaccinesGivenRepo;
import com.marco.javacovidstatus.utils.Constants;

/**
 * My implementation of the interface
 * 
 * @author Marco
 *
 */
@Transactional
public class VaccinesGivenRepoImpPostgres implements VaccinesGivenRepo {
	private static final Logger _LOGGER = LoggerFactory.getLogger(VaccinesGivenRepoImpPostgres.class);
	@PersistenceContext
	private EntityManager em;

	@Override
	public boolean saveEntity(EntitySomministrazioneVaccini entity) {
		em.persist(entity);
		return true;
	}

	@Override
	public boolean deleteAllData() {
		// Truncate from table
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<EntitySomministrazioneVaccini> query = cb
				.createCriteriaDelete(EntitySomministrazioneVaccini.class);
		query.from(EntitySomministrazioneVaccini.class);
		em.createQuery(query).executeUpdate();
		return true;
	}

	@Override
	public List<DailySumGivenVaccines> getDailyReportOfGivenVaccinesBetween(LocalDate start, LocalDate end) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DailySumGivenVaccines> cq = cb.createQuery(DailySumGivenVaccines.class);
		Root<EntitySomministrazioneVaccini> root = cq.from(EntitySomministrazioneVaccini.class);

		// @formatter:off
		/*
		 * SELECT DATE_DATA, SUM(MEN_COUNTER), SUM(WOMEN_COUNTER), SUM(NHS_PEOPLE_COUNTER), 
		 * SUM(NON_NHS_PEOPLE_COUNTER), SUM(NURSING_HOME_COUNTER),
		 * SUM(AGE_60_69_COUNTER), SUM(AGE_70_79_COUNTER), SUM(OVER_80_COUNTER), 
		 * SUM(PUBLIC_ORDER_COUNTER), SUM(SCHOOL_STAFF_COUNTER), SUM(FRAGILE_PEOPLE_COUNTER), 
		 * SUM(OTHER_PEOPLE_COUNTER), SUM(FIRST_DOSE_COUNTER), SUM(SECOND_DOSE_COUNTER)
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
				cb.sum(root.get(EntitySomministrazioneVaccini_.AGE6069COUNTER)),
				cb.sum(root.get(EntitySomministrazioneVaccini_.AGE7079COUNTER)),
				cb.sum(root.get(EntitySomministrazioneVaccini_.OVER80_COUNTER)),
				cb.sum(root.get(EntitySomministrazioneVaccini_.PUBLIC_ORDER_COUNTER)),
				cb.sum(root.get(EntitySomministrazioneVaccini_.SCHOOL_STAFF_COUNTER)),
				cb.sum(root.get(EntitySomministrazioneVaccini_.FRAGILE_PEOPLE_COUNTER)),
				cb.sum(root.get(EntitySomministrazioneVaccini_.OTHER_PEOPLE_COUNTER)),
				cb.sum(root.get(EntitySomministrazioneVaccini_.FIRST_DOSE_COUNTER)),
				cb.sum(root.get(EntitySomministrazioneVaccini_.SECOND_DOSE_COUNTER)),
                cb.sum(root.get(EntitySomministrazioneVaccini_.MONO_DOSE_COUNTER)),
                cb.sum(root.get(EntitySomministrazioneVaccini_.DOSE_AFTER_INFECT_COUNTER)),
                cb.sum(root.get(EntitySomministrazioneVaccini_.THIRD_DOSE_COUNTER)),
                cb.sum(root.get(EntitySomministrazioneVaccini_.FOURTH_DOSE_COUNTER)),
                cb.sum(root.get(EntitySomministrazioneVaccini_.FIFTH_DOSE_COUNTER))
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
		_LOGGER.debug("Adding mepty rows: addMissingRowsForNoVaccinationDays");

		String tableName = "somministrazioni_vaccini";

		// @formatter:off
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
		sql.append("b.age_60_69_counter,");
		sql.append("b.age_70_79_counter,");
		sql.append("b.over_80_counter,");
		sql.append("b.public_order_counter,");
		sql.append("b.school_staff_counter,");
		sql.append("b.fragile_people_counter,");
		sql.append("b.other_people_counter,");
		sql.append("b.first_dose_counter,");
		sql.append("b.second_dose_counter, ");
		sql.append("b.mono_dose_counter, ");
		sql.append("b.dose_after_infect_counter, ");
		sql.append("b.third_dose_counter, ");
		sql.append("b.fourth_dose_counter, ");
		sql.append("b.fifth_dose_counter ");
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
		sql.append("age_60_69_counter = 0,");
		sql.append("age_70_79_counter = 0,");
		sql.append("over_80_counter = 0,");
		sql.append("public_order_counter = 0,");
		sql.append("school_staff_counter = 0,");
		sql.append("fragile_people_counter = 0,");
		sql.append("other_people_counter = 0,");
		sql.append("first_dose_counter = 0,");
		sql.append("second_dose_counter = 0, ");
		sql.append("mono_dose_counter = 0, ");
		sql.append("dose_after_infect_counter = 0, ");
		sql.append("third_dose_counter = 0, ");
		sql.append("fourth_dose_counter = 0, ");
		sql.append("fifth_dose_counter = 0 ");
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
		// @formatter:on

		sqls.forEach(sb -> {
			_LOGGER.debug("Executing query: " + sb.toString());
			Query query = em.createNativeQuery(sb.toString());
			query.executeUpdate();
			_LOGGER.debug("Query executed");
		});
		
		_LOGGER.debug("Finish adding mepty rows: addMissingRowsForNoVaccinationDays");
	}

	@Override
	public List<DoseCounter> getListOfGivenDosesBetween(LocalDate start, LocalDate end) {
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<DoseCounter> cq = cb.createQuery(DoseCounter.class);
		Root<EntitySomministrazioneVaccini> root = cq.from(EntitySomministrazioneVaccini.class);

		/*
		 * SELECT sum(first_dose_counter), sum(second_dose_counter), sum(mono_dose_counter) FROM
		 * somministrazioni_vaccini WHERE DATE_DATA BETWEEN X AND Y
		 */
		// @formatter:off
		cq.multiselect(
				cb.sum(root.get(EntitySomministrazioneVaccini_.FIRST_DOSE_COUNTER)),
				cb.sum(root.get(EntitySomministrazioneVaccini_.SECOND_DOSE_COUNTER)),
                cb.sum(root.get(EntitySomministrazioneVaccini_.MONO_DOSE_COUNTER)),
                cb.sum(root.get(EntitySomministrazioneVaccini_.DOSE_AFTER_INFECT_COUNTER)),
                cb.sum(root.get(EntitySomministrazioneVaccini_.THIRD_DOSE_COUNTER)),
                cb.sum(root.get(EntitySomministrazioneVaccini_.FOURTH_DOSE_COUNTER)),
                cb.sum(root.get(EntitySomministrazioneVaccini_.FIFTH_DOSE_COUNTER))
			)
		.where(
				cb.between(root.get(EntitySomministrazioneVaccini_.ID).get(EntitySomministrazioneVacciniPk_.DATE), start, end)
			);
		
		// @formatter:on

		TypedQuery<DoseCounter> tq = em.createQuery(cq);
		return tq.getResultList();
	}

	@Override
	public List<AgeRangeGivenVaccines> getListOfGivenVaccinesBetweenDatesGroupByAgeRange(LocalDate start, LocalDate end) {
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<AgeRangeGivenVaccines> cq = cb.createQuery(AgeRangeGivenVaccines.class);
		Root<EntitySomministrazioneVaccini> root = cq.from(EntitySomministrazioneVaccini.class);

		/*
		 * SELECT age_range, sum(first_dose_counter), sum (second_dose_counter), sum (mono_dose_counter) from
		 * somministrazioni_vaccini WHERE DATE_DATA BETWEEN X AND Y group by age_range
		 */

		// @formatter:off
		cq.multiselect(
				root.get(EntitySomministrazioneVaccini_.ID).get(EntitySomministrazioneVacciniPk_.AGE_RANGE),
				cb.sum(root.get(EntitySomministrazioneVaccini_.FIRST_DOSE_COUNTER)),
				cb.sum(root.get(EntitySomministrazioneVaccini_.SECOND_DOSE_COUNTER)),
                cb.sum(root.get(EntitySomministrazioneVaccini_.MONO_DOSE_COUNTER)),
                cb.sum(root.get(EntitySomministrazioneVaccini_.DOSE_AFTER_INFECT_COUNTER)),
                cb.sum(root.get(EntitySomministrazioneVaccini_.THIRD_DOSE_COUNTER)),
                cb.sum(root.get(EntitySomministrazioneVaccini_.FOURTH_DOSE_COUNTER)),
                cb.sum(root.get(EntitySomministrazioneVaccini_.FIFTH_DOSE_COUNTER))
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

	@Override
	public LocalDate getLastDateForAvailableData() {
		/*
		 * SELECT MAX(DATE_DATA) AS DATE_DATA FROM SOMMINISTRAZIONI_VACCINI
		 */
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<LocalDate> cq = cb.createQuery(LocalDate.class);
		Root<EntitySomministrazioneVaccini> root = cq.from(EntitySomministrazioneVaccini.class);

		// @formatter:off
		cq.select(
			cb.greatest(
				root.get(EntitySomministrazioneVaccini_.ID).<LocalDate>get(EntitySomministrazioneVacciniPk_.DATE)
				)
		);
		
		// @formatter:on
		TypedQuery<LocalDate> tq = em.createQuery(cq);
		return tq.getSingleResult();
	}

	@Override
	public Long getTotalGivenVaccines() {
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<DoseCounter> cq = cb.createQuery(DoseCounter.class);
		Root<EntitySomministrazioneVaccini> root = cq.from(EntitySomministrazioneVaccini.class);

		/*
		 * https://www.postgresql.org/docs/8.1/functions-conditional.html
		 * 
		 * SELECT COALESCE(SUM(first_dose_counter), 0), COALESCE(SUM(second_dose_counter), 0), COALESCE(SUM(MONO_dose_counter), 0)
		 * , COALESCE(SUM(DOSE_AFTER_INFECT_COUNTER), 0) 
		 * FROM
		 * somministrazioni_vaccini
		 */
		// @formatter:off
		cq.multiselect(
				cb.coalesce(cb.sum(root.get(EntitySomministrazioneVaccini_.FIRST_DOSE_COUNTER)), 0L),
				cb.coalesce(cb.sum(root.get(EntitySomministrazioneVaccini_.SECOND_DOSE_COUNTER)), 0L),
				cb.coalesce(cb.sum(root.get(EntitySomministrazioneVaccini_.MONO_DOSE_COUNTER)), 0L),
                cb.coalesce(cb.sum(root.get(EntitySomministrazioneVaccini_.DOSE_AFTER_INFECT_COUNTER)), 0L),
                cb.coalesce(cb.sum(root.get(EntitySomministrazioneVaccini_.THIRD_DOSE_COUNTER)), 0L),
                cb.coalesce(cb.sum(root.get(EntitySomministrazioneVaccini_.FOURTH_DOSE_COUNTER)), 0L),
                cb.coalesce(cb.sum(root.get(EntitySomministrazioneVaccini_.FIFTH_DOSE_COUNTER)), 0L)
			);
		
		// @formatter:on

		TypedQuery<DoseCounter> tq = em.createQuery(cq);
		List<DoseCounter> list = tq.getResultList();
		if (list.size() == 1) {
		    DoseCounter dc = list.get(0);
		    Long counter = dc.getFirstDoseCounter();
		    counter += dc.getSecondDoseCounter();
		    counter += dc.getMonoDoseCounter();
		    counter += dc.getDoseAfterInfectCounter();
		    counter += dc.getThirdDoseCounter();
		    counter += dc.getFourthDoseCounter();
		    counter += dc.getFifthDoseCounter();
			return counter;
		}
		return 0L;
	}

	@Override
	public List<TotalVaccineGivenPerRegion> getListTotalVaccinatedPeoplePerRegion() {
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<TotalVaccineGivenPerRegion> cq = cb.createQuery(TotalVaccineGivenPerRegion.class);
		Root<EntitySomministrazioneVaccini> root = cq.from(EntitySomministrazioneVaccini.class);

		/*
		 * SELECT region_code, sum(first_dose_counter + second_dose_counter + MONO_DOSE_COUNTER + DOSE_AFTER_INFECT_COUNTER)
  		 * FROM somministrazioni_vaccini group by region_code order by region_code
		 */
		
		// @formatter:off
		cq.multiselect(
				root.get(EntitySomministrazioneVaccini_.ID).get(EntitySomministrazioneVacciniPk_.REGION_CODE),
				cb.sum(
				    cb.sum(				    
				            cb.sum(
				                    cb.sum(root.get(EntitySomministrazioneVaccini_.FIRST_DOSE_COUNTER)),
				                    cb.sum(root.get(EntitySomministrazioneVaccini_.SECOND_DOSE_COUNTER))
				                    ),
				            cb.sum(
				                    cb.sum(root.get(EntitySomministrazioneVaccini_.MONO_DOSE_COUNTER)),
				                    cb.sum(root.get(EntitySomministrazioneVaccini_.DOSE_AFTER_INFECT_COUNTER))
				                    )
		            ),
                    cb.sum(
                    		cb.sum(
                    				cb.sum(root.get(EntitySomministrazioneVaccini_.THIRD_DOSE_COUNTER)),
                    				cb.sum(root.get(EntitySomministrazioneVaccini_.FOURTH_DOSE_COUNTER))
                            ),
                            cb.sum(root.get(EntitySomministrazioneVaccini_.FIFTH_DOSE_COUNTER))
                        )
				)
			)
		.groupBy(
				root.get(EntitySomministrazioneVaccini_.ID).get(EntitySomministrazioneVacciniPk_.REGION_CODE)
			)
		.orderBy(cb.asc(root.get(EntitySomministrazioneVaccini_.ID).get(EntitySomministrazioneVacciniPk_.REGION_CODE)));
		
		// @formatter:on

		TypedQuery<TotalVaccineGivenPerRegion> tq = em.createQuery(cq);
		return tq.getResultList();
	}

	@Override
	public void deleteInformationForDate(LocalDate date) {
		
		/*
		 * DELETE FROM SOMMINISTRAZIONI_VACCINI WHERE DATE_DATA = X
		 */
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<EntitySomministrazioneVaccini> cd = cb.createCriteriaDelete(EntitySomministrazioneVaccini.class);
		Root<EntitySomministrazioneVaccini> root = cd.from(EntitySomministrazioneVaccini.class);
	
		// @formatter:off
		cd.where(
			cb.equal(root.get(EntitySomministrazioneVaccini_.ID).<LocalDate>get(EntitySomministrazioneVacciniPk_.DATE), date)
		);
		// @formatter:on
		em.createQuery(cd).executeUpdate();
	}

	@Override
	public List<AgeRangeGivenVaccines> getListOfTotalGivenVaccinesGorupByAgeRange() {
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<AgeRangeGivenVaccines> cq = cb.createQuery(AgeRangeGivenVaccines.class);
		Root<EntitySomministrazioneVaccini> root = cq.from(EntitySomministrazioneVaccini.class);

		/*
		 * SELECT age_range, sum(first_dose_counter), sum (second_dose_counter), sum (mono_dose_counter) from
		 * somministrazioni_vaccini group by age_range
		 */

		// @formatter:off
		cq.multiselect(
				root.get(EntitySomministrazioneVaccini_.ID).get(EntitySomministrazioneVacciniPk_.AGE_RANGE),
				cb.sum(root.get(EntitySomministrazioneVaccini_.FIRST_DOSE_COUNTER)),
				cb.sum(root.get(EntitySomministrazioneVaccini_.SECOND_DOSE_COUNTER)),
                cb.sum(root.get(EntitySomministrazioneVaccini_.MONO_DOSE_COUNTER)),
                cb.sum(root.get(EntitySomministrazioneVaccini_.DOSE_AFTER_INFECT_COUNTER)),
                cb.sum(root.get(EntitySomministrazioneVaccini_.THIRD_DOSE_COUNTER)),
                cb.sum(root.get(EntitySomministrazioneVaccini_.FOURTH_DOSE_COUNTER)),
                cb.sum(root.get(EntitySomministrazioneVaccini_.FIFTH_DOSE_COUNTER))
			)
		.groupBy(
				root.get(EntitySomministrazioneVaccini_.ID).get(EntitySomministrazioneVacciniPk_.AGE_RANGE)
		);
		// @formatter:on

		TypedQuery<AgeRangeGivenVaccines> tq = em.createQuery(cq);
		return tq.getResultList();
	}

    @Override
    public AgeRangeGivenVaccines getTotalPeolpleVaccinated() {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<DoseCounter> cq = cb.createQuery(DoseCounter.class);
        Root<EntitySomministrazioneVaccini> root = cq.from(EntitySomministrazioneVaccini.class);

        /*
         * https://www.postgresql.org/docs/8.1/functions-conditional.html
         * 
         * SELECT COALESCE(SUM(first_dose_counter), 0), COALESCE(SUM(second_dose_counter), 0), COALESCE(SUM(MONO_dose_counter), 0)
         * , COALESCE(SUM(DOSE_AFTER_INFECT_COUNTER), 0) 
         * FROM
         * somministrazioni_vaccini
         */
        // @formatter:off
        cq.multiselect(
                cb.coalesce(cb.sum(root.get(EntitySomministrazioneVaccini_.FIRST_DOSE_COUNTER)), 0L),
                cb.coalesce(cb.sum(root.get(EntitySomministrazioneVaccini_.SECOND_DOSE_COUNTER)), 0L),
                cb.coalesce(cb.sum(root.get(EntitySomministrazioneVaccini_.MONO_DOSE_COUNTER)), 0L),
                cb.coalesce(cb.sum(root.get(EntitySomministrazioneVaccini_.DOSE_AFTER_INFECT_COUNTER)), 0L),
                cb.coalesce(cb.sum(root.get(EntitySomministrazioneVaccini_.THIRD_DOSE_COUNTER)), 0L),
                cb.coalesce(cb.sum(root.get(EntitySomministrazioneVaccini_.FOURTH_DOSE_COUNTER)), 0L),
                cb.coalesce(cb.sum(root.get(EntitySomministrazioneVaccini_.FIFTH_DOSE_COUNTER)), 0L)
            );
        
        // @formatter:on

        TypedQuery<DoseCounter> tq = em.createQuery(cq);
        List<DoseCounter> list = tq.getResultList();
        
        if (list.size() == 1) {
            DoseCounter dc = list.get(0);
            return new AgeRangeGivenVaccines(
                    Constants.LABEL_VACCINES_GIVEN_TOTAL,
                    dc.getFirstDoseCounter(),
                    dc.getSecondDoseCounter(),
                    dc.getMonoDoseCounter(),
                    dc.getDoseAfterInfectCounter(),
                    dc.getThirdDoseCounter(),
                    dc.getFourthDoseCounter(),
                    dc.getFifthDoseCounter());
        }
        return new AgeRangeGivenVaccines(Constants.LABEL_VACCINES_GIVEN_TOTAL, 0L, 0L, 0L, 0L, 0L, 0L, 0L);
    }

	@Override
	public List<VaccinesGivenPerRegion> getListOfTotalGivenVaccinesGorupByRegion() {
		
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<VaccinesGivenPerRegion> cq = cb.createQuery(VaccinesGivenPerRegion.class);
		Root<EntitySomministrazioneVaccini> root = cq.from(EntitySomministrazioneVaccini.class);
		/*
		 * SELECT REGION_CODE, sum(first_dose_counter), sum (second_dose_counter), sum (mono_dose_counter), sum(dose_fater_infect_counter) from
		 * somministrazioni_vaccini group by REGION_CODE
		 */

		// @formatter:off
		cq.multiselect(
				root.get(EntitySomministrazioneVaccini_.ID).get(EntitySomministrazioneVacciniPk_.REGION_CODE),
				cb.sum(root.get(EntitySomministrazioneVaccini_.FIRST_DOSE_COUNTER)),
				cb.sum(root.get(EntitySomministrazioneVaccini_.SECOND_DOSE_COUNTER)),
                cb.sum(root.get(EntitySomministrazioneVaccini_.MONO_DOSE_COUNTER)),
                cb.sum(root.get(EntitySomministrazioneVaccini_.DOSE_AFTER_INFECT_COUNTER)),
                cb.sum(root.get(EntitySomministrazioneVaccini_.THIRD_DOSE_COUNTER)),
		        cb.sum(root.get(EntitySomministrazioneVaccini_.FOURTH_DOSE_COUNTER)),
		        cb.sum(root.get(EntitySomministrazioneVaccini_.FIFTH_DOSE_COUNTER)))
		.groupBy(
				root.get(EntitySomministrazioneVaccini_.ID).get(EntitySomministrazioneVacciniPk_.REGION_CODE)
		);
		// @formatter:on

		TypedQuery<VaccinesGivenPerRegion> tq = em.createQuery(cq);
		return tq.getResultList();
	}

}
