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

import com.marco.javacovidstatus.model.entitites.EntityRegionCode;
import com.marco.javacovidstatus.model.entitites.EntityRegionCode_;
import com.marco.javacovidstatus.model.entitites.infections.EntityNationalData;
import com.marco.javacovidstatus.model.entitites.infections.EntityNationalData_;
import com.marco.javacovidstatus.model.entitites.infections.EntityProvinceData;
import com.marco.javacovidstatus.model.entitites.infections.EntityProvinceDataPk_;
import com.marco.javacovidstatus.model.entitites.infections.EntityProvinceData_;
import com.marco.javacovidstatus.model.entitites.infections.EntityRegionalData;
import com.marco.javacovidstatus.model.entitites.infections.EntityRegionalDataPk_;
import com.marco.javacovidstatus.model.entitites.infections.EntityRegionalData_;
import com.marco.javacovidstatus.repositories.interfaces.DataRepository;

/**
 * Postgres + JPA implementation of the DataRepository Interface
 * 
 * @author Marco
 *
 */
@Transactional
public class DataRepositoryImpPostgres implements DataRepository {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<EntityRegionCode> getRegionListOrderedByDescription() {
        /*
         * Thanks to: https://wiki.eclipse.org/EclipseLink/UserGuide/JPA/Basic_JPA_Development/Querying/Criteria#Constructors
         * 
         * SELECT REGION_CODE, DESCR FROM CODICI_REGIONI ORDER BY DESCR
         */
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EntityRegionCode> cq = cb.createQuery(EntityRegionCode.class);
        Root<EntityRegionCode> root = cq.from(EntityRegionCode.class);
        // @formatter:off
        cq.select(root)
            .orderBy(
                cb.asc(root.get(EntityRegionCode_.DESC))
            );
        // @formatter:on
        TypedQuery<EntityRegionCode> tq = em.createQuery(cq);
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
    public List<String> getProvincesListForRegion(String regionCode) {

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
    public LocalDate getLastDateAvailableForNationalData() {
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
    public LocalDate getLastDateAvailableFroRegionalData() {
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
    public LocalDate getLastDateAvailableForProvincesData() {
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
    public List<EntityRegionalData> getRegionalDataListOrderedByDateAsc(LocalDate from, LocalDate to) {
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
