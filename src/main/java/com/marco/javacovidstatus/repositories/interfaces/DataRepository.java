package com.marco.javacovidstatus.repositories.interfaces;

import java.time.LocalDate;
import java.util.List;

import com.marco.javacovidstatus.model.entitites.EntityRegionCode;
import com.marco.javacovidstatus.model.entitites.infections.EntityProvinceData;
import com.marco.javacovidstatus.model.entitites.infections.EntityRegionalData;

/**
 * This interface defines more complicated actions which might not be possible
 * to define with a standard Spring CRUD interface
 * 
 * @author Marco
 *
 */
public interface DataRepository {

    /**
     * Returns the list of the region
     * 
     * @return
     */
    public List<EntityRegionCode> getRegionListOrderedByDescription();

    /**
     * Returns the province data for the specified region
     * 
     * @param from
     * @param to
     * @param regionCode
     * @return
     */
    public List<EntityProvinceData> getProvinceDataBetweenDatesOrderByDateAscending(LocalDate from, LocalDate to,
            String regionCode);

    /**
     * Returns the list of province of the specified region
     * 
     * @param regionCode
     * @return
     */
    public List<String> getProvincesListForRegion(String regionCode);

    /**
     * Returns the last available date of the download data
     * 
     * @return
     */
    public LocalDate getLastDateAvailableForNationalData();

    /**
     * It returns the last date for the available regions data
     * 
     * @return
     */
    public LocalDate getLastDateAvailableFroRegionalData();

    /**
     * It returns the last date for the available provinces data
     * 
     * @return
     */
    public LocalDate getLastDateAvailableForProvincesData();

    /**
     * Returns the Regional data between the from date and to data
     * 
     * @param from
     * @param to
     * @return
     */
    public List<EntityRegionalData> getRegionalDataListOrderedByDateAsc(LocalDate from, LocalDate to);
}
