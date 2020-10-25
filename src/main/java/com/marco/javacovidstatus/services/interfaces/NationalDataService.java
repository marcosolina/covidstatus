package com.marco.javacovidstatus.services.interfaces;

import java.util.List;

import com.marco.javacovidstatus.model.DailyData;

/**
 * This interfaces provides a list of functionalities that you can use to
 * retrieve the National Data
 * 
 * @author Marco
 *
 */
public interface NationalDataService {

    /**
     * It returns the list of the national data in a descending order
     * @return
     */
    public List<DailyData> getAllDataDescending();
}
