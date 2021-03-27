package com.marco.javacovidstatus.config;

import java.time.LocalDate;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.Ordered;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.classmate.TypeResolver;
import com.marco.javacovidstatus.repositories.implementations.CovidRepositoryPostgres;
import com.marco.javacovidstatus.repositories.implementations.GivenVaccinesRepoPostgres;
import com.marco.javacovidstatus.repositories.implementations.VeccinesDeliveredRepoPostgres;
import com.marco.javacovidstatus.repositories.interfaces.CovidRepository;
import com.marco.javacovidstatus.repositories.interfaces.GivenVaccinesRepo;
import com.marco.javacovidstatus.repositories.interfaces.VeccinesDeliveredRepo;
import com.marco.javacovidstatus.services.implementations.EmailNotificationSender;
import com.marco.javacovidstatus.services.implementations.MarcoNationalDataService;
import com.marco.javacovidstatus.services.implementations.NationalCovidDataDownloader;
import com.marco.javacovidstatus.services.implementations.ProvinceCoviddataDownloader;
import com.marco.javacovidstatus.services.implementations.RegionCovidDataDownloader;
import com.marco.javacovidstatus.services.implementations.RegionMapDownloaderFromNationalWebSite;
import com.marco.javacovidstatus.services.implementations.VaccineDataServiceMarco;
import com.marco.javacovidstatus.services.implementations.VaccinesDeliveredDownloader;
import com.marco.javacovidstatus.services.implementations.VaccinesGivenDownloader;
import com.marco.javacovidstatus.services.interfaces.CovidDataDownloader;
import com.marco.javacovidstatus.services.interfaces.CovidDataService;
import com.marco.javacovidstatus.services.interfaces.NotificationSenderInterface;
import com.marco.javacovidstatus.services.interfaces.RegionMapDownloader;
import com.marco.javacovidstatus.services.interfaces.VaccineDataService;
import com.marco.javacovidstatus.utils.CovidUtils;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Standard Springboot configuration class
 * 
 * @author Marco
 *
 */
@Configuration
public class Beans {

	@Value("${covidstatus.version}")
	private String appVersion;
	@Value("${spring.mail.username}")
	private String emailUser;
	@Value("${spring.mail.password}")
	private String emailPassw;
	
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
		return new CovidRepositoryPostgres();
	}

	@Bean
	public VeccinesDeliveredRepo getVeccinesDeliveredRepo() {
		return new VeccinesDeliveredRepoPostgres();
	}

	@Bean
	public GivenVaccinesRepo getGivenVaccinesRepo() {
		return new GivenVaccinesRepoPostgres();
	}

	@Bean
	public VaccineDataService getVaccineDateService() {
		return new VaccineDataServiceMarco();
	}

	@Bean
	public JavaMailSender getJavaMailSender() {
		/*
		 * Send email using your GMAIL account
		 */
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost("smtp.gmail.com");
		mailSender.setPort(587);

		mailSender.setUsername(emailUser);
		mailSender.setPassword(emailPassw);

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

	@Bean
	public CovidUtils getCovidUtils() {
		return new CovidUtils();
	}

	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasenames("classpath:/messages/errorCodes");

		return messageSource;
	}
	
	 @Bean
    public Docket swaggerConfiguration() {
		 TypeResolver resolver = new TypeResolver();
		 return new Docket(DocumentationType.SWAGGER_2)
			 .alternateTypeRules( AlternateTypeRules.newRule(
					 resolver.resolve(List.class, LocalDate.class),
					 resolver.resolve(List.class, String.class), Ordered.HIGHEST_PRECEDENCE))
        	.select()
            .apis(RequestHandlerSelectors.basePackage("com.marco.javacovidstatus.controllers"))
            .paths(PathSelectors.regex("/.*"))
            .build().apiInfo(apiEndPointsInfo());
    }
    
    
    private ApiInfo apiEndPointsInfo() {
        return new ApiInfoBuilder().title("Covid 19 Italy")
            .description("I have created this project to collect the Covid 19 data related to the Italian situation and display them into multiple charts")
            .contact(new Contact("Marco Solina", "", ""))
            .version(appVersion)
            .build();
    }

}
