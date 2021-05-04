package com.marco.javacovidstatus.services.implementations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.util.ResourceUtils;
import org.springframework.web.reactive.function.client.WebClient;

import com.marco.javacovidstatus.enums.Gender;
import com.marco.javacovidstatus.model.dto.PopulationDto;
import com.marco.javacovidstatus.services.interfaces.CovidDataDownloader;
import com.marco.javacovidstatus.services.interfaces.NotificationSenderInterface;
import com.marco.javacovidstatus.services.interfaces.PopulationDataService;
import com.marco.utils.MarcoException;

public class PopulationDownloaderExcel extends CovidDataDownloader {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(PopulationDownloaderExcel.class);

    @Autowired
    private PopulationDataService service;
    @Autowired
    private NotificationSenderInterface notificationService;
    
    public PopulationDownloaderExcel(WebClient webClient) {
        super(webClient);
    }

    @Override
    public boolean downloadData() {
        
        AtomicBoolean error = new AtomicBoolean();
        try{
            List<String> rows = new ArrayList<>();
            String line = null;
            while((line = bf.readLine()) != null) {
                rows.add(line);
            }
            
            rows.remove(0);
            
            for (int j = 0; j < rows.size(); j++) {
                String row = rows.get(j);
                String[] cols = row.split(";");
                PopulationDto male = new PopulationDto();
                male.setAge(Integer.parseInt(cols[0]));
                male.setCounter(Integer.parseInt(cols[1]));
                male.setYear(2020);
                male.setGender(Gender.MEN);
                
                PopulationDto female = new PopulationDto();
                female.setAge(Integer.parseInt(cols[0]));
                female.setCounter(Integer.parseInt(cols[2]));
                female.setYear(2020);
                female.setGender(Gender.WOMEN);
                try {
                    service.storeNewPopulationDto(male);
                    service.storeNewPopulationDto(female);
                } catch (MarcoException e) {
                    error.set(true);
                    e.printStackTrace();
                }
            }
            
            
        } catch (IOException e) {
            error.set(true);
            e.printStackTrace();
        }
        
        if (error.get()) {
            String message = "There was an error while downloading the ISTAT data"; 
            _LOGGER.error(message);
            notificationService.sendEmailMessage("marcosolina@gmail.com", "Marco Solina - Covid Status", message);
        }else {
            _LOGGER.info("Downloading ISTAT data complete");
        }
        return !error.get();
    }

    @Override
    public LocalDate getStartDate() {
        return null;
    }

}
