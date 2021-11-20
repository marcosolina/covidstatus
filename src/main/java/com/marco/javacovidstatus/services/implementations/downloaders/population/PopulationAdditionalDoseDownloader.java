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

import com.marco.javacovidstatus.model.dto.AdditionalDosePopulationDto;
import com.marco.javacovidstatus.services.interfaces.AdditionalDosePopulationService;
import com.marco.javacovidstatus.services.interfaces.CovidDataService;
import com.marco.javacovidstatus.services.interfaces.NotificationSenderInterface;
import com.marco.javacovidstatus.services.interfaces.downloaders.CovidDataDownloader;
import com.marco.utils.MarcoException;

public class PopulationAdditionalDoseDownloader extends CovidDataDownloader {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(PopulationAdditionalDoseDownloader.class);
    private static final int COL_COUNT = 3;
    private static final int AREA = 0;

    private String url = "https://raw.githubusercontent.com/italia/covid19-opendata-vaccini/master/dati/platea-dose-aggiuntiva.csv";
    
    @Autowired
    private NotificationSenderInterface notificationService;
    @Autowired
    private CovidDataService covidDataService;
    @Autowired
    private AdditionalDosePopulationService service;

    public PopulationAdditionalDoseDownloader(WebClient webClient) {
        super(webClient);
    }

    @Override
    public boolean downloadData() {
        _LOGGER.info("PopulationAdditionalDoseDownloader downloading additional dose data");
        
        AtomicBoolean error = new AtomicBoolean();

        try {
            service.deleteAll();

            List<String> listRows = getCsvRows(url);

            Map<String, String> regionsCodes = new HashMap<>();

            covidDataService.getRegionsListOrderedByDescription().forEach(r -> regionsCodes.put(r.getArea(), r.getCode()));

            listRows.stream().forEach(csvRow -> {

                String[] cols = csvRow.split(",");
                
                AdditionalDosePopulationDto dto = new AdditionalDosePopulationDto();
                dto.setRegionCode(regionsCodes.get(cols[AREA]));
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
            String message = "There was an error while downloading the additional dose Population data";
            _LOGGER.error(message);
            notificationService.sendEmailMessage("marcosolina@gmail.com", "Marco Solina - Covid Status", message);
        } else {
            _LOGGER.info("Downloading GOVERNMENT additional dose data complete");
        }

        return !error.get();

    }

    @Override
    public LocalDate getStartDate() {
        return null;
    }

}
