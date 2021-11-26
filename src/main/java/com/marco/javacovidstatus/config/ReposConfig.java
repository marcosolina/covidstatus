package com.marco.javacovidstatus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.marco.javacovidstatus.repositories.implementations.DataRepositoryImpPostgres;
import com.marco.javacovidstatus.repositories.implementations.PopulationRepoImpPostgres;
import com.marco.javacovidstatus.repositories.implementations.VaccinesGivenRepoImpPostgres;
import com.marco.javacovidstatus.repositories.implementations.VeccinesDeliveredRepoImpPostgres;
import com.marco.javacovidstatus.repositories.interfaces.DataRepository;
import com.marco.javacovidstatus.repositories.interfaces.PopulationRepo;
import com.marco.javacovidstatus.repositories.interfaces.VaccinesGivenRepo;
import com.marco.javacovidstatus.repositories.interfaces.VeccinesDeliveredRepo;

@Configuration
public class ReposConfig {
    
    @Bean
    public DataRepository getCovidRepository() {
        return new DataRepositoryImpPostgres();
    }

    @Bean
    public VeccinesDeliveredRepo getVeccinesDeliveredRepo() {
        return new VeccinesDeliveredRepoImpPostgres();
    }

    @Bean
    public VaccinesGivenRepo getGivenVaccinesRepo() {
        return new VaccinesGivenRepoImpPostgres();
    }

    @Bean
    public PopulationRepo getPopulationRepo() {
        return new PopulationRepoImpPostgres();
    }
}
