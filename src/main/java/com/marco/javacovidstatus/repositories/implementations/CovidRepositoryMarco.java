package com.marco.javacovidstatus.repositories.implementations;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.transaction.annotation.Transactional;

import com.marco.javacovidstatus.model.entitites.EntityNationalData;
import com.marco.javacovidstatus.model.entitites.EntityNationalData_;
import com.marco.javacovidstatus.model.entitites.EntityProvinceData;
import com.marco.javacovidstatus.model.entitites.EntityProvinceDataPk_;
import com.marco.javacovidstatus.model.entitites.EntityProvinceData_;
import com.marco.javacovidstatus.model.entitites.EntityRegionalData;
import com.marco.javacovidstatus.model.entitites.EntityRegionalDataPk_;
import com.marco.javacovidstatus.model.entitites.EntityRegionalData_;
import com.marco.javacovidstatus.model.entitites.RegionData;
import com.marco.javacovidstatus.repositories.interfaces.CovidRepository;

/**
 * With this implementation I wanted to provide different examples of how to use
 * JPA
 * 
 * @author Marco
 *
 */
@Transactional
public class CovidRepositoryMarco implements CovidRepository {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<RegionData> getRegionList() {
        /*
         * Thanks to: https://wiki.eclipse.org/EclipseLink/UserGuide/JPA/Basic_JPA_Development/Querying/Criteria#Constructors
         * 
         * SELECT REGION_CODE, REGION_DESC FROM PROVINCE_DATA GROUP BY REGION_CODE, REGION_DESC ORDER BY REGION_DESC
         */
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RegionData> cq = cb.createQuery(RegionData.class);
        Root<EntityProvinceData> root = cq.from(EntityProvinceData.class);
        // @formatter:off
        cq.multiselect(
                root.get(EntityProvinceData_.ID).get(EntityProvinceDataPk_.REGION_CODE), root.get(EntityProvinceData_.REGION_DESC)
            )
            .groupBy(
                root.get(EntityProvinceData_.ID).get(EntityProvinceDataPk_.REGION_CODE), root.get(EntityProvinceData_.REGION_DESC)
            )
            .orderBy(
                cb.asc(root.get(EntityProvinceData_.REGION_DESC))
            );
        // @formatter:on
        TypedQuery<RegionData> tq = em.createQuery(cq);
        return tq.getResultList();
    }

    @Override
    public List<EntityProvinceData> getProvinceDataBetweenDatesOrderByDateAscending(LocalDate from, LocalDate to,
            String regionCode) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<EntityProvinceData> cq = cb.createQuery(EntityProvinceData.class);
        Root<EntityProvinceData> root = cq.from(EntityProvinceData.class);
        
        
        /*
         * SELECT * FROM PROVINCE_DATA WHERE DATE_DATA BETWEEN X AND Y AND REGION_CODE = Z
         * ORDER BY DATE_DATA
         */
        // @formatter:off
        cq.select(root).where(
                cb.and(
                    cb.between(root.get(EntityProvinceData_.ID).get(EntityProvinceDataPk_.DATE), from, to),
                    cb.equal(root.get(EntityProvinceData_.ID).get(EntityProvinceDataPk_.REGION_CODE), regionCode) 
                    )
                )
            .orderBy(
                cb.asc(root.get(EntityProvinceData_.ID).get(EntityProvinceDataPk_.DATE))
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
        
        /*
         * SELECT PROVINCE_CODE FROM PROVINCE_DATA WHERE REGION_CODE = X GROUP BY PROVINCE_CODE
         */
        // @formatter:off
        cq.multiselect(root.get(EntityProvinceData_.ID).get(EntityProvinceDataPk_.PROVINCE_CODE))
            .where(
                cb.and(
                    cb.equal(root.get(EntityProvinceData_.ID).get(EntityProvinceDataPk_.REGION_CODE), regionCode) 
                    )
                )
            .groupBy(root.get(EntityProvinceData_.ID).get(EntityProvinceDataPk_.PROVINCE_CODE));
        // @formatter:on

        return em.createQuery(cq).getResultList();
    }

    @Override
    public LocalDate getNationalMaxDateAvailable() {
        /*
         * SELECT MAX(DATE_DATA) AS DATE_DATA FROM NATIONAL_DATA
         */
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<LocalDate> cq = cb.createQuery(LocalDate.class);
        Root<EntityNationalData> root = cq.from(EntityNationalData.class);
        
        cq.select(cb.greatest(root.<LocalDate>get(EntityNationalData_.DATE)));
        TypedQuery<LocalDate> tq = em.createQuery(cq);
        return tq.getSingleResult();
    }

    @Override
    public LocalDate getRegionMaxDateAvailable() {
        /*
         * SELECT MAX(DATE_DATA) AS DATE_DATA FROM REGIONAL_DATA
         */
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<LocalDate> cq = cb.createQuery(LocalDate.class);
        Root<EntityRegionalData> root = cq.from(EntityRegionalData.class);
        
        cq.select(cb.greatest(root.get(EntityRegionalData_.ID).<LocalDate>get(EntityRegionalDataPk_.DATE)));
        TypedQuery<LocalDate> tq = em.createQuery(cq);
        return tq.getSingleResult();
    }

    @Override
    public LocalDate getProvinceMaxDateAvailable() {
        /*
         * SELECT MAX(DATE_DATA) AS DATE_DATA FROM PROVINCE_DATA
         */
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<LocalDate> cq = cb.createQuery(LocalDate.class);
        Root<EntityProvinceData> root = cq.from(EntityProvinceData.class);
        
        cq.select(cb.greatest(root.get(EntityProvinceData_.ID).<LocalDate>get(EntityProvinceDataPk_.DATE)));
        TypedQuery<LocalDate> tq = em.createQuery(cq);
        return tq.getSingleResult();
    }

    @Override
    public List<EntityRegionalData> getRegionalDataAscending(LocalDate from, LocalDate to) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<EntityRegionalData> cq = cb.createQuery(EntityRegionalData.class);
        Root<EntityRegionalData> root = cq.from(EntityRegionalData.class);
        
        /*
         * SELECT * FROM  REGIONAL_DATA WHERE DATE_DATA BETWEEN X AND Y ORDER BY DATE_DATA
         */
        // @formatter:off
        cq.select(root).where(
            cb.between(root.get(EntityRegionalData_.ID).get(EntityRegionalDataPk_.DATE), from, to)
            ).orderBy(
                cb.asc(root.get(EntityRegionalData_.ID).get(EntityRegionalDataPk_.DATE))
            );
        // @formatter:on

        TypedQuery<EntityRegionalData> tq = em.createQuery(cq);
        return tq.getResultList();
    }

}
