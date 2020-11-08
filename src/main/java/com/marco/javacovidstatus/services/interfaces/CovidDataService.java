package com.marco.javacovidstatus.services.interfaces;

import java.time.LocalDate;
import java.util.List;

import com.marco.javacovidstatus.model.DailyData;
import com.marco.javacovidstatus.model.ProvinceDailyData;
import com.marco.javacovidstatus.model.Region;

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
    public List<DailyData> getAllDataDescending();

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
    public List<DailyData> getAllDataAscending();

    /**
     * It returns the list of the national data of the specific range in a ascending
     * order
     * 
     * @param from
     * @param to
     * @return
     */
    public List<DailyData> getDatesInRangeAscending(LocalDate from, LocalDate to);

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
    public boolean storeData(DailyData dto);

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
}
