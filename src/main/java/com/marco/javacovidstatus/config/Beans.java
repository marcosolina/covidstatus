package com.marco.javacovidstatus.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.marco.javacovidstatus.repositories.implementations.CovidRepositoryMarco;
import com.marco.javacovidstatus.repositories.implementations.GivenVaccinesRepoMarco;
import com.marco.javacovidstatus.repositories.implementations.VeccinesDeliveredRepoMarco;
import com.marco.javacovidstatus.repositories.interfaces.CovidRepository;
import com.marco.javacovidstatus.repositories.interfaces.GivenVaccinesRepo;
import com.marco.javacovidstatus.repositories.interfaces.VeccinesDeliveredRepo;
import com.marco.javacovidstatus.services.implementations.EmailNotificationSender;
import com.marco.javacovidstatus.services.implementations.MarcoNationalDataService;
import com.marco.javacovidstatus.services.implementations.NationalCovidDataDownloader;
import com.marco.javacovidstatus.services.implementations.ProvinceCoviddataDownloader;
import com.marco.javacovidstatus.services.implementations.RegionCovidDataDownloader;
import com.marco.javacovidstatus.services.implementations.RegionMapDownloaderFromNationalWebSite;
import com.marco.javacovidstatus.services.implementations.VaccinesDeliveredDownloader;
import com.marco.javacovidstatus.services.implementations.VaccinesGivenDownloader;
import com.marco.javacovidstatus.services.interfaces.CovidDataDownloader;
import com.marco.javacovidstatus.services.interfaces.CovidDataService;
import com.marco.javacovidstatus.services.interfaces.NotificationSenderInterface;
import com.marco.javacovidstatus.services.interfaces.RegionMapDownloader;

/**
 * Standard Springboot configuration class
 * 
 * @author Marco
 *
 */
@Configuration
public class Beans {

    @Bean
    public WebClient getWebClient() {
    	// @formatter:off
    	int megaByteNumber = 50;
        return WebClient.builder()
        		.exchangeStrategies(
        			ExchangeStrategies.builder()
        				.codecs(configurer -> 
        					configurer.defaultCodecs().maxInMemorySize(megaByteNumber * 1024 *1024)
        				).build()
        		)
        		.build();
        // @formatter:on
    }

    @Bean
    public CovidDataService getNationalDataService() {
        return new MarcoNationalDataService();
    }

    @Bean()
    public ThreadPoolTaskScheduler taskScheduler() {
        /*
         * Setting a thread pool, so the scheduler can run in a different thread
         */
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(20);
        return taskScheduler;
    }

    @Bean
    public CovidRepository getCovidRepository() {
        return new CovidRepositoryMarco();
    }
    
    @Bean
    public VeccinesDeliveredRepo getVeccinesDeliveredRepo() {
    	return new VeccinesDeliveredRepoMarco();
    }
    
    @Bean
    public GivenVaccinesRepo getGivenVaccinesRepo() {
    	return new GivenVaccinesRepoMarco();
    }

    @Bean
    public JavaMailSender getJavaMailSender() {
        /*
         * Send email using your GMAIL account
         */
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername("XXXX");
        mailSender.setPassword("XXXX");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

    @Bean
    public NotificationSenderInterface getNotificationSenderInterface() {
        return new EmailNotificationSender();
    }

    @Bean(name = "National")
    public CovidDataDownloader getNationalCovidDataDownloader() {
        return new NationalCovidDataDownloader(getWebClient());
    }

    @Bean(name = "Province")
    public CovidDataDownloader getProvinceCoviddataDownloader() {
        return new ProvinceCoviddataDownloader(getWebClient());
    }

    @Bean(name = "Region")
    public CovidDataDownloader getRegionCovidDataDownloader() {
        return new RegionCovidDataDownloader(getWebClient());
    }
    
    @Bean(name = "GivenVaccines")
    public CovidDataDownloader getGivenVaccinesDownloader() {
    	return new VaccinesGivenDownloader(getWebClient());
    }
    
    @Bean(name = "DeliveredVaccines")
    public CovidDataDownloader getDeliveredVaccinesDownloader() {
    	return new VaccinesDeliveredDownloader(getWebClient());
    }
    
    @Bean
    public RegionMapDownloader getRegionMapDownloader() {
    	return new RegionMapDownloaderFromNationalWebSite();
    }

}
