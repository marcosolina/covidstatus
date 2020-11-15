package com.marco.javacovidstatus.services.interfaces;

import java.time.LocalDate;
import java.util.List;

import com.marco.javacovidstatus.model.dto.NationalDailyData;
import com.marco.javacovidstatus.model.dto.ProvinceDailyData;
import com.marco.javacovidstatus.model.dto.Region;
import com.marco.javacovidstatus.model.dto.RegionalDailyData;

/**
 * This interfaces provides a list of functionalities that you can use to
 * retrieve the National Data
 * 
 * @author Marco
 *
 */
public interface CovidDataService {

    /**
     * It returns the list of the national data in a descending order
     * 
     * @return
     */
    public List<NationalDailyData> getAllDataDescending();

    /**
     * It returns a list of regions
     * 
     * @return
     */
    public List<Region> getRegionsList();

    /**
     * It returns the list of the national data in an ascending order
     * 
     * @return
     */
    public List<NationalDailyData> getAllDataAscending();

    /**
     * It returns the list of the national data of the specific range in a ascending
     * order
     * 
     * @param from
     * @param to
     * @return
     */
    public List<NationalDailyData> getDatesInRangeAscending(LocalDate from, LocalDate to);

    /**
     * It returns the regional data between the provided dates in ascending order
     * 
     * @param from
     * @param to
     * @return
     */
    public List<RegionalDailyData> getRegionalDatesInRangeAscending(LocalDate from, LocalDate to);

    /**
     * It stores the {@link RegionalDailyData}
     * 
     * @param data
     * @return
     */
    public boolean saveRegionalDailyData(RegionalDailyData data);

    /**
     * It returns the data of the provinces for the selected region
     * 
     * @param from
     * @param to
     * @return
     */
    public List<ProvinceDailyData> getProvinceDataInRangeAscending(LocalDate from, LocalDate to, String regionCode);

    /**
     * Retrieves the list of provinces for the specific region
     * 
     * @param region
     * @return
     */
    public List<String> getProfinceListForRegion(String region);

    /**
     * Store the data
     * 
     * @param dto
     * @return
     */
    public boolean storeData(NationalDailyData dto);

    /**
     * It deletes all the data
     * 
     * @return
     */
    public boolean deleteAllData();

    /**
     * It stores the {@link ProvinceDailyData}
     * 
     * @param data
     * @return
     */
    public boolean storeProvinceDailyData(ProvinceDailyData data);

    /**
     * It returns the last date for the available national data
     * 
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
}
