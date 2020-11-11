package com.marco.javacovidstatus.repositories.sql;

import java.time.LocalDate;
import java.util.List;

import com.marco.javacovidstatus.model.Region;
import com.marco.javacovidstatus.repositories.model.EntityProvinceData;
import com.marco.javacovidstatus.repositories.model.EntityRegionalData;

public interface CovidRepository {

    /**
     * Returns the list of the region
     * @return
     */
    public List<Region> getRegionList();
    
    /**
     * Returns the province data for the specified region
     * @param from
     * @param to
     * @param regionCode
     * @return
     */
    public List<EntityProvinceData> getProvinceDataBetweenDatesOrderByDateAscending(LocalDate from, LocalDate to, String regionCode);
    
    /**
     * Returns the list of province of the specified region
     * @param regionCode
     * @return
     */
    public List<String> getProvincesForRegion(String regionCode);
    
    /**
     * Returns the last date of the avilable data
     * @return
     */
    public LocalDate getMaxDate();
    
    /**
     * Returns the Regional data between the from date and to data
     * @param from
     * @param to
     * @return
     */
    public List<EntityRegionalData> getRegionalDataAscending(LocalDate from, LocalDate to);
}
