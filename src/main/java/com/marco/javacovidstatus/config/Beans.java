package com.marco.javacovidstatus.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.reactive.function.client.WebClient;

import com.marco.javacovidstatus.repositories.sql.CovidRepository;
import com.marco.javacovidstatus.repositories.sql.MarcoCovidRepository;
import com.marco.javacovidstatus.services.implementations.EmailNotificationSender;
import com.marco.javacovidstatus.services.implementations.MarcoNationalDataService;
import com.marco.javacovidstatus.services.interfaces.CovidDataService;
import com.marco.javacovidstatus.services.interfaces.NotificationSenderInterface;

@Configuration
public class Beans {

    @Bean
    public WebClient getWebClient() {
        return WebClient.builder().build();
    }

    @Bean
    public CovidDataService getNationalDataService() {
        return new MarcoNationalDataService();
    }

    @Bean()
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(20);
        return taskScheduler;
    }
    
    @Bean
    public CovidRepository getCovidRepository() {
        return new MarcoCovidRepository();
    }
    
    @Bean
    public JavaMailSender getJavaMailSender() {
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

}
