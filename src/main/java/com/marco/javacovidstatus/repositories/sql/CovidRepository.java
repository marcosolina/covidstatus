package com.marco.javacovidstatus.repositories.sql;

import java.time.LocalDate;
import java.util.List;

import com.marco.javacovidstatus.model.Region;
import com.marco.javacovidstatus.repositories.model.EntityProvinceData;

public interface CovidRepository {

    public List<Region> getRegionList();
    
    public List<EntityProvinceData> getProvinceDataBetweenDatesOrderByDateAscending(LocalDate from, LocalDate to, String regionCode);
    
    public List<String> getProvincesForRegion(String regionCode);
}
