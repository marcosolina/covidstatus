package com.marco.javacovidstatus.services.implementations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.marco.javacovidstatus.model.DailyData;
import com.marco.javacovidstatus.services.interfaces.GovermentDataRetrieverScheduler;
import com.marco.javacovidstatus.services.interfaces.NationalDataService;

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
	private NationalDataService dataService;
	
	private String url = "https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-andamento-nazionale/dpc-covid19-ita-andamento-nazionale-%s.csv";
	
	
	@Scheduled(cron = "0 40 18 * * *")
	@Override
	public void updateNationalData() {
		
		logger.debug("Clear the DB");
		dataService.deleteAllData();//TODO optimise
		
		LocalDate start = LocalDate.of(2020, 2, 24);
		LocalDate end = LocalDate.now();
		List<Integer> lastWeeknNewInfection = new ArrayList<>(); 
		while(start.isBefore(end)) {
			start = start.plusDays(1);
			/*
			 * Retrieve the data of today and yesterday
			 */
			ItalianNationalData precedente = getDatiAllaData(start.minusDays(1));
			ItalianNationalData corrente = getDatiAllaData(start);
			
			/*
			 * Do some math
			 */
			BigDecimal infectionPercentage = BigDecimal.valueOf(percentualeInfetti(corrente, precedente)).setScale(2, RoundingMode.DOWN);
			
			/*
			 * Store the data
			 */
			DailyData dto = new DailyData();
			dto.setDate(start);
			dto.setInfectionPercentage(infectionPercentage);
			dto.setNewCasualties(corrente.newCasualties - precedente.newCasualties);
			dto.setNewInfections(corrente.newInfections - precedente.newInfections);
			dto.setNewTests(corrente.newTests - precedente.newTests);
			
			lastWeeknNewInfection.add(dto.getNewInfections());
			
			/*
			 * Do some math
			 */
			BigDecimal casualtiesPercentage = BigDecimal.valueOf(percentualeMorti(dto.getNewCasualties(), lastWeeknNewInfection.get(0))).setScale(2, RoundingMode.DOWN);
			dto.setCaualtiesPercentage(casualtiesPercentage);
			
			if(lastWeeknNewInfection.size() > 7) {
			    lastWeeknNewInfection.remove(0);
			}
			
			/*
			 * Store the info
			 */
			dataService.storeData(dto);
			logger.debug(String.format("Inserted data for date: %s", start.toString()));
		}
	}
	
	

	
	private ItalianNationalData getDatiAllaData(LocalDate data) {
		logger.trace("Inside CronServiceGit.getDatiAllaData");
		
		/*
		 * Inserting the date into the URL
		 */
		String uriFile = String.format(url, data.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
		
		/*
		 * Get the CSV file using an GET HTTP call
		 */
		ClientResponse response = webClient.get().uri(uriFile).exchange().block();
		
		/*
		 * Read the response as a string 
		 */
		String csv = response.bodyToMono(String.class).block();
		
		/*
		 * Parsing the string
		 */
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

	/**
	 * @see <a href="https://www.focus.it/scienza/salute/tasso-di-mortalita-covid-19">Algorithm</a>
	 * @param newCasualties
	 * @param newCasesFrom7DaysAgo
	 * @return
	 */
	private float percentualeMorti(int newCasualties, int newCasesFrom7DaysAgo){
	    return ((float)newCasualties / newCasesFrom7DaysAgo) * 100;
	}
	
	private class ItalianNationalData {
		int newInfections;
		int newTests;
		int newCasualties;
	}

}
