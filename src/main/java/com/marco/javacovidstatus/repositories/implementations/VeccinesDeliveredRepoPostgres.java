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

import com.marco.javacovidstatus.model.entitites.EntityVacciniConsegne;
import com.marco.javacovidstatus.model.entitites.EntityVacciniConsegnePk_;
import com.marco.javacovidstatus.model.entitites.EntityVacciniConsegne_;
import com.marco.javacovidstatus.model.entitites.TotalVaccineDeliveredPerRegion;
import com.marco.javacovidstatus.model.entitites.VacciniConsegne;
import com.marco.javacovidstatus.model.entitites.VeccinesDeliveredPerSupplier;
import com.marco.javacovidstatus.repositories.interfaces.VeccinesDeliveredRepo;

/**
 * My implementation of the interface
 * 
 * @author Marco
 *
 */
@Transactional
public class VeccinesDeliveredRepoPostgres implements VeccinesDeliveredRepo {
	private static final Logger _LOGGER = LoggerFactory.getLogger(VeccinesDeliveredRepoPostgres.class);

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
	public List<VacciniConsegne> getDeliveredVaccinesBetween(LocalDate start, LocalDate end) {
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<VacciniConsegne> cq = cb.createQuery(VacciniConsegne.class);
		Root<EntityVacciniConsegne> root = cq.from(EntityVacciniConsegne.class);

		/*
		 * SELECT region_code, date_data, sum(doses_delivered) FROM
		 * public.vaccini_consegne WHERE DATE_DATA BETWEEN X AND Y group by region_code,
		 * date_data order by date_data
		 */
		// @formatter:off
		cq.multiselect(
				root.get(EntityVacciniConsegne_.ID).get(EntityVacciniConsegnePk_.REGION_CODE),
				root.get(EntityVacciniConsegne_.ID).get(EntityVacciniConsegnePk_.DATE),
				cb.sum(root.get(EntityVacciniConsegne_.DOSES_DELIVERED))
			)
		.where(
				cb.between(root.get(EntityVacciniConsegne_.ID).get(EntityVacciniConsegnePk_.DATE), start, end)
			)
		.groupBy(
				root.get(EntityVacciniConsegne_.ID).get(EntityVacciniConsegnePk_.REGION_CODE),
				root.get(EntityVacciniConsegne_.ID).get(EntityVacciniConsegnePk_.DATE)
			)
		.orderBy(
				cb.asc(
					root.get(EntityVacciniConsegne_.ID).get(EntityVacciniConsegnePk_.DATE)
				)
			);

		// @formatter:on

		TypedQuery<VacciniConsegne> tq = em.createQuery(cq);
		return tq.getResultList();
	}

	@Override
	public void addMissingRowsForNoDeliveryDays() {
		_LOGGER.debug("Adding mepty rows");

		String tableName = "vaccini_consegne";

		// @formatter:off
		List<StringBuilder> sqls = new ArrayList<>();
		sqls.add(new StringBuilder(String.format("create table temp_dates as select date_data from %s group by date_data with data", tableName)));
		sqls.add(new StringBuilder(String.format("create table temp_regions as select region_code from %s group by region_code with data", tableName)));
		sqls.add(new StringBuilder(String.format("create table temp_supplier as select supplier from %s group by supplier with data", tableName)));
		sqls.add(new StringBuilder("create table cartesian as select * from temp_dates , temp_regions , temp_supplier order by date_data, region_code, supplier with data"));

		StringBuilder sql = new StringBuilder();
		sql.append("create table filldata as ");
		sql.append("select a.region_code, a.date_data, a.supplier, b.doses_delivered ");
		sql.append(String.format("from cartesian as a left join %s as b ", tableName));
		sql.append(" on a.date_data = b.date_data and a.region_code = b.region_code and a.supplier = b.supplier ");
		sql.append("with data");
		sqls.add(sql);

		sqls.add(new StringBuilder("update filldata set doses_delivered = 0 where doses_delivered is null"));
		sqls.add(new StringBuilder(String.format("truncate %s", tableName)));
		sqls.add(new StringBuilder(String.format("insert into %s select * from filldata", tableName)));
		sqls.add(new StringBuilder("drop table temp_dates"));
		sqls.add(new StringBuilder("drop table temp_regions"));
		sqls.add(new StringBuilder("drop table temp_supplier"));
		sqls.add(new StringBuilder("drop table cartesian"));
		sqls.add(new StringBuilder("drop table filldata"));
		// @formatter:on

		sqls.forEach(sb -> {
			Query query = em.createNativeQuery(sb.toString());
			query.executeUpdate();
		});
	}

