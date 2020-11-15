package com.marco.javacovidstatus.repositories.implementations;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.transaction.annotation.Transactional;

import com.marco.javacovidstatus.model.dto.Region;
import com.marco.javacovidstatus.model.entitites.EntityProvinceData;
import com.marco.javacovidstatus.model.entitites.EntityRegionalData;
import com.marco.javacovidstatus.repositories.interfaces.CovidRepository;

@Transactional
public class MarcoCovidRepository implements CovidRepository {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Region> getRegionList() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("p.id.regionCode ,");
        sql.append("p.regionDesc ");
        sql.append(" FROM EntityProvinceData p ");
        sql.append(" GROUP BY ");
        sql.append("p.id.regionCode ,");
        sql.append("p.regionDesc ");
        sql.append(" ORDER BY ");
        sql.append("p.regionDesc ");

        @SuppressWarnings("unchecked")
        List<Object[]> results = em.createQuery(sql.toString()).getResultList();

        // @formatter:off
        return results.stream()//Stream the array of objects
                .map(arr -> new Region(String.class.cast(arr[0]), String.class.cast(arr[1])))//convert the object[] into a Region object
                .collect(Collectors.toList());//put everything into a list
        // @formatter:on
    }

    @Override
    public List<EntityProvinceData> getProvinceDataBetweenDatesOrderByDateAscending(LocalDate from, LocalDate to,
            String regionCode) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<EntityProvinceData> cq = cb.createQuery(EntityProvinceData.class);
        Root<EntityProvinceData> root = cq.from(EntityProvinceData.class);
        // @formatter:off
        //SELECT * FROM .....
        cq.select(root).where(
                cb.and(
                    cb.between(root.get("id").get("date"), from, to),
                    cb.equal(root.get("id").get("regionCode"), regionCode) 
                    )
                )
            .orderBy(
                cb.asc(root.get("id").get("date"))
            );
        // @formatter:on

        TypedQuery<EntityProvinceData> tq = em.createQuery(cq);
        return tq.getResultList();
    }

    @Override
    public List<String> getProvincesForRegion(String regionCode) {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<EntityProvinceData> root = cq.from(EntityProvinceData.class);
        // @formatter:off
        //SELECT proviceCode FROM .....
        cq.multiselect(root.get("id").get("provinceCode"))
            .where(
                cb.and(
                    cb.equal(root.get("id").get("regionCode"), regionCode) 
                    )
                )
            .groupBy(root.get("id").get("provinceCode"));
        // @formatter:on

        return em.createQuery(cq).getResultList();
    }

    @Override
    public LocalDate getNationalMaxDateAvailable() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append(" MAX(p.date) ");
        sql.append(" FROM EntityNationalData p ");

        Object obj = em.createQuery(sql.toString()).getSingleResult();
        if (obj != null) {
            return LocalDate.class.cast(obj);
        }
        return null;
    }

    @Override
    public LocalDate getRegionMaxDateAvailable() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append(" MAX(p.id.date) ");
        sql.append(" FROM EntityRegionalData p ");

        Object obj = em.createQuery(sql.toString()).getSingleResult();
        if (obj != null) {
            return LocalDate.class.cast(obj);
        }
        return null;
    }

    @Override
    public LocalDate getProvinceMaxDateAvailable() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append(" MAX(p.id.date) ");
        sql.append(" FROM EntityProvinceData p ");

        Object obj = em.createQuery(sql.toString()).getSingleResult();
        if (obj != null) {
            return LocalDate.class.cast(obj);
        }
        return null;
    }

    @Override
    public List<EntityRegionalData> getRegionalDataAscending(LocalDate from, LocalDate to) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<EntityRegionalData> cq = cb.createQuery(EntityRegionalData.class);
        Root<EntityRegionalData> root = cq.from(EntityRegionalData.class);
        // @formatter:off
        //SELECT * FROM .....
        cq.select(root).where(
            cb.between(root.get("id").get("date"), from, to)
            ).orderBy(
                cb.asc(root.get("id").get("date"))
            );
        // @formatter:on

        TypedQuery<EntityRegionalData> tq = em.createQuery(cq);
        return tq.getResultList();
    }

}
