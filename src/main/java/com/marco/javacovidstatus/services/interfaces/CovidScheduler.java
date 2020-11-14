package com.marco.javacovidstatus.services.interfaces;

/**
 * This interface defines the required functionalities to retrieve the national
 * data from the Government
 * 
 * @author Marco
 *
 */
public interface CovidScheduler {

    /**
     * It downloads new data
     */
    public void downloadNewData();
}
