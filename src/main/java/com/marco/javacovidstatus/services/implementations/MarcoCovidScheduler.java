package com.marco.javacovidstatus.services.implementations;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.marco.javacovidstatus.enums.PopulationSource;
import com.marco.javacovidstatus.services.interfaces.CovidScheduler;
import com.marco.javacovidstatus.services.interfaces.NotificationSenderInterface;
import com.marco.javacovidstatus.services.interfaces.downloaders.CovidDataDownloader;

/**
 * This is my implementation of the {@link CovidScheduler}
 * 
 * @author Marco
 *
 */
@Component
@EnableScheduling
public class MarcoCovidScheduler implements CovidScheduler {
    private static final Logger logger = LoggerFactory.getLogger(MarcoCovidScheduler.class);

    @Autowired
    @Qualifier("National")
    private CovidDataDownloader nationalDownloader;

    @Autowired
    @Qualifier("Region")
    private CovidDataDownloader regionDownloader;

    @Autowired
    @Qualifier("Province")
    private CovidDataDownloader provinceDownloader;

    @Autowired
    @Qualifier("GivenVaccines")
    private CovidDataDownloader givenVaccinesDownloader;

    @Autowired
    @Qualifier("DeliveredVaccines")
    private CovidDataDownloader deliveredVaccinesDownloader;

    @Autowired
    @Qualifier("IstatPopulation")
    private CovidDataDownloader istatDownloader;

    @Autowired
    @Qualifier("CsvPopulation")
    private CovidDataDownloader csvDownloader;
    
    @Autowired
    @Qualifier("GovernmentPopulation")
    private CovidDataDownloader govDownloader;
    
    @Autowired
    @Qualifier("AdditionalDosePopulation")
    private CovidDataDownloader additionalDoseDownloader;
    
    @Autowired
    @Qualifier("BoosterDosePopulation")
    private CovidDataDownloader boosterDoseDownloader;
    
    @Value("${covidstatus.populationdownloader.implementation}")
    private PopulationSource populationDownloader;

    @Autowired
    private NotificationSenderInterface notificationService;

    @Scheduled(cron = "${covidstatus.scheduled.downloadnewdata.cron:0 0 * * * *}") // if not specified it will be every
                                                                                   // hour
    @Override
    public synchronized void downloadNewData() {

        logger.info("Updating Covid Data");

        try {
            List<CovidDataDownloader> downloaders = new ArrayList<>();
            downloaders.add(additionalDoseDownloader);
            downloaders.add(boosterDoseDownloader);
            downloaders.add(nationalDownloader);
            downloaders.add(regionDownloader);
            downloaders.add(provinceDownloader);
            downloaders.add(givenVaccinesDownloader);
            downloaders.add(deliveredVaccinesDownloader);
            downloaders.parallelStream().forEach(CovidDataDownloader::downloadData);
        } catch (Exception e) {
            logger.error(e.getMessage());
            notificationService.sendEmailMessage("marcosolina@gmail.com", "Marco Solina - Covid Status", e.getMessage());
        }

        logger.info("Update Covid data complete");
    }

    @Scheduled(cron = "${covidstatus.scheduled.downloadnewdata.cron.istat:0 0 0 * * *}")
    @Override
    public synchronized void downloadIstatData() {
        logger.info("Updating Population Data");

        try {
        	if(populationDownloader == PopulationSource.GOVERNMENT) {
        		govDownloader.downloadData();
        	} else if (!istatDownloader.downloadData()) {
                notificationService.sendEmailMessage("marcosolina@gmail.com", "Marco Solina - Covid Status", "No dati dall'istat");
                csvDownloader.downloadData();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            notificationService.sendEmailMessage("marcosolina@gmail.com", "Marco Solina - Covid Status", e.getMessage());
        }

        logger.info("Update Population complete");

    }

}
