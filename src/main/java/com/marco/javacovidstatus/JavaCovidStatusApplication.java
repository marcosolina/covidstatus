package com.marco.javacovidstatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.marco.javacovidstatus.services.interfaces.CovidScheduler;

@SpringBootApplication
public class JavaCovidStatusApplication {

    @Autowired
    private CovidScheduler scheduler;
    
	public static void main(String[] args) {
		SpringApplication.run(JavaCovidStatusApplication.class, args);
	}
	
	@Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> scheduler.downloadNewData();
    }
}
