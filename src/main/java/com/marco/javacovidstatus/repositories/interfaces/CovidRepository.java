package com.marco.javacovidstatus.repositories.interfaces;

import java.time.LocalDate;
import java.util.List;

import com.marco.javacovidstatus.model.dto.Region;
import com.marco.javacovidstatus.model.entitites.EntityProvinceData;
import com.marco.javacovidstatus.model.entitites.EntityRegionalData;

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
     * Returns the last available date of the download data
     * @return
     */
    public LocalDate getNationalMaxDateAvailable();
    
    /**
     * It returns the last date for the available regions data
     * 
     * @return
     */
    public LocalDate getRegionMaxDateAvailable();
    
    /**
     * It returns the last date for the available provinces data
     * 
     * @return
     */
    public LocalDate getProvinceMaxDateAvailable();
    
    /**
     * Returns the Regional data between the from date and to data
     * @param from
     * @param to
     * @return
     */
    public List<EntityRegionalData> getRegionalDataAscending(LocalDate from, LocalDate to);
}
