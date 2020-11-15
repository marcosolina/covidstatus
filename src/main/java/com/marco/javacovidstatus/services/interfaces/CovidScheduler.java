package com.marco.javacovidstatus.services.interfaces;

/**
 * This interface defines some actions than needs to be scheduled
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
