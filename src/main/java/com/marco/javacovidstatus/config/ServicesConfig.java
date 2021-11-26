package com.marco.javacovidstatus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.marco.javacovidstatus.services.implementations.MarcoNationalDataService;
import com.marco.javacovidstatus.services.implementations.PopulationDataServiceMarco;
import com.marco.javacovidstatus.services.implementations.VaccineDataServiceMarco;
import com.marco.javacovidstatus.services.interfaces.CovidDataService;
import com.marco.javacovidstatus.services.interfaces.PopulationDataService;
import com.marco.javacovidstatus.services.interfaces.VaccineDataService;

@Configuration
public class ServicesConfig {
    
    @Bean
    public CovidDataService getNationalDataService() {
        return new MarcoNationalDataService();
    }
    @Bean
    public PopulationDataService getPopulationDataService() {
        return new PopulationDataServiceMarco();
    }

    @Bean
    public VaccineDataService getVaccineDateService() {
        return new VaccineDataServiceMarco();
    }
}
