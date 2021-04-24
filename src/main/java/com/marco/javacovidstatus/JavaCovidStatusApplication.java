package com.marco.javacovidstatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.marco.javacovidstatus.services.interfaces.CovidScheduler;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class JavaCovidStatusApplication {

    @Autowired
    private CovidScheduler scheduler;
    
	public static void main(String[] args) {
		SpringApplication.run(JavaCovidStatusApplication.class, args);
	}
	
	/**
	 * Execute some tasks at startup
	 * @param ctx
	 * @return
	@Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                scheduler.downloadIstatData();
                scheduler.downloadNewData();
            }
        };
    }
	 */
}
