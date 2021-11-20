package com.marco.javacovidstatus.services.implementations.downloaders.population;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;

import com.marco.javacovidstatus.model.dto.BoosterPopulationDto;
import com.marco.javacovidstatus.services.interfaces.BoosterPopulationService;
import com.marco.javacovidstatus.services.interfaces.CovidDataService;
import com.marco.javacovidstatus.services.interfaces.NotificationSenderInterface;
import com.marco.javacovidstatus.services.interfaces.downloaders.CovidDataDownloader;
import com.marco.utils.MarcoException;

public class PopulationBoosterDoseDownloader extends CovidDataDownloader {

    private static final Logger _LOGGER = LoggerFactory.getLogger(PopulationBoosterDoseDownloader.class);
    private static final int COL_COUNT = 3;
    private static final int COL_AGE = 2;
    private static final int AREA = 0;

    private String url = "https://raw.githubusercontent.com/italia/covid19-opendata-vaccini/master/dati/platea-dose-booster.csv";
    
    @Autowired
    private NotificationSenderInterface notificationService;
    @Autowired
    private CovidDataService covidDataService;
    @Autowired
    private BoosterPopulationService service;
    
    public PopulationBoosterDoseDownloader(WebClient webClient) {
        super(webClient);
    }

    @Override
    public boolean downloadData() {
        _LOGGER.info("PopulationBoosterDoseDownloader downloading booster data");
        
        AtomicBoolean error = new AtomicBoolean();

        try {
            service.deleteAll();

            List<String> listRows = getCsvRows(url);

            Map<String, String> regionsCodes = new HashMap<>();

            covidDataService.getRegionsListOrderedByDescription().forEach(r -> regionsCodes.put(r.getArea(), r.getCode()));

            listRows.stream().forEach(csvRow -> {

                String[] cols = csvRow.split(",");

                String age = this.getCategory(cols[COL_AGE]);
                if(age == null) {
                    return;
                }
                
                BoosterPopulationDto dto = new BoosterPopulationDto();
                dto.setRegionCode(regionsCodes.get(cols[AREA]));
                dto.setAge(Integer.parseInt(age));
                dto.setCounter(Integer.parseInt(cols[COL_COUNT]));

                try {
                    service.insert(dto);
                } catch (MarcoException e) {
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            if (_LOGGER.isTraceEnabled()) {
                e.printStackTrace();
            }
            error.set(true);
            _LOGGER.error(e.getMessage());
        }

        if (error.get()) {
            String message = "There was an error while downloading the Booster Population data";
            _LOGGER.error(message);
            notificationService.sendEmailMessage("marcosolina@gmail.com", "Marco Solina - Covid Status", message);
        } else {
            _LOGGER.info("Downloading GOVERNMENT Booster data complete");
        }

        return !error.get();

    }
    
    private String getCategory(String cvsString){
        final String opt1 = "Operatori Sanitari e Sociosanitari";
        final String opt2 = "Ospiti Strutture Residenziali";
        final String opt3 = "Over 80";
        
        switch(cvsString) {
            case opt1:
            case opt2:
                return null;
            case opt3:
                return "80";
        }
        
        return cvsString.substring(0, 2);
    }

    @Override
    public LocalDate getStartDate() {
        // TODO Auto-generated method stub
        return null;
    }

}