	@Override
	public List<VeccinesDeliveredPerSupplier> getDeliveredVaccinesPerSupplierBetween(LocalDate start, LocalDate end) {
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<VeccinesDeliveredPerSupplier> cq = cb.createQuery(VeccinesDeliveredPerSupplier.class);
		Root<EntityVacciniConsegne> root = cq.from(EntityVacciniConsegne.class);

		/*
		 * SELECT supplier, sum(doses_delivered) as delivered FROM vaccini_consegne
		 * group by supplier order by supplier
		 */
		// @formatter:off
		cq.multiselect(
				root.get(EntityVacciniConsegne_.ID).get(EntityVacciniConsegnePk_.SUPPLIER),
				cb.sum(root.get(EntityVacciniConsegne_.DOSES_DELIVERED))
			)
		.where(
				cb.between(root.get(EntityVacciniConsegne_.ID).get(EntityVacciniConsegnePk_.DATE), start, end)
			)
		.groupBy(
				root.get(EntityVacciniConsegne_.ID).get(EntityVacciniConsegnePk_.SUPPLIER),
				root.get(EntityVacciniConsegne_.ID).get(EntityVacciniConsegnePk_.DATE)
			)
		.orderBy(cb.asc(root.get(EntityVacciniConsegne_.ID).get(EntityVacciniConsegnePk_.SUPPLIER)));
		// @formatter:on

		TypedQuery<VeccinesDeliveredPerSupplier> tq = em.createQuery(cq);
		return tq.getResultList();
	}

	@Override
	public LocalDate getDataAvailableLastDate() {
		/*
		 * SELECT MAX(DATE_DATA) AS DATE_DATA FROM VACCINI_CONSEGNE
		 */

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<LocalDate> cq = cb.createQuery(LocalDate.class);
		Root<EntityVacciniConsegne> root = cq.from(EntityVacciniConsegne.class);

		cq.select(cb.greatest(root.get(EntityVacciniConsegne_.ID).<LocalDate>get(EntityVacciniConsegnePk_.DATE)));
		TypedQuery<LocalDate> tq = em.createQuery(cq);
		return tq.getSingleResult();
	}

	@Override
	public Long getTotalNumberDeliveedVaccines() {
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<EntityVacciniConsegne> root = cq.from(EntityVacciniConsegne.class);

		/*
		 * SELECT sum(doses_delivered) FROM vaccini_consegne
		 */
		// @formatter:off
		cq.multiselect(
				cb.sum(root.get(EntityVacciniConsegne_.DOSES_DELIVERED))
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
	public List<TotalVaccineDeliveredPerRegion> getTotalVaccineDeliveredPerRegion() {
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<TotalVaccineDeliveredPerRegion> cq = cb.createQuery(TotalVaccineDeliveredPerRegion.class);
		Root<EntityVacciniConsegne> root = cq.from(EntityVacciniConsegne.class);

		/*
		 * SELECT region_code, sum(doses_delivered) as doses_delivered FROM
		 * vaccini_consegne group by region_code order by region_code
		 */
		// @formatter:off
		cq.multiselect(
				root.get(EntityVacciniConsegne_.ID).get(EntityVacciniConsegnePk_.REGION_CODE),
				cb.sum(root.get(EntityVacciniConsegne_.DOSES_DELIVERED))
			)
		.groupBy(
				root.get(EntityVacciniConsegne_.ID).get(EntityVacciniConsegnePk_.REGION_CODE)
			)
		.orderBy(cb.asc(root.get(EntityVacciniConsegne_.ID).get(EntityVacciniConsegnePk_.REGION_CODE)));
		// @formatter:on

		TypedQuery<TotalVaccineDeliveredPerRegion> tq = em.createQuery(cq);
		return tq.getResultList();
	}
}
