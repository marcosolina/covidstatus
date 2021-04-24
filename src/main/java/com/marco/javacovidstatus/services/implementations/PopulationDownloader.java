package com.marco.javacovidstatus.services.implementations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;

import com.marco.javacovidstatus.enums.Gender;
import com.marco.javacovidstatus.model.dto.PopulationDto;
import com.marco.javacovidstatus.services.interfaces.CovidDataDownloader;
import com.marco.javacovidstatus.services.interfaces.NotificationSenderInterface;
import com.marco.javacovidstatus.services.interfaces.PopulationDataService;
import com.marco.utils.MarcoException;

/**
 * This class will download the details about the Italian population. The data
 * are retrieved using the ISTA web services
 * 
 * @author Marco
 *
 */
public class PopulationDownloader extends CovidDataDownloader {
    private static final Logger _LOGGER = LoggerFactory.getLogger(PopulationDownloader.class);
    private static final int COL_GENDER = 4;
    private static final int COL_YEAR = 7;
    private static final int COL_COUNT = 8;

    @Autowired
    private PopulationDataService service;
    @Autowired
    private NotificationSenderInterface notificationService;

    public PopulationDownloader(WebClient webClient) {
        super(webClient);
    }

    @Override
    public void downloadData() {
        _LOGGER.info("Downloading ISTAT data");
        
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/vnd.sdmx.data+csv;version=1.0.0");
        AtomicBoolean error = new AtomicBoolean();

        try {
            service.deleteAll();
            List<Integer> years = new ArrayList<>();
            for (int i = 0; i < 99; i++) {
                years.add(i);
            }
            
            years.parallelStream().forEach(i -> {
                String url = String.format("http://sdmx.istat.it/SDMXWS/rest/data/22_289/.Y%d.IT.1+2.99.JAN/", i);
                _LOGGER.debug(String.format("Downloading ISTAT: %s", url));
                List<String> rows = getCsvRows(url, headers);
                for (int j = 0; j < rows.size(); j++) {
                    String row = rows.get(j);
                    String[] cols = row.split(",");
                    PopulationDto dto = new PopulationDto();
                    dto.setAge(i);
                    dto.setCounter(Integer.parseInt(cols[COL_COUNT]));
                    dto.setYear(Integer.parseInt(cols[COL_YEAR]));
                    dto.setGender(Gender.fromInt(Integer.parseInt(cols[COL_GENDER])));
                    try {
                        service.storeNewPopulationDto(dto);
                    } catch (MarcoException e) {
                        error.set(true);
                        e.printStackTrace();
                    }
                }
            });
            
            String url = "http://sdmx.istat.it/SDMXWS/rest/data/22_289/.Y_GE100.IT.1+2.99.JAN/";
            _LOGGER.debug(String.format("Downloading ISTAT: %s", url));
            List<String> rows = getCsvRows(url, headers);
            for (int j = 0; j < rows.size(); j++) {
                String row = rows.get(j);
                String[] cols = row.split(",");
                PopulationDto dto = new PopulationDto();
                dto.setAge(100);
                dto.setCounter(Integer.parseInt(cols[COL_COUNT]));
                dto.setYear(Integer.parseInt(cols[COL_YEAR]));
                dto.setGender(Gender.fromInt(Integer.parseInt(cols[COL_GENDER])));
                service.storeNewPopulationDto(dto);
            }
            
        } catch (MarcoException e) {
            if(_LOGGER.isTraceEnabled()) {
                e.printStackTrace();
            }
            error.set(true);
            _LOGGER.error(e.getMessage());
        }
        
        if (error.get()) {
            String message = "There was an error while downloading the ISTAT data"; 
            _LOGGER.error(message);
            notificationService.sendEmailMessage("marcosolina@gmail.com", "Marco Solina - Covid Status", message);
        }else {
            _LOGGER.info("Downloading ISTAT data complete");
        }
    }

    @Override
    public LocalDate getStartDate() {
        return null;
    }

}
