package com.marco.javacovidstatus.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.reactive.function.client.WebClient;

import com.marco.javacovidstatus.services.implementations.MarcoNationalDataService;
import com.marco.javacovidstatus.services.implementations.NationalDataServiceRasp;
import com.marco.javacovidstatus.services.interfaces.NationalDataService;

@Configuration
public class Beans {
    private static final Logger LOGGER = LoggerFactory.getLogger(Beans.class);

    @Value("${covidstatus.serviceimpl}")
    private String serviceImp;

    @Bean
    public WebClient getWebClient() {
        return WebClient.builder().build();
    }

    @Bean
    public NationalDataService getNationalDataService() {
        if ("rasp".equals(serviceImp)) {
            LOGGER.info("Uso implementazione per il RASP");
            return new NationalDataServiceRasp();
        }
        LOGGER.info("Uso implementazione di default");
        return new MarcoNationalDataService();
    }

    @Bean()
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(20);
        return taskScheduler;
    }

}
