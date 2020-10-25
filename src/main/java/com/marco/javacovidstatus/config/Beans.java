package com.marco.javacovidstatus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import com.marco.javacovidstatus.services.implementations.MarcoNationalDataService;
import com.marco.javacovidstatus.services.interfaces.NationalDataService;

@Configuration
public class Beans {

	@Bean
	public WebClient getWebClient() {
		return WebClient.builder().build();
	}
	
	@Bean
	public NationalDataService getNationalDataService() {
	    return new MarcoNationalDataService()
;	}
	
}
