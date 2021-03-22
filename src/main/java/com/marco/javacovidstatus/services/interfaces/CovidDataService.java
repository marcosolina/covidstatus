package com.marco.javacovidstatus.services.interfaces;

import java.time.LocalDate;
import java.util.List;

import com.marco.javacovidstatus.model.dto.NationalDailyDataDto;
import com.marco.javacovidstatus.model.dto.ProvinceDailyDataDto;
import com.marco.javacovidstatus.model.dto.RegionDto;
import com.marco.javacovidstatus.model.dto.RegionalDailyDataDto;
import com.marco.utils.MarcoException;

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
    public List<NationalDailyDataDto> getAllDataDescending();

    /**
     * It returns a list of regions
     * 
     * @return
     */
    public List<RegionDto> getRegionsList();

    /**
     * It returns the list of the national data in an ascending order
     * 
     * @return
     */
    public List<NationalDailyDataDto> getAllDataAscending();

    /**
     * It returns the list of the national data of the specific range in a ascending
     * order
     * 
     * @param from
     * @param to
     * @return
     */
    public List<NationalDailyDataDto> getDatesInRangeAscending(LocalDate from, LocalDate to) throws MarcoException;

    /**
     * It returns the regional data between the provided dates in ascending order
     * 
     * @param from
     * @param to
     * @return
     */
    public List<RegionalDailyDataDto> getRegionalDatesInRangeAscending(LocalDate from, LocalDate to) throws MarcoException;

    /**
     * It stores the {@link RegionalDailyDataDto}
     * 
     * @param data
     * @return
     */
    public boolean saveRegionalDailyData(RegionalDailyDataDto data);

    /**
     * It returns the data of the provinces for the selected region
     * 
     * @param from
     * @param to
     * @return
     */
    public List<ProvinceDailyDataDto> getProvinceDataInRangeAscending(LocalDate from, LocalDate to, String regionCode) throws MarcoException;

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
    public boolean storeData(NationalDailyDataDto dto);

    /**
     * It deletes all the data
     * 
     * @return
     */
    public boolean deleteAllData();

    /**
     * It stores the {@link ProvinceDailyDataDto}
     * 
     * @param data
     * @return
     */
    public boolean storeProvinceDailyData(ProvinceDailyDataDto data);

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
