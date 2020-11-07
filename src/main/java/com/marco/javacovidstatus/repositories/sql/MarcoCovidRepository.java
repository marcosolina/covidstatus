package com.marco.javacovidstatus.repositories.sql;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Transactional;

import com.marco.javacovidstatus.model.Region;

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
        
        return results.stream().map(arr -> new Region(String.class.cast(arr[0]), String.class.cast(arr[1]))).collect(Collectors.toList());
    }

}
