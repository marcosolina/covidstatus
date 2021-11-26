package com.marco.javacovidstatus.services.implementations;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private static final AtomicBoolean refreshVirusDataInProgress = new AtomicBoolean(false);
    private static final AtomicBoolean refreshPopulationDataInProgress = new AtomicBoolean(false);
    private static final ZoneId zoneId = ZoneId.of("Europe/Rome");
    private static LocalDateTime lastUpdate = null;

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
    
    @Value("${covidstatus.populationdownloader.implementation}")
    private PopulationSource populationDownloader;

    @Autowired
    private NotificationSenderInterface notificationService;

    @Scheduled(cron = "${covidstatus.scheduled.downloadnewdata.cron:0 0 * * * *}") // if not specified it will be every
                                                                                   // hour
    @Override
    public synchronized void downloadNewData() {

        logger.info("Updating Covid Data");
        refreshVirusDataInProgress.set(true);
        try {
            List<CovidDataDownloader> downloaders = new ArrayList<>();
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
        
        lastUpdate = LocalDateTime.now(zoneId);
        
        refreshVirusDataInProgress.set(false);
        logger.info("Update Covid data complete");
    }

    @Scheduled(cron = "${covidstatus.scheduled.downloadnewdata.cron.istat:0 0 0 * * *}")
    @Override
    public synchronized void downloadIstatData() {
        logger.info("Updating Population Data");
        refreshPopulationDataInProgress.set(true);
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
        refreshPopulationDataInProgress.set(false);
    }

    @Override
    public boolean isRefreshing() {
        return refreshPopulationDataInProgress.get() || refreshVirusDataInProgress.get();
    }

    @Override
    public String getLastUpdateTime() {
        if(lastUpdate == null) {
            return "Mai aggiornato";
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dtf.format(lastUpdate);
    }

}
