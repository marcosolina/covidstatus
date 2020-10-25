package com.marco.javacovidstatus.services.implementations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.marco.javacovidstatus.repositories.model.EntityNationalData;
import com.marco.javacovidstatus.repositories.sql.NationallDataSqlRepository;
import com.marco.javacovidstatus.services.interfaces.GovermentDataRetrieverScheduler;

/**
 * This implementations uses the Italian national data
 * @author Marco
 *
 */
@Configuration
@EnableScheduling
public class ItalianNationalDataScheduler implements GovermentDataRetrieverScheduler{
	private static final Logger logger = LoggerFactory.getLogger(ItalianNationalDataScheduler.class);	
	
	@Autowired
	private WebClient webClient;
	@Autowired
	private NationallDataSqlRepository repository;
	
	private String url = "https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-andamento-nazionale/dpc-covid19-ita-andamento-nazionale-%s.csv";
	
	
	@Scheduled(cron = "0 10 18 * * *")
	@Override
	public void updateNationalData() {
		
		logger.debug("Clear the DB");
		repository.deleteAll();
		
		LocalDate start = LocalDate.of(2020, 2, 24);
		LocalDate end = LocalDate.now();
		while(start.isBefore(end)) {
			start = start.plusDays(1);
			ItalianNationalData precedente = getDatiAllaData(start.minusDays(1));
			ItalianNationalData corrente = getDatiAllaData(start);
			
			BigDecimal result = BigDecimal.valueOf(percentualeInfetti(corrente, precedente)).setScale(2, RoundingMode.DOWN);
			
			EntityNationalData dto = new EntityNationalData();
			dto.setDate(start);
			dto.setInfectionPercentage(result.floatValue());
			dto.setNewCasualties(corrente.newCasualties - precedente.newCasualties);
			dto.setNewInfections(corrente.newInfections - precedente.newInfections);
			dto.setNewTests(corrente.newTests - precedente.newTests);
			
			repository.save(dto);
			
			logger.debug(String.format("Inserted data for date: %s", start.toString()));
		}
	}
	
	

	
	private ItalianNationalData getDatiAllaData(LocalDate data) {
		logger.trace("Inside CronServiceGit.getDatiAllaData");
		
		String uriFile = String.format(url, data.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
		ClientResponse response = webClient.get().uri(uriFile).exchange().block();
		
		String csv = response.bodyToMono(String.class).block();
		String [] tmp = csv.split("\\n");
		String [] values = tmp[1].split(",");
		
		
		ItalianNationalData dati = new ItalianNationalData();
		dati.newCasualties = Integer.parseInt(values[10]);
		dati.newTests = Integer.parseInt(values[14]);
		dati.newInfections = Integer.parseInt(values[13]);
		
		return dati;
	}
	
	
	private float percentualeInfetti(ItalianNationalData corrente, ItalianNationalData precedente) {
		int deltaTests = corrente.newTests - precedente.newTests;
		int deltaInfections = corrente.newInfections - precedente.newInfections;
		return ((float)deltaInfections / deltaTests) * 100;
	}
	
	private class ItalianNationalData {
		int newInfections;
		int newTests;
		int newCasualties;
	}

}
