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
}
