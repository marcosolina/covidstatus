package com.marco.javacovidstatus.services.interfaces;

/**
 * This interface defines some actions than needs to be scheduled
 * 
 * @author Marco
 *
 */
public interface CovidScheduler {

    /**
     * It downloads new data related to the Covid situation
     */
    public void downloadNewData();

    /**
     * It donwload the data from the ISTAT agency
     */
    public void downloadIstatData();
    
    /**
     * It indicates whether the app is currently checking for new
     * data or not. 
     * @return
     */
    public boolean isRefreshing();
    
    /**
     * It returns the time when the last successful refresh data occurred
     * @return
     */
    public String getLastUpdateTime();
}
