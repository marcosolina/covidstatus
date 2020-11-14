package com.marco.javacovidstatus.services.implementations;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.marco.javacovidstatus.services.interfaces.CovidDataDownloader;
import com.marco.javacovidstatus.services.interfaces.CovidScheduler;
import com.marco.javacovidstatus.services.interfaces.NotificationSenderInterface;

/**
 * This is my implementation of the Covid Scheduler
 * 
 * @author Marco
 *
 */
@Component
@EnableScheduling
public class MarcoCovidScheduler implements CovidScheduler {
    private static final Logger logger = LoggerFactory.getLogger(MarcoCovidScheduler.class);

    @Autowired
    private NotificationSenderInterface notificationService;

    @Autowired
    @Qualifier("National")
    private CovidDataDownloader nationalDownloader;
    
    @Autowired
    @Qualifier("Region")
    private CovidDataDownloader regionDownloader;
    
    @Autowired
    @Qualifier("Province")
    private CovidDataDownloader provinceDownloader;

    @Scheduled(cron = "0 0 * * * *")
    @Override
    public synchronized void downloadNewData() {

        logger.info("Updating Data");
        List<CovidDataDownloader> downloaders = new ArrayList<>();
        downloaders.add(nationalDownloader);
        downloaders.add(regionDownloader);
        downloaders.add(provinceDownloader);
        
        downloaders.parallelStream().forEach(CovidDataDownloader::downloadData);

        notificationService.sendMessage("marcosolina@gmail.com", "Marco Solina - Covid Status", "Dati aggiornati");
    }

}
